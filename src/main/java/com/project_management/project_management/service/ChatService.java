package com.project_management.project_management.service;

import com.project_management.project_management.Dtos.project.chat.ReceiveMessageDTO;
import com.project_management.project_management.Dtos.project.chat.SendMessageDTO;
import com.project_management.project_management.model.ChatMessage;
import com.project_management.project_management.model.Project;
import com.project_management.project_management.model.User;
import com.project_management.project_management.repository.ChatRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ProjectService projectService;
    private final UserService userService;

    public ChatService(final ChatRepository chatRepository, final SimpMessagingTemplate simpMessagingTemplate,
                       final ProjectService projectService, final UserService userService){
        this.chatRepository = chatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.projectService = projectService;
        this.userService = userService;
    }

    public void saveAndSendMessage(SendMessageDTO sendMessageDTO)  {
      User sender = userService.getUserById(sendMessageDTO.getSender_id());
      User receiver = userService.getUserById(sendMessageDTO.getReceiver_id());
      Project project = projectService.getProjectById(sendMessageDTO.getProject_id());

        ChatMessage chatMessage = ChatMessage.builder()
                .project(project)
                .sender(sender)
                .receiver(receiver)
                .message(sendMessageDTO.getMessage())
                .sentAt(Instant.now())
                .build();

       chatMessage = chatRepository.save(chatMessage);
       ReceiveMessageDTO receiveMessageDTO = new ReceiveMessageDTO();

       receiveMessageDTO.setMessage(chatMessage.getMessage());
       receiveMessageDTO.setSender_id(chatMessage.getSender().getId());
       receiveMessageDTO.setSender_name(chatMessage.getSender().getName());
       receiveMessageDTO.setSender_job_title(chatMessage.getSender().getTitle());
       receiveMessageDTO.setSentAt(chatMessage.getSentAt());
       receiveMessageDTO.setSender_profile_pic(chatMessage.getSender().getProfile_pic());

       String destinationUrl = "/queue/project/"+chatMessage.getProject().getProject_id()+"/messages";
       simpMessagingTemplate.convertAndSendToUser(chatMessage.getReceiver().getId(), destinationUrl, receiveMessageDTO);
    }
}
