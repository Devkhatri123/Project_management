package com.project_management.project_management.service;

import com.project_management.project_management.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Async
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    public EmailService(final JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void registrationSuccessfulEmail(User user) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.png");
        sendEmail(user, "Account created successfully! Here is your account verification code", emailRegistrationSuccessfulContent(user), classPathResource, "logo");
        log.info("Registration successful notification sent successfully!");
    }
    public void resendVerificationEmail(User user) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.png");
        sendEmail(user, "Verification code", resendVerificationCodeBody(user), classPathResource, "logo");
        log.info("Verification code email resent successfully!");
    }
    public void ForgetPasswordLink(User user) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.png");
        sendEmail(user, "Forget Password link", forgetPasswordLink(user), classPathResource, "logo");
        log.info("Forget password link notification sent successfully!");
    }

    public void PasswordChangedSuccessfully(User user) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.png");
        sendEmail(user, "Password changed successfully!", passwordChangedSuccessfullyBody(user), classPathResource, "logo");
        log.info("Password changed notification sent successfully!");
    }

    private void sendEmail(User user, String subject, String body, ClassPathResource classPathResource, String imgName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setText(body, true);
        helper.setFrom(from);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.addInline(imgName, classPathResource);

        mailSender.send(mimeMessage);
    }

    private static String emailRegistrationSuccessfulContent(User user){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; background-color: #f9f9f9; max-width: 550px; margin-left:auto; margin-right:auto; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #333333;\">\n" +
                "\n" +
                "    <div class=\"email-container\" style=\"max-width: 550px; margin: 20px auto; background: #ffffff; border: 1px solid #eeeeee; padding: 20px; text-align: left;\">\n" +
                "        \n" +
                "        <div class=\"header-logo\" style=\"text-align: center; margin-bottom: 10px;\">\n" +
                "            <strong style=\"font-size: 24px; letter-spacing: 2px;\">\n" +
                "                <img src=\"cid:logo\" style=\"width: 200px; height: 200px; vertical-align: middle;\" />\n" +
                "            </strong>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"mainBody\" style=\"box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px, rgba(0, 0, 0, 0.08) 0px 0px 0px 1px; border-radius: 4px; overflow: hidden;\">\n" +
                "            \n" +
                "            <h1 style=\"font-size: 22px; font-weight: 700; margin-bottom: 25px; color: #000000; padding: 20px 0 0 20px;\">\n" +
                "                Verify This Email Address\n" +
                "            </h1>\n" +
                "\n" +
                "            <p style=\"font-size: 15px; line-height: 1.6; margin-bottom: 15px; padding-left: 20px;\">\n" +
                "                Hi "+user.getName()+",\n" +
                "            </p>\n" +
                "            \n" +
                "            <p style=\"font-size: 15px; line-height: 1.6; margin-bottom: 15px; padding-left: 20px;\">\n" +
                "                Welcome to App!\n" +
                "            </p>\n" +
                "\n" +
                "            <p style=\"font-size: 15px; line-height: 1.6; margin-bottom: 15px; padding-left: 20px;\">\n" +
                "                Please use the verification code below to confirm your email address.\n" +
                "            </p>\n" +
                "\n" +
                "            <div class=\"code-container\" style=\"text-align: center; margin: 30px 0; padding: 20px; border-radius: 8px;\">\n" +
                "                <p style=\"margin-bottom: 10px; font-size: 12px; color: #666; text-transform: uppercase; padding-left: 0;\">\n" +
                "                    Your Verification Code\n" +
                "                </p>\n" +
                "                <div class=\"verification-code\" style=\"font-size: 32px; font-weight: bold; letter-spacing: 8px; color: black; margin: 0;\">\n" +
                "                     "+user.getVerification().getOtpCode()+"\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"footer-signature\" style=\"margin-top: 30px; font-size: 14px; line-height: 1.4; color: #666666;\">\n" +
                "            Dev khatri<br>\n" +
                "            <strong style=\"color: #333333;\">App Support Team</strong>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }
    private static String resendVerificationCodeBody(User user){
      return "<!DOCTYPE html>\n" +
              "<html>\n" +
              "<head>\n" +
              "    <meta charset=\"UTF-8\">\n" +
              "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
              "</head>\n" +
              "<body style=\"margin: 0; padding: 0; background-color: #f9f9f9; max-width: 550px; margin-left:auto; margin-right:auto; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #333333;\">\n" +
              "\n" +
              "    <div class=\"email-container\" style=\"max-width: 550px; margin: 20px auto; background: #ffffff; border: 1px solid #eeeeee; padding: 20px; text-align: left;\">\n" +
              "        \n" +
              "        <div class=\"header-logo\" style=\"text-align: center; margin-bottom: 10px;\">\n" +
              "            <strong style=\"font-size: 24px; letter-spacing: 2px;\">\n" +
              "                <img src=\"cid:logo\" style=\"width: 200px; height: 200px; vertical-align: middle;\" />\n" +
              "            </strong>\n" +
              "        </div>\n" +
              "\n" +
              "        <div class=\"mainBody\" style=\"box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px, rgba(0, 0, 0, 0.08) 0px 0px 0px 1px; border-radius: 4px; overflow: hidden;\">\n" +
              "            \n" +
              "            <h1 style=\"font-size: 22px; font-weight: 700; margin-bottom: 25px; color: #000000; padding: 20px 0 0 20px;\">\n" +
              "                New Verification Code\n" +
              "            </h1>\n" +
              "\n" +
              "            <p style=\"font-size: 15px; line-height: 1.6; margin-bottom: 15px; padding-left: 20px; padding-right: 20px;\">\n" +
              "                Hi "+user.getName()+",\n" +
              "            </p>\n" +
              "            \n" +
              "            <p style=\"font-size: 15px; line-height: 1.6; margin-bottom: 15px; padding-left: 20px; padding-right: 20px;\">\n" +
              "                As requested, here is a new verification code to help you verify your account.\n" +
              "            </p>\n" +
              "\n" +
              "            <p style=\"font-size: 15px; line-height: 1.6; margin-bottom: 15px; padding-left: 20px; padding-right: 20px;\">\n" +
              "                Please enter this code on the verification screen to verify your email.\n" +
              "            </p>\n" +
              "\n" +
              "            <div class=\"code-container\" style=\"text-align: center; margin: 30px 0; padding: 20px; border-radius: 8px;\">\n" +
              "                <p style=\"margin-bottom: 10px; font-size: 12px; color: #666; text-transform: uppercase; padding-left: 0;\">\n" +
              "                    Your New Verification Code\n" +
              "                </p>\n" +
              "                <div class=\"verification-code\" style=\"font-size: 32px; font-weight: bold; letter-spacing: 8px; color: black; margin: 0;\">\n" +
              "                     "+user.getVerification().getOtpCode()+"\n" +
              "                </div>\n" +
              "            </div>\n" +
              "            \n" +
              "        </div>\n" +
              "\n" +
              "        <div class=\"footer-signature\" style=\"margin-top: 30px; font-size: 14px; line-height: 1.4; color: #666666;\">\n" +
              "            Dev khatri<br>\n" +
              "            <strong style=\"color: #333333;\">App Support Team</strong>\n" +
              "        </div>\n" +
              "    </div>\n" +
              "\n" +
              "</body>\n" +
              "</html>";
    }
    private static String forgetPasswordLink(User user){
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reset Your Password</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; background-color: #f9f9f9; max-width: 550px; margin-left:auto; margin-right:auto; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\">\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed; background-color: #f9f9f9;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 550px; background-color: #ffffff; border-radius: 8px; border: 1px solid #e0e0e0; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.02);\">\n" +
                "                    \n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 40px 40px 20px 40px;\">\n" +
                "                            <div style=\"margin-bottom: 15px;\">\n" +
                "                                <img src=\"cid:logo\" alt=\"Logo\" style=\"width: 200px; height: 200px; display: block;\" />\n" +
                "                            </div>\n" +
                "                            <h2 style=\"margin: 0; color: #1a1a1a; font-size: 16px; letter-spacing: 2px; text-transform: uppercase; font-weight: 700; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\">App</h2>\n" +
                "                            <h1 style=\"margin: 15px 0 0 0; color: #111827; font-size: 24px; font-weight: 700; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\">Reset your password</h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 0 40px;\">\n" +
                "                            <hr style=\"border: 0; border-top: 1px solid #f0f0f0; margin: 0;\">\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px; text-align: left;\">\n" +
                "                            <p style=\"margin: 0 0 25px 0; color: #111827; font-size: 16px; font-weight: 600;\">Hey "+user.getName()+",</p>\n" +
                "                            <p style=\"margin: 0 0 30px 0; color: #4b5563; font-size: 15px; line-height: 1.6;\">\n" +
                "                                Need to reset your password? No problem! Just click the button below and you'll be on your way.\n" +
                "                            </p>\n" +
                "                            \n" +
                "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\">\n" +
                "                                        <a href="+user.getForgetPassword().getToken()+" target=\"_blank\" style=\"display: inline-block; width: 100%; background-color: #3b82f6; color: #ffffff; padding: 16px 0; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px; text-align: center;\">\n" +
                "                                            Reset your password\n" +
                "                                        </a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "                \n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 500px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 20px; color: #9ca3af; font-size: 12px;\">\n" +
                "                            &copy; 2026 App. All rights reserved.\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";
    }
    private static String passwordChangedSuccessfullyBody(User user){
        return "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Password Changed Successfully</title>\n" +
                "    </head>\n" +
                "<body style=\"margin: 0; padding: 0; background-color: #f9f9f9; max-width: 550px; margin-left:auto; margin-right:auto; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\">\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 550px; background-color: #ffffff; border-radius: 8px; border: 1px solid #e0e0e0; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.02);\">\n" +
                "                    \n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 40px 40px 20px 40px;\">\n" +
                "                            <div style=\"margin-bottom: 15px;\">\n" +
                "                               ' <img src='cid:logo' style=\" width: 200px; height:200px\"/>" +
                "                            </div>\n" +
                "                            <h2 style=\"margin: 0; color: #1a1a1a; font-size: 16px; letter-spacing: 2px; text-transform: uppercase; font-weight: 700;\">App</h2>\n" +
                "                            <h1 style=\"margin: 15px 0 0 0; color: #111827; font-size: 24px; font-weight: 700;\">Password updated</h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 0 40px;\">\n" +
                "                            <hr style=\"border: 0; border-top: 1px solid #f0f0f0; margin: 0;\">\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px; text-align: left;\">\n" +
                "                            <p style=\"margin: 0 0 20px 0; color: #111827; font-size: 16px; font-weight: 600;\">Success!</p>\n" +
                "                            <p style=\"margin: 0 0 30px 0; color: #4b5563; font-size: 15px; line-height: 1.6;\">\n" +
                "                                Your password has been successfully changed. You can now log back in with your new credentials.\n" +
                "                            </p>\n" +
                "                            \n" +
                "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\">\n" +
                "                                       \n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "                \n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 500px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 20px; color: #9ca3af; font-size: 12px;\">\n" +
                "                            Sent by App • Karachi, Sindh<br>\n" +
                "                            If you're having trouble, contact khatridev318@gmail.com\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";
    }
}
