package com.project_management.project_management.service;

import com.cloudinary.Cloudinary;
import com.project_management.project_management.model.Attachment;
import com.project_management.project_management.model.Task;
import com.project_management.project_management.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AttachmentService {
    private final Cloudinary cloudinary;
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(final Cloudinary cloudinary, final AttachmentRepository attachmentRepository){
        this.cloudinary = cloudinary;
        this.attachmentRepository = attachmentRepository;
    }
    public List<Attachment> uploadTaskAttachmentsToCloud(List<MultipartFile> attachments, final Task task) throws IOException {
     List<Attachment> uploadedAttachments = new ArrayList<>();

     for (MultipartFile attachment : attachments) {
       Map image_result =  cloudinary.uploader().upload(attachment.getBytes(),
                 Map.of("folder", "task_attachment"));
      String img_url = (String) image_result.get("secure_url");
      String img_format = (String) image_result.get("format"); // png, jpg, jpeg, pdf
      String resource_type = (String) image_result.get("resource_type"); // image, pdf
         // Create an Attachment Entity object
        Attachment buildedAttachment = Attachment.builder()
                 .attachment_format(img_format)
                 .attachment_name(attachment.getName())
                 .attachment_type(resource_type)
                 .attachment_url(img_url)
                 .uploadedOn(Instant.now())
                 .attachment_Owner(task)
                 .build();

        uploadedAttachments.add(buildedAttachment);
     }
     return uploadedAttachments;
    }
    public long findNumberOfAttachmentOfATaskById(String task_id){
        return attachmentRepository.countByTaskId(task_id);
    }
}
