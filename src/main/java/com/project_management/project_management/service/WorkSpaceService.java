package com.project_management.project_management.service;

import com.project_management.project_management.Dtos.workspace.CreateWorkSpaceDTO;
import com.project_management.project_management.Dtos.workspace.InvitationDTO;
import com.project_management.project_management.Dtos.workspace.UpdateWorkSpace;
import com.project_management.project_management.enums.Plan_Enums.plan;
import com.project_management.project_management.event.JoinInvitationEvent;
import com.project_management.project_management.exception.Token.TokenExpired;
import com.project_management.project_management.exception.user.UserNotFound;
import com.project_management.project_management.exception.workspace.*;
import com.project_management.project_management.model.Invitation;
import com.project_management.project_management.model.Subscription;
import com.project_management.project_management.model.User;
import com.project_management.project_management.model.WorkSpace;
import com.project_management.project_management.repository.InvitationRepo;
import com.project_management.project_management.repository.WorkSpaceRepository;
import com.project_management.project_management.util.UserUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;
    private final static String WORKSPACE_DUMMY_LOGO = "https://res.cloudinary.com/djecydjrh/image/upload/v1774009713/task_attachment/fgz0zgmjtt7s6wwqrli6.png";
    private final InvitationRepo invitationRepo;
    private final InvitationService invitationService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    public WorkSpaceService(final WorkSpaceRepository workSpaceRepository,
                            final InvitationRepo invitationRepo,
                            final InvitationService invitationService,
                            final UserService userService,
                            final ApplicationEventPublisher applicationEventPublisher
    ){
        this.workSpaceRepository = workSpaceRepository;
        this.invitationRepo = invitationRepo;
        this.invitationService = invitationService;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void createWorkSpace(CreateWorkSpaceDTO createWorkSpaceDTO) throws MaximumWorkSpaceCreationLimitReached {
     User currentUser = UserUtil.getCurrentUser();
      Subscription userCurrentSubscription = currentUser.getSubscription();
         currentUser.setMyWorkSpaces(workSpaceRepository.findByOwner(currentUser));
         if(userCurrentSubscription.getPlan().getPlanName().equals(plan.BASIC)){
             if(currentUser.getMyWorkSpaces().size() >= userCurrentSubscription.getPlan().getMax_work_space()){
                 throw new MaximumWorkSpaceCreationLimitReached("Your maximum workspace creation limit has been reached. Please upgrade to premium plan to create more workspace");
             }
         }
             WorkSpace workSpace = WorkSpace.builder()
                     .logo(createWorkSpaceDTO.logo() == null ? WORKSPACE_DUMMY_LOGO : "")
                     .title(createWorkSpaceDTO.title())
                     .description(createWorkSpaceDTO.description())
                     .isLocked(false)
                     .createdOn(LocalDateTime.now(ZoneOffset.UTC))
                     .last_updated(LocalDateTime.now(ZoneOffset.UTC))
                     .owner(currentUser)
                     .key("#"+UUID.randomUUID().toString().substring(0, 12).replace("-", "").toUpperCase())
                     .build();

             workSpaceRepository.save(workSpace);
    }
    public void deleteWorkSpace(String workspace_key){
        workSpaceRepository.deleteByKey(workspace_key);
    }

    public void updateWorkSpace(UpdateWorkSpace updateWorkSpace) throws WorkSpaceNotFound, WorkSpaceIsLocked {
       WorkSpace workSpace = workSpaceRepository.findOneByKey(updateWorkSpace.workspace_key())
                .orElseThrow(() -> new WorkSpaceNotFound("Workspace not found. May be it doesn't exist or try again later"));
       if(!workSpace.isLocked()) {
           workSpace.setTitle(updateWorkSpace.title());
           workSpace.setDescription(updateWorkSpace.description());
           workSpace.setLogo(updateWorkSpace.logo() == null ? workSpace.getLogo() : updateWorkSpace.logo().toString());
           workSpaceRepository.save(workSpace);
       } else throw new WorkSpaceIsLocked("You cannot update this workspace, because this workspace is locked.");
    }

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public void inviteUserToWorkSpace(InvitationDTO invitationDTO) throws WorkSpaceNotFound, MessagingException, WorkSpaceIsLocked, UserNotFound, UserHasAlreadyJoinedTheWorkSpace, MaximumWorkSpaceEmployeesLimitHasBeenReached {
       WorkSpace workSpace = workSpaceRepository.findWithJoinedEmployees(invitationDTO.workspace_key())
        .orElseThrow(() -> new WorkSpaceNotFound("Invalid key. Workspace not found"));

        if(workSpace.isLocked()){
            throw new WorkSpaceIsLocked("This workspace is locked, you can't invite new users to this workspace. Please subscribe to our premium plan to invite more users to this workspace");
        }
        User workSpaceOwner = workSpace.getOwner();

        if(workSpaceOwner.getSubscription().getPlan().getPlanName() == plan.BASIC) {
            if (workSpace.getWorkspace_employees().size() >= workSpaceOwner.getSubscription().getPlan().getMax_members_per_workspace()) {
                throw new MaximumWorkSpaceEmployeesLimitHasBeenReached("Your limit of inviting users to workspace has been reached. Please upgrade to add more users to your workspace.");
            }
        }
        if(userService.existByEmail(invitationDTO.userToBeInvitedEmail())){
            User userToBeJoined = userService.getUserByEmail(invitationDTO.userToBeInvitedEmail());
            if(workSpace.getWorkspace_employees().contains(userToBeJoined)){
                throw new UserHasAlreadyJoinedTheWorkSpace("This user is already in your workspace");
            }
        }
        Invitation invitation = invitationService.createInvitation(invitationDTO.userToBeInvitedEmail(), workSpace);

        invitation = invitationRepo.save(invitation);
        applicationEventPublisher.publishEvent(new JoinInvitationEvent(invitation));
        // workSpaceEmailService.sendWorkSpaceJoinInvitationEmail(invitation);
    }

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public void joinWorkSpaceFromInvitationLink(String work_space_key, String userToBeJoinedEmail, String invitation_link) throws WorkSpaceInvitationLinkNotFound, TokenExpired, WorkSpaceNotFound, UserNotFound, MaximumWorkSpaceEmployeesLimitHasBeenReached, UserHasAlreadyJoinedTheWorkSpace {
       Invitation invitationRequest = invitationService.getInvitationRequestByLink(invitation_link);

       if(invitationRequest.getExpiresOn().isBefore(LocalDateTime.now(ZoneOffset.UTC))){
           throw new TokenExpired("Request link has been expired. Please ask workspace owner to send new invitation request");
       }

       // Workspace where user will be added to
       WorkSpace workSpaceToBeJoined = workSpaceRepository.findWithJoinedEmployees(work_space_key)
               .orElseThrow(() -> new WorkSpaceNotFound("Invalid workspace key. Workspace not found. Try again"));

        // current workspace joined employees list
        List<User> workspace_employees = workSpaceToBeJoined.getWorkspace_employees();

       // User which will join the workspace
       User workSpaceJoiningUser = userService.getUserByEmail(userToBeJoinedEmail);

        if (workspace_employees.contains(workSpaceJoiningUser)) {
          throw new UserHasAlreadyJoinedTheWorkSpace("You have already joined this workspace");
        }

       // workspace owner
       User workspaceOwner = workSpaceToBeJoined.getOwner();

       if(workspaceOwner.getSubscription().getPlan().getPlanName() == plan.BASIC) {
           if (workSpaceToBeJoined.getWorkspace_employees().size() >= workspaceOwner.getSubscription().getPlan().getMax_members_per_workspace()) {
                throw new MaximumWorkSpaceEmployeesLimitHasBeenReached("Your limit of inviting users to workspace has been reached. Please upgrade to add more users to your workspace.");
           }
       }
       // add new user to joined employees list
       workspace_employees.add(workSpaceJoiningUser);
       workSpaceToBeJoined.setWorkspace_employees(workspace_employees);

       workSpaceRepository.save(workSpaceToBeJoined);

       // Delete invitation link from database
       invitationService.deleteInvitation(invitationRequest.getId());

    }
    public WorkSpace getWorkSpaceBy_WorkSpace_Key(String workspace_key) throws WorkSpaceNotFound {
       return workSpaceRepository.findOneByKey(workspace_key)
               .orElseThrow(() -> new WorkSpaceNotFound("Invalid workspace key. Workspace not found"));
    }

    public WorkSpace getWorkSpaceWithProjectByWorkSpaceKey(String workSpaceKey) throws WorkSpaceNotFound {
       return workSpaceRepository.findWithCreatedProjects(workSpaceKey)
               .orElseThrow(() -> new WorkSpaceNotFound("Invalid workspace key. Workspace not found"));
    }
}
