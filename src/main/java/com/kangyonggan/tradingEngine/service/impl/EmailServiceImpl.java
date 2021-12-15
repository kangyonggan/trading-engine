package com.kangyonggan.tradingEngine.service.impl;

import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.components.EmailProperties;
import com.kangyonggan.tradingEngine.components.MessageHandler;
import com.kangyonggan.tradingEngine.components.RedisManager;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.EmailCheckStatus;
import com.kangyonggan.tradingEngine.constants.enums.EmailType;
import com.kangyonggan.tradingEngine.dto.EmailDto;
import com.kangyonggan.tradingEngine.dto.req.CheckEmailCodeReq;
import com.kangyonggan.tradingEngine.dto.req.SendEmailReq;
import com.kangyonggan.tradingEngine.service.IEmailService;
import com.kangyonggan.tradingEngine.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author kyg
 */
@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private Properties props;

    @Autowired
    private EmailProperties emailProperties;

    @Resource(name = "sendMailExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private RedisManager redisManager;

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    @Valid
    public void sendEmail(SendEmailReq req) {
        // dev环境不发送验证码
        if (AppConstants.ENV_DEV.equals(env)) {
            return;
        }

        // 多语言
        Locale locale = messageHandler.getLocale();
        // 通用数据
        req.getData().put("datetime", DateUtil.format(new Date(), DateUtil.DATETIME_PATTERN));
        req.getData().put("expiresIn", String.valueOf(emailProperties.getExpiresIn()));
        String templateName = String.format("%s_%s/%s.html", locale.getLanguage(), locale.getCountry(), req.getType().name());
        String content;
        try {
            content = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfigurer.getConfiguration().getTemplate(templateName), req.getData());
        } catch (Exception e) {
            throw new BizException("加载邮件模板失败", e);
        }

        // 使用线程池发送
        taskExecutor.execute(() -> {
            send(req.getEmail(), req.getSubject(), content, true);
            // 放入redis
            EmailDto emailDto = new EmailDto();
            emailDto.setType(req.getType().name());
            emailDto.setTo(req.getEmail());
            emailDto.setVerifyCode(req.getVerifyCode());

            redisManager.set("EMAIL:" + emailDto.getType() + ":" + emailDto.getTo(), emailDto, emailProperties.getExpiresIn(), TimeUnit.MINUTES);
        });
    }

    @Override
    @Valid
    public EmailCheckStatus checkEmailCode(CheckEmailCodeReq req) {
        EmailCheckStatus status;
        EmailDto emailDto = redisManager.get("EMAIL:" + req.getType().name() + ":" + req.getEmail());

        if (emailDto == null) {
            // 未发送邮件 或者 邮件已失效 或者 邮件已使用
            status = EmailCheckStatus.INVALID;
        } else if (!emailDto.getVerifyCode().equals(req.getVerifyCode())) {
            // 验证码错误
            status = EmailCheckStatus.FAILURE;
        } else {
            // 验证码正确
            status = EmailCheckStatus.SUCCESS;

            // 删除邮件
            redisManager.delete("EMAIL:" + req.getType().name() + ":" + req.getEmail());
        }

        return status;
    }

    /**
     * 发送
     *
     * @param email
     * @param subject
     * @param content
     * @param isHtml
     */
    private void send(String email, String subject, String content, boolean isHtml) {
        try {
            initProperties();
            MimeMessage msg = new MimeMessage(getSession());
            MimeMessageHelper helper = new MimeMessageHelper(msg, isHtml, StandardCharsets.UTF_8.name());
            helper.setFrom(emailProperties.getUsername());
            helper.setTo(email);
            helper.setSubject(subject);

            // html
            if (isHtml) {
                Multipart mp = new MimeMultipart();
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(content, MediaType.TEXT_HTML_VALUE + ";charset=gb2312");
                mp.addBodyPart(htmlPart);
                msg.setContent(mp);
            } else {
                helper.setText(content);
            }
            Transport.send(msg);

            log.info("给{}发送邮件成功", email);
        } catch (Exception e) {
            log.error("给{}发送邮件失败", email, e);
        }
    }

    private void initProperties() {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        if (props == null) {
            props = new Properties();
            props.setProperty("mail.smtp.host", emailProperties.getHost());
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", emailProperties.getPort());
            props.setProperty("mail.smtp.socketFactory.port", emailProperties.getPort());
            props.put("mail.smtp.auth", "true");
        }
    }

    private Session getSession() {
        return Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getUsername(), emailProperties.getPassword());
            }
        });
    }
}
