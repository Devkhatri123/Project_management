package com.project_management.project_management.service.email.task;

import com.project_management.project_management.model.Task;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Async
public class TaskEmailService {
    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;

    @Autowired
    public TaskEmailService(final JavaMailSender javaMailSender){
        this.mailSender = javaMailSender;
    }

    public void sendTaskDeadLineUpComingReminder(Task task, int timeRemaining) throws MessagingException {
       sendEmail(task.getAssignee().getEmail(), "You have an upcoming of a task", upcomingTasDeadLineBody(task, timeRemaining));
    }
    public void sendTaskDeadLineMissed(Task task) throws MessagingException {
         sendEmail(task.getAssignee().getEmail(), "You missed a task deadline", DeadLineMissedBody(task));
    }
    public void sendTaskAssignedEmail(Task task, String formattedDeadLine) throws MessagingException {
        sendEmail(task.getAssignee().getEmail(), "You have been assigned a new task", TaskAssignedBody(task,formattedDeadLine));
    }

    private String DeadLineMissedBody(Task task){
        return "\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; background-color: #f8fafc; font-family: 'Inter', -apple-system, sans-serif; }\n" +
                "        .wrapper { width: 100%; background-color: #f8fafc; padding: 40px 0; }\n" +
                "        .main { background-color: #ffffff; margin: 0 auto; width: 100%; max-width: 550px; border-radius: 16px; border: 1px solid #e2e8f0; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05); overflow: hidden; }\n" +
                "        \n" +
                "        /* Header Section */\n" +
                "        .header { padding: 32px 32px 0; }\n" +
                "        .icon-circle { width: 48px; height: 48px; background-color: #fef2f2; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin-bottom: 20px; }\n" +
                "        .h1 { font-size: 22px; font-weight: 700; color: #0f172a; margin: 0; letter-spacing: -0.02em; }\n" +
                "        \n" +
                "        /* Content Section */\n" +
                "        .content { padding: 24px 32px 32px; color: #475569; line-height: 1.6; font-size: 15px; }\n" +
                "        \n" +
                "        /* The Task Card */\n" +
                "        .task-card { background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px; margin: 24px 0; }\n" +
                "        .task-row { display: flex; align-items: center; margin-bottom: 12px; }\n" +
                "        .task-row:last-child { margin-bottom: 0; }\n" +
                "        \n" +
                "        /* Icons */\n" +
                "        .ui-icon { width: 18px; height: 18px; margin-right: 12px; vertical-align: middle; }\n" +
                "        \n" +
                "        .task-title { font-size: 16px; font-weight: 600; color: #1e293b; display: block; margin-bottom: 16px; }\n" +
                "        .label-text { font-size: 14px; color: #64748b; }\n" +
                "        .value-text { font-size: 14px; color: #334155; font-weight: 500; }\n" +
                "        .urgent-text { color: #dc2626; font-weight: 600; }\n" +
                "\n" +
                "        /* Button */\n" +
                "        .btn { background-color: #4f46e5; color: #ffffff !important; padding: 14px 28px; border-radius: 8px; text-decoration: none; font-weight: 600; display: inline-block; font-size: 15px; margin-top: 10px; }\n" +
                "        \n" +
                "        .footer { text-align: center; font-size: 13px; color: #94a3b8; padding: 24px 32px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"wrapper\">\n" +
                "        <div class=\"main\">\n" +
                "            <div class=\"header\">\n" +
                "                <div class=\"icon-circle\">\n" +
                "                    <img src=\"https://img.icons8.com/fluency-systems-filled/48/ef4444/alarm-clock.png\" width=\"24\" height=\"24\" alt=\"Alert\">\n" +
                "                </div>\n" +
                "                <h1 class=\"h1\">Deadline Missed</h1>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"content\">\n" +
                "                <p>Hi <strong>"+task.getAssignee().getName()+"</strong>, just a heads-up that a task in <strong>FoodPanda Mvp</strong> is now overdue. Please take a moment to update the status.</p>\n" +
                "                \n" +
                "                <div class=\"task-card\">\n" +
                "                    <span class=\"task-title\">"+task.getTitle()+"</span>\n" +
                "                    \n" +
                "                    <div class=\"task-row\">\n" +
                "                        <img src=\"https://img.icons8.com/fluency-systems-regular/48/64748b/folder-invoices.png\" class=\"ui-icon\">\n" +
                "                        <span class=\"label-text\">Project: </span>\n" +
                "                        <span class=\"value-text\">&nbsp;"+task.getProject().getTitle()+"</span>\n" +
                "                    </div>\n" +
                "                    \n" +
                "                    <div class=\"task-row\">\n" +
                "                        <img src=\"https://img.icons8.com/?size=512w&id=zs2rX4l5u1cs&format=png\" class=\"ui-icon\">\n" +
                "                        <span class=\"label-text\">Due Date: </span>\n" +
                "                        <span class=\"urgent-text\">&nbsp;26/3/2026</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "\n" +
                "                <div style=\"text-align: center;\">\n" +
                "                    <a href=\"{{taskUrl}}\" class=\"btn\">View Task Details</a>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <p>Sent from <strong>Taskify</strong> </p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
    public String upcomingTasDeadLineBody(Task task, int timeRemaining){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; background-color: #f8fafc; font-family: 'Inter', -apple-system, sans-serif; }\n" +
                "        .wrapper { width: 100%; background-color: #f8fafc; padding: 40px 0; }\n" +
                "        .main { background-color: #ffffff; margin: 0 auto; width: 100%; max-width: 550px; border-radius: 16px; border: 1px solid #e2e8f0; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); overflow: hidden; }\n" +
                "        \n" +
                "        /* Header Section */\n" +
                "        .header { padding: 32px 32px 0; }\n" +
                "        .icon-circle { width: 48px; height: 48px; background-color: #eef2ff; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin-bottom: 20px; }\n" +
                "        .h1 { font-size: 22px; font-weight: 700; color: #0f172a; margin: 0; letter-spacing: -0.02em; }\n" +
                "        \n" +
                "        /* Content Section */\n" +
                "        .content { padding: 24px 32px 32px; color: #475569; line-height: 1.6; font-size: 15px; }\n" +
                "        \n" +
                "        /* The Task Card */\n" +
                "        .task-card { background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px; margin: 24px 0; }\n" +
                "        .task-row { display: flex; align-items: center; margin-bottom: 12px; }\n" +
                "        .task-row:last-child { margin-bottom: 0; }\n" +
                "        \n" +
                "        /* Icons */\n" +
                "        .ui-icon { width: 18px; height: 18px; margin-right: 12px; vertical-align: middle; }\n" +
                "        \n" +
                "        .task-title { font-size: 16px; font-weight: 600; color: #1e293b; display: block; margin-bottom: 16px; }\n" +
                "        .label-text { font-size: 14px; color: #64748b; }\n" +
                "        .value-text { font-size: 14px; color: #334155; font-weight: 500; }\n" +
                "        .due-text { color: #4f46e5; font-weight: 600; }\n" +
                "\n" +
                "        /* Button */\n" +
                "        .btn { background-color: #4f46e5; color: #ffffff !important; padding: 14px 28px; border-radius: 8px; text-decoration: none; font-weight: 600; display: inline-block; font-size: 15px; margin-top: 10px; }\n" +
                "        \n" +
                "        .footer { text-align: center; font-size: 13px; color: #94a3b8; padding: 24px 32px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"wrapper\">\n" +
                "        <div class=\"main\">\n" +
                "            <div class=\"header\">\n" +
                "                <div class=\"icon-circle\">\n" +
                "                    <img src=\"https://img.icons8.com/fluency-systems-filled/48/4f46e5/calendar-plus.png\" width=\"24\" height=\"24\" alt=\"Upcoming\">\n" +
                "                </div>\n" +
                "                <h1 class=\"h1\">Upcoming Deadline</h1>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"content\">\n" +
                "                <p>Hi <strong>"+task.getAssignee().getName()+"</strong>, this is a quick heads-up that a task in <strong>{{workspaceName}}</strong> is due soon. Just making sure it's on your radar!</p>\n" +
                "                \n" +
                "                <div class=\"task-card\">\n" +
                "                    <span class=\"task-title\">"+task.getTitle()+"</span>\n" +
                "                    \n" +
                "                    <div class=\"task-row\">\n" +
                "                        <img src=\"https://img.icons8.com/fluency-systems-regular/48/64748b/opened-folder.png\" class=\"ui-icon\">\n" +
                "                        <span class=\"label-text\">Project: </span>\n" +
                "                        <span class=\"value-text\">&nbsp;"+task.getProject().getTitle()+"</span>\n" +
                "                    </div>\n" +
                "                    \n" +
                "                    <div class=\"task-row\">\n" +
                "                        <img src=\"https://img.icons8.com/fluency-systems-regular/48/4f46e5/clock--v1.png\" class=\"ui-icon\">\n" +
                "                        <span class=\"label-text\">Due In: </span>\n" +
                "                        <span class=\"due-text\">&nbsp "+timeRemaining+" ({{deadline}})</span>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "\n" +
                "                <div style=\"text-align: center;\">\n" +
                "                    <a href=\"{{taskUrl}}\" class=\"btn\">View Task Details</a>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <p>Sent via <strong>Taskify</strong></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
    public String TaskAssignedBody(Task task, String formattedDeadLine){
        return "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; background-color: #f3f4f6; font-family: 'Inter', -apple-system, sans-serif; }\n" +
                "        .wrapper { width: 100%; background-color: #f3f4f6; padding: 48px 0; }\n" +
                "        .main { background-color: #ffffff; margin: 0 auto; width: 100%; max-width: 550px; border-radius: 20px; border: 1px solid #e5e7eb; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.04); overflow: hidden; }\n" +
                "        \n" +
                "        /* Header */\n" +
                "        .header { background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%); padding: 40px 32px; text-align: center; color: #ffffff; }\n" +
                "        .icon-badge { background: rgba(255, 255, 255, 0.2); width: 56px; height: 56px; border-radius: 14px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 16px; backdrop-filter: blur(4px); }\n" +
                "        .h1 { font-size: 24px; font-weight: 800; margin: 0; letter-spacing: -0.03em; }\n" +
                "        \n" +
                "        /* Content */\n" +
                "        .content { padding: 32px; color: #374151; line-height: 1.6; }\n" +
                "        .greeting { font-size: 16px; margin-bottom: 8px; color: #111827; }\n" +
                "        \n" +
                "        /* Task Card */\n" +
                "        .task-card { background-color: #f9fafb; border: 1px solid #f3f4f6; border-radius: 16px; padding: 24px; margin: 24px 0; position: relative; }\n" +
                "        .priority-tag { font-size: 11px; font-weight: 700; text-transform: uppercase; padding: 4px 10px; border-radius: 20px; display: inline-block; margin-bottom: 12px; }\n" +
                "        .priority-high { background-color: #fee2e2; color: #dc2626; }\n" +
                "        .priority-medium { background-color: #fef3c7; color: #d97706; }\n" +
                "        \n" +
                "        .task-title { font-size: 18px; font-weight: 700; color: #111827; margin-bottom: 16px; display: block; }\n" +
                "        \n" +
                "        .info-row { display: flex; align-items: center; margin-bottom: 10px; font-size: 14px; }\n" +
                "        .info-icon { width: 18px; height: 18px; margin-right: 12px; opacity: 0.7; }\n" +
                "        .label { color: #6b7280; width: 80px; font-weight: 500; }\n" +
                "        .value { color: #1f2937; font-weight: 600; }\n" +
                "\n" +
                "        /* Description Box */\n" +
                "        .desc-box { font-size: 14px; color: #4b5563; border-top: 1px solid #e5e7eb; margin-top: 16px; padding-top: 16px; font-style: italic; }\n" +
                "\n" +
                "        /* Interactive Button */\n" +
                "        .cta-container { text-align: center; margin-top: 32px; }\n" +
                "        .btn-primary { background-color: #111827; color: #ffffff !important; padding: 16px 32px; border-radius: 12px; text-decoration: none; font-weight: 600; font-size: 15px; display: inline-block; transition: all 0.2s; }\n" +
                "        \n" +
                "        .footer { text-align: center; font-size: 12px; color: #9ca3af; padding-bottom: 40px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"wrapper\">\n" +
                "        <div class=\"main\">\n" +
                "            <div class=\"header\">\n" +
                "                <div class=\"icon-badge\">\n" +
                "                    <img src=\"https://img.icons8.com/fluency-systems-filled/48/ffffff/add-property.png\" width=\"28\" height=\"28\" alt=\"New Task\">\n" +
                "                </div>\n" +
                "                <h1 class=\"h1\">New Task Assigned</h1>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"content\">\n" +
                "                <p class=\"greeting\">Hey <strong>"+task.getAssignee().getName()+"</strong>,</p>\n" +
                "                <p>You've been assigned a new task by <strong>"+task.getCreatedBy().getName()+"</strong> in the <strong>"+task.getProject().getTitle()+"</strong> workspace.</p>\n" +
                "                \n" +
                "                <div class=\"task-card\">\n" +
                "                    <div class=\"priority-tag priority-{{priorityClass}}\">"+task.getTask_status().getStatus_name()+"</div>\n" +
                "                    \n" +
                "                    <span class=\"task-title\">"+task.getTitle()+"</span>\n" +
                "                    \n" +
                "                    <div class=\"info-row\">\n" +
                "                        <img src=\"https://img.icons8.com/fluency-systems-regular/48/6b7280/layers.png\" class=\"info-icon\">\n" +
                "                        <span class=\"label\">Project</span>\n" +
                "                        <span class=\"value\">"+task.getProject().getTitle()+"</span>\n" +
                "                    </div>\n" +
                "                    \n" +
                "                    <div class=\"info-row\">\n" +
                "                        <img src=\"https://img.icons8.com/fluency-systems-regular/48/6b7280/calendar-8.png\" class=\"info-icon\">\n" +
                "                        <span class=\"label\">Due Date</span>\n" +
                "                        <span class=\"value\">"+formattedDeadLine+"</span>\n" +
                "                    </div>\n" +
                "\n" +
                "                    <div class=\"desc-box\">\n" +
                "                        Description: "+task.getDescription()+"\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "\n" +
                "                <div class=\"cta-container\">\n" +
                "                    <a href=\"{{taskUrl}}\" class=\"btn-primary\">Accept & Open Task</a>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"footer\">\n" +
                "            <p>Sent from <strong>Taskify</strong></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
    private void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setText(body, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        mailSender.send(mimeMessage);
    }
}
