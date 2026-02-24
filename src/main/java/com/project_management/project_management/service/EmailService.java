package com.project_management.project_management.service;

import com.project_management.project_management.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Async
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.email.username}")
    private String from;
    @Autowired
    public EmailService(final JavaMailSender mailSender){
        this.mailSender = mailSender;
    }
    public void registrationSuccessfulEmail(User user) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.jpg");
        sendEmail(user, "Account created successfully! Here is your account verification code", emailRegistrationSuccessfulContent(user), classPathResource, "logo");

    }
    public void ForgetPasswordLink(User user) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.jpg");
        sendEmail(user, "Forget Password link", forgetPasswordLink(user), classPathResource, "logo");

    }
    public void PasswordChangedSuccessfully(User user) throws MessagingException {
        ClassPathResource classPathResource = new ClassPathResource("/images/app_logo.jpg");
        sendEmail(user, "Password changed successfully!", passwordChangedSuccessfullyBody(user), classPathResource, "logo");

    }

    private void sendEmail(User user, String subject, String body, ClassPathResource classPathResource, String imgName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setText(body, true);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.addInline(imgName, classPathResource);

        mailSender.send(mimeMessage);
    }

    private static String emailRegistrationSuccessfulContent(User user){
        return "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        /* General Reset */\n" +
                "        body {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f9f9f9;\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "\n" +
                "        .email-container {\n" +
                "            max-width: 800px;\n" +
                "            margin: 20px auto;\n" +
                "            background: #ffffff;\n" +
                "            border: 1px solid #eeeeee;\n" +
                "            padding: 20px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "\n" +
                "        .header-logo {\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "\n" +
                "        .header-logo img {\n" +
                "            width: 150px; /* Placeholder for Carbon Logo */\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            font-size: 22px;\n" +
                "            font-weight: 700;\n" +
                "            margin-bottom: 25px;\n" +
                "            color: #000000;\n" +
                "            padding-left:20px;\n" +
                "            padding-top:20px\n" +
                "        }\n" +
                "\n" +
                "        p {\n" +
                "            font-size: 15px;\n" +
                "            line-height: 1.6;\n" +
                "            margin-bottom: 15px;\n" +
                "            padding-left:20px\n" +
                "        }\n" +
                "\n" +
                "        /* Verification Code Box */\n" +
                "        .code-container {\n" +
                "            text-align: center;\n" +
                "            margin: 30px 0;\n" +
                "            padding: 20px;\n" +
                "            //background-color: #f0f7ff;\n" +
                "            border-radius: 8px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-code {\n" +
                "            font-size: 32px;\n" +
                "            font-weight: bold;\n" +
                "            letter-spacing: 8px;\n" +
                "            color: black;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        .footer-signature {\n" +
                "            margin-top: 30px;\n" +
                "            font-size: 14px;\n" +
                "            line-height: 1.4;\n" +
                "        }\n" +
                "\n" +
                "        /* Social Icons Simulation */\n" +
                "        .social-links {\n" +
                "            text-align: center;\n" +
                "            margin-top: 40px;\n" +
                "        }\n" +
                "\n" +
                "        .social-icon {\n" +
                "            display: inline-block;\n" +
                "            width: 30px;\n" +
                "            height: 30px;\n" +
                "            background-color: #74c0fc;\n" +
                "            border-radius: 50%;\n" +
                "            margin: 0 5px;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "      .mainBody{\n" +
                "      box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px         0px, rgba(0, 0, 0, 0.08) 0px 0px 0px 1px;\n" +
                "      }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"header-logo\">\n" +
                "            <strong style=\"font-size: 24px; letter-spacing: 2px;\"><img src='cid:logo' style=\" width: 40px; height:40px\"/>\n" +
                "        </div>\n" +
                "        <div class=\"mainBody\">\n" +
                "        <h1>Verify This Email Address</h1>\n" +
                "\n" +
                "        <p>Hi '"+user.getName()+"',</p>\n" +
                "        \n" +
                "        <p>Welcome to App!</p>\n" +
                "\n" +
                "        <p>Please use the verification code below to confirm your email address.</p>\n" +
                "\n" +
                "        <div class=\"code-container\">\n" +
                "            <p style=\"margin-bottom: 10px; font-size: 12px; color: #666; text-transform: uppercase;\">Your Verification Code</p>\n" +
                "            <div class=\"verification-code\">'"+user.getVerification().getOtpCode()+"'</div>\n" +
                "        </div>\n" +
                " </div>\n" +
                "        <div class=\"footer-signature\">\n" +
                "            Dev khatri<br>\n" +
                "            <strong>App Support Team</strong>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }
    private static String forgetPasswordLink(User user){
        return "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reset Your Password</title>\n" +
                "    </head>\n" +
                "<body style=\"margin: 0; padding: 0; background-color: #f9f9f9; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\">\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 500px; background-color: #ffffff; border-radius: 8px; border: 1px solid #e0e0e0; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.02);\">\n" +
                "                    \n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 40px 40px 20px 40px;\">\n" +
                "                            <div style=\"margin-bottom: 15px;\">\n" +
                "                                <svg width=\"40\" height=\"40\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "                                   ' <img src='cid:logo' style=\" width: 40px; height:40px\"/> "+
                "                                </svg>\n" +
                "                            </div>\n" +
                "                            <h2 style=\"margin: 0; color: #1a1a1a; font-size: 16px; letter-spacing: 2px; text-transform: uppercase; font-weight: 700;\">BLUASSIST</h2>\n" +
                "                            <h1 style=\"margin: 15px 0 0 0; color: #111827; font-size: 24px; font-weight: 700;\">Reset your password</h1>\n" +
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
                "                            <p style=\"margin: 0 0 25px 0; color: #111827; font-size: 16px; font-weight: 600;\">Hey Jack,</p>\n" +
                "                            <p style=\"margin: 0 0 30px 0; color: #4b5563; font-size: 15px; line-height: 1.6;\">\n" +
                "                                Need to reset your password? No problem! Just click the button below and you'll be on your way. If you did not make this request, please ignore this email.\n" +
                "                            </p>\n" +
                "                            \n" +
                "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                                <tr>\n" +
                "                                    <td align=\"center\">\n" +
                "                                        <a href=\"'"+user.getForgetPassword().getToken()+"'\" target=\"_blank\" style=\"display: inline-block; width: 100%; background-color: #3b82f6; color: #ffffff; padding: 16px 0; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px;\">\n" +
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
                "<body style=\"margin: 0; padding: 0; background-color: #f9f9f9; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;\">\n" +
                "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 10px;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 500px; background-color: #ffffff; border-radius: 8px; border: 1px solid #e0e0e0; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.02);\">\n" +
                "                    \n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 40px 40px 20px 40px;\">\n" +
                "                            <div style=\"margin-bottom: 15px;\">\n" +
                "                               ' <img src='cid:logo' style=\" width: 40px; height:40px\"/>" +
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
                "                            <p style=\"margin: 30px 0 0 0; color: #6b7280; font-size: 13px; line-height: 1.5; text-align: center;\">\n" +
                "                                Didn't make this change? <a href=\"#\" style=\"color: #ef4444; text-decoration: underline; font-weight: 600;\">Secure your account immediately.</a>\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "                \n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 500px;\">\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"padding: 20px; color: #9ca3af; font-size: 12px;\">\n" +
                "                            Sent by App • New York, NY 10001<br>\n" +
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
