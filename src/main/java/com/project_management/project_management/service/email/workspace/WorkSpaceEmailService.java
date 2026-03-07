package com.project_management.project_management.service.email.workspace;

import com.project_management.project_management.Dtos.workspace.InvitationDTO;
import com.project_management.project_management.model.Invitation;
import com.project_management.project_management.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkSpaceEmailService {
    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;
    private final EmailService emailService;
    @Autowired
    public WorkSpaceEmailService(final JavaMailSender mailSender, final EmailService emailService){
        this.mailSender = mailSender;
        this.emailService = emailService;
    }
    private String JoinWorkSpaceInvitationBody(Invitation invitation){
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Join your team on App</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; background-color: #ffffff; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\">\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed; background-color: #ffffff;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 60px 20px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px; text-align: left;\">\n" +
                "                    \n" +
                "                    <tr>\n" +
                "                        <td style=\"padding-bottom: 40px;\">\n" +
                "                            <img src=\"cid:logo\" alt=\"Stand Logo\" style=\"width: 200px; height: 200px; display: block;\" />\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding-bottom: 25px;\">\n" +
                "                            <h1 style=\"margin: 0; color: #111111; font-size: 36px; font-weight: 800; line-height: 1.2; letter-spacing: -0.5px;\">\n" +
                "                                You’ve been invited to join "+invitation.getInvitedToWorkspace().getTitle()+" workspace on <span style=\"color: #7047eb;\">App.</span>\n" +
                "                            </h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding-bottom: 35px;\">\n" +
                "                            <p style=\"margin: 0; color: #4b5563; font-size: 18px; line-height: 1.6;\">\n" +
                "                                Let’s use Stand to complete daily standups, please complete standups at the start of your working day.\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding-bottom: 40px;\">\n" +
                "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\" bgcolor=\"#7047eb\" style=\"border-radius: 12px;\">\n" +
                "                                        <a href="+invitation.getLink()+" target=\"_blank\" style=\"display: inline-block; padding: 18px 45px; font-size: 18px; font-weight: 600; color: #ffffff; text-decoration: none;\">\n" +
                "                                            Join Now\n" +
                "                                        </a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding-top: 20px; border-top: 1px solid #f3f4f6;\">\n" +
                "                            <p style=\"margin: 0; color: #9ca3af; font-size: 14px; line-height: 1.5;\">\n" +
                "                                <strong>Note:</strong> This invitation link will expire in 24 hours for security purposes. If you miss this window, please reach out to your team lead to request a new invitation.\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                </table>\n" +
                "                \n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"left\" style=\"padding-top: 40px; color: #9ca3af; font-size: 12px;\">\n" +
                "                            &copy; 2026 App Inc. All rights reserved.\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";
    }
    public void sendWorkSpaceJoinInvitationEmail(Invitation invitation) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.png");
        sendEmail(invitation.getEmail(), "Join workspace invitation from "+invitation.getInvitedToWorkspace().getTitle(), JoinWorkSpaceInvitationBody(invitation), classPathResource, "logo");
        log.info("Join workspace email invitation sent successfully!");
    }

    private void sendEmail(String toEmail, String subject, String body, ClassPathResource classPathResource, String imgName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setText(body, true);
        helper.setFrom(from);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.addInline(imgName, classPathResource);

        mailSender.send(mimeMessage);
    }
}
