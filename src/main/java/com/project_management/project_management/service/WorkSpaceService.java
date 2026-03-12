package com.project_management.project_management.service;

import com.project_management.project_management.Dtos.workspace.CreateWorkSpaceDTO;
import com.project_management.project_management.Dtos.workspace.InvitationDTO;
import com.project_management.project_management.Dtos.workspace.UpdateWorkSpace;
import com.project_management.project_management.enums.Plan_Enums.plan;
import com.project_management.project_management.enums.User_Enums.Role;
import com.project_management.project_management.enums.WorkSpace_Enums.WorkSpaceJoin_Status;
import com.project_management.project_management.exception.Token.TokenExpired;
import com.project_management.project_management.exception.user.UserNotFound;
import com.project_management.project_management.exception.workspace.*;
import com.project_management.project_management.model.Invitation;
import com.project_management.project_management.model.Subscription;
import com.project_management.project_management.model.User;
import com.project_management.project_management.model.WorkSpace;
import com.project_management.project_management.repository.InvitationRepo;
import com.project_management.project_management.repository.WorkSpaceRepository;
import com.project_management.project_management.service.email.workspace.WorkSpaceEmailService;
import com.project_management.project_management.util.UserUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class WorkSpaceService {
    private final WorkSpaceRepository workSpaceRepository;
    private final static String WORKSPACE_DUMMY_LOGO = "https://lh3.googleusercontent.com/rd-gg/AMW1TPp9FpvxAWqgyfXtrPvlLPcAijdLTx8hi2FiICCGcpPH-FDgrPWiL7-rlntvbxTtGbbScfrW1srqh02Emhh7-NdN-pBdt6xPff3eLuTnt_tFPSPz1CcbAUlmXDCs8jN79uE9GH9Qe1s34PmP6Bu8VPzvjBv5VSKbBMXa-XmQgmORBAJ8903VyQFfPmZhujI0j5st4bW10MoJt4E3BrZLAAxESe8S7gQe3Hz-zMEZlNmREh3BSacSDKWbSSdGtwyvyxDVKa2pYD5MVxUG5HmHGLcDfgv8xt1cpuyXd-httvqSxnjiEMPVTxBnJm9D2uxFu12I_ElBLleA0sGNi8154hYJtN-8VJxYJpms_YrW8CQ_SOgXF5XmKQ7eFVjuVeB5nHwsWmAE6vVAR2zx9G_Q93nk8F59z5pR7wrPBzJp6UBeUdOGORadZ8gFDNv_t-FKZQgHx16t40uAx_id_LNbvoLqNipehKbFAv_NtG10UHK_UxRhWT-O7r8h7pyxK19Mharb5SONPTsGO1D-pTDi9BGLgopzneGIQo5ReszkAstPcvYct_3AUVf0rzmPszD6J-Nzg4gs33lcTG7BQA6unXk5r_uGNez5ZnK_iHkMCHMirSEFtQbKTNjTH1moeRxlITN50o8qiCq5Wb8AhgZXdLlmLMmyws5lV7jWzoJ3g8alxtNrtDvqTo931yNiovNnhQOD2-AEULv5SUFafzmCrQIlyZecP9p0aiGDWwo1jQPG4bUqT05OdBP0HXM1cz4yFSvDRzIbxWauZawxSdi8NodD3FD7oSTvaEVhyQqVmoH2Z1tghH4evgaww31L3ejK8Qy4nIDBVCLdO7zmM968uraoncYH7yLsm0q3WkIBhFNHcXrsdQ9DVto7cA3tD0rJF2uvcMtBZuv7qU71vTnjnbznI0UhyB4ysE-EQAjfR3ZFa21KHeUas45ILdI1PdFUhn6tNHPsOvX9FrtW5a88CZCvliilvz3ui2iWa6qQ8LCaTK7pWitFcNkdeBzjdjUoUoaiiZxSmMOIewBTbDIi1zAlS3y7atksA3d9HPDtoRmErLg7u2VxDAxrjClxrCq2hbCE5792faFeOM4WV1tvNluZWohVgzpXZmAfokCNVx8uc9r0sbPD3CaZA1ZMkLt6bmSvsay79789kNPM9DS0stVcFG7UdQAyDsZXffiI73_TaqarkSVfJxTs7w0yGNRx8DpQZt6OI-ONml3aWianxzF3zTz9nWwT1BzEBRslAal8d8le6YbaQno96iQezF_L7Upqwo1gPV_gy4LPZWnPyAA1OPz0KUkdwMSmbylueYiUyyjrCAeA3w9QYiht5Ek5m3jyeBCItDbRZj9drvTq0id2F7xQS53lpU5bVjf7UI0dIiRS-xyAbu0h9CfQL8PxsmkoxSljPZb0E0z9LjonmIWBAyqBNKEP8b78uaMSbTFEZli-AKs3YMV34NY=s1024-rj";
    private final WorkSpaceEmailService workSpaceEmailService;
    private final InvitationRepo invitationRepo;
    private final InvitationService invitationService;
    private final AuthService authService;
    @Autowired
    public WorkSpaceService(final WorkSpaceRepository workSpaceRepository,
                            final WorkSpaceEmailService workSpaceEmailService,
                            final InvitationRepo invitationRepo, final InvitationService invitationService,
                            final AuthService authService){
        this.workSpaceRepository = workSpaceRepository;
        this.workSpaceEmailService = workSpaceEmailService;
        this.invitationRepo = invitationRepo;
        this.invitationService = invitationService;
        this.authService = authService;
    }

    public void createWorkSpace(CreateWorkSpaceDTO createWorkSpaceDTO) throws MaximumWorkSpaceCreationLimitReached {
     User currentUser = UserUtil.getCurrentUser();
     if(currentUser.getRole().equals(Role.OWNER)){
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
                     .key(UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                     .build();

             workSpaceRepository.save(workSpace);
     }
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

    public void inviteUserToWorkSpace(InvitationDTO invitationDTO) throws WorkSpaceNotFound, MessagingException, WorkSpaceIsLocked, UserNotFound, UserHasAlreadyJoinedTheWorkSpace, MaximumWorkSpaceEmployeesLimitHasBeenReached {
       // send invitation email to user
       WorkSpace workSpace = workSpaceRepository.findOneByKey(invitationDTO.workspace_key())
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
        if(authService.existByEmail(invitationDTO.userToBeInvitedEmail())){
            User userToBeJoined = authService.getUserByEmail(invitationDTO.userToBeInvitedEmail());
            if(workSpace.getWorkspace_employees().contains(userToBeJoined)){
                throw new UserHasAlreadyJoinedTheWorkSpace("This user is already in your workspace");
            }
        }
       Invitation invitation = Invitation.builder()
                .invitedToWorkspace(workSpace)
                .link(UUID.randomUUID().toString().substring(0,10))
                .status(WorkSpaceJoin_Status.PENDING)
                .email(invitationDTO.userToBeInvitedEmail())
                .expiresOn(LocalDateTime.now(ZoneOffset.UTC).plusDays(1))
                .build();

        invitationRepo.save(invitation);
        workSpaceEmailService.sendWorkSpaceJoinInvitationEmail(invitation);
    }

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public void joinWorkSpaceFromInvitationLink(String work_space_key, String userToBeJoinedEmail, String invitation_link) throws WorkSpaceInvitationLinkNotFound, TokenExpired, WorkSpaceNotFound, UserNotFound, MaximumWorkSpaceEmployeesLimitHasBeenReached, UserHasAlreadyJoinedTheWorkSpace {
       Invitation invitationRequest = invitationService.getInvitationRequestByLink(invitation_link);

       if(invitationRequest.getExpiresOn().isBefore(LocalDateTime.now(ZoneOffset.UTC))){
           throw new TokenExpired("Request link has been expired. Please ask workspace owner to send new invitation request");
       }

       // Workspace where user will be added to
       WorkSpace workSpaceToBeJoined = workSpaceRepository.findOneByKey(work_space_key)
               .orElseThrow(() -> new WorkSpaceNotFound("Invalid workspace key. Workspace not found. Try again"));

        // current workspace joined employees list
        List<User> workspace_employees = workSpaceToBeJoined.getWorkspace_employees();

       // User which will join the workspace
       User workSpaceJoiningUser = authService.getUserByEmail(userToBeJoinedEmail);

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
}
