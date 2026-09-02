package com.vaccine.service;

import com.vaccine.entity.Appointment;
import com.vaccine.entity.User;
import com.vaccine.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通知服务（模拟短信/邮件通知）
 * 通知内容会保存到txt文件中模拟用户接收
 */
@Slf4j
@Service
public class NotificationService {

    @Autowired
    private UserMapper userMapper;

    @Value("${notification.save.path:notifications}")
    private String notificationPath;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 发送预约审核通过通知
     */
    public void sendApprovalNotification(Appointment appointment) {
        User user = userMapper.findById(appointment.getUserId());
        if (user == null) return;

        String message = String.format(
            "【疫苗预约系统】尊敬的%s，您的疫苗预约（单号：%s）已审核通过，请及时支付。预约日期：%s，时段：%s。",
            user.getRealName() != null ? user.getRealName() : user.getUsername(),
            appointment.getOrderNo(),
            appointment.getAppointmentDate(),
            appointment.getTimeSlot()
        );

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            sendSms(user.getPhone(), message);
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sendEmail(user.getEmail(), "预约审核通过通知", message);
        }
    }

    /**
     * 发送预约审核拒绝通知
     */
    public void sendRejectionNotification(Appointment appointment, String reason) {
        User user = userMapper.findById(appointment.getUserId());
        if (user == null) return;

        String message = String.format(
            "【疫苗预约系统】尊敬的%s，您的疫苗预约（单号：%s）审核未通过。原因：%s。如有疑问请联系社区。",
            user.getRealName() != null ? user.getRealName() : user.getUsername(),
            appointment.getOrderNo(),
            reason != null ? reason : "未说明"
        );

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            sendSms(user.getPhone(), message);
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sendEmail(user.getEmail(), "预约审核未通过通知", message);
        }
    }

    /**
     * 发送支付成功通知
     */
    public void sendPaymentSuccessNotification(Appointment appointment) {
        User user = userMapper.findById(appointment.getUserId());
        if (user == null) return;

        String message = String.format(
            "【疫苗预约系统】尊敬的%s，您的疫苗预约（单号：%s）已支付成功。请于%s %s到预约社区接种。",
            user.getRealName() != null ? user.getRealName() : user.getUsername(),
            appointment.getOrderNo(),
            appointment.getAppointmentDate(),
            appointment.getTimeSlot()
        );

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            sendSms(user.getPhone(), message);
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sendEmail(user.getEmail(), "支付成功通知", message);
        }
    }

    /**
     * 发送接种完成通知
     */
    public void sendVaccinationCompleteNotification(Appointment appointment) {
        User user = userMapper.findById(appointment.getUserId());
        if (user == null) return;

        String message = String.format(
            "【疫苗预约系统】尊敬的%s，您已完成疫苗接种（单号：%s）。请注意观察身体状况，如有不适请及时就医。",
            user.getRealName() != null ? user.getRealName() : user.getUsername(),
            appointment.getOrderNo()
        );

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            sendSms(user.getPhone(), message);
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            sendEmail(user.getEmail(), "接种完成通知", message);
        }
    }

    /**
     * 模拟发送短信（同时保存到txt文件）
     */
    private void sendSms(String phone, String message) {
        log.info("【模拟短信】发送至：{}，内容：{}", maskPhone(phone), message);
        saveToFile("sms", phone, null, message);
    }

    /**
     * 模拟发送邮件（同时保存到txt文件）
     */
    private void sendEmail(String email, String subject, String content) {
        log.info("【模拟邮件】发送至：{}，主题：{}，内容：{}", maskEmail(email), subject, content);
        saveToFile("email", email, subject, content);
    }

    /**
     * 保存通知到txt文件
     */
    private void saveToFile(String type, String recipient, String subject, String content) {
        try {
            File dir = new File(notificationPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = type.equals("sms") ? "sms_notifications.txt" : "email_notifications.txt";
            File file = new File(dir, filename);

            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                writer.println("========================================");
                writer.println("时间: " + LocalDateTime.now().format(FORMATTER));
                writer.println("类型: " + (type.equals("sms") ? "短信通知" : "邮件通知"));
                writer.println("接收人: " + recipient);
                if (subject != null) {
                    writer.println("主题: " + subject);
                }
                writer.println("内容: " + content);
                writer.println("========================================");
                writer.println();
            }

            log.debug("通知已保存到文件: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("保存通知到文件失败", e);
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) return email;
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }
}
