package com.genev.a100nts.server.services.impl;

import com.genev.a100nts.server.services.EmailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private static final String ARGUMENT_NAME;
    private static final String ARGUMENT_SECURITY_CODE;
    private static final String EMAIL_SUBJECT;
    private static String EMAIL_TEMPLATE_HTML;

    static {
        try {
            EMAIL_TEMPLATE_HTML = StreamUtils.copyToString(new ClassPathResource("static/email_template.html").getInputStream(), Charset.defaultCharset());
        } catch (IOException ignored) {
        }
        ARGUMENT_NAME = "NAME";
        ARGUMENT_SECURITY_CODE = "SECURITY_CODE";
        EMAIL_SUBJECT = "100 NTS | Security code";
    }

    @Autowired
    private JavaMailSender mailSender;

    @SneakyThrows
    public void sendSecurityCode(String email, String name, String code) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        final String message = EMAIL_TEMPLATE_HTML.replace(ARGUMENT_NAME, name).replace(ARGUMENT_SECURITY_CODE, code);
        helper.setTo(email);
        helper.setSubject(EMAIL_SUBJECT);
        helper.setText(message, true);
        mailSender.send(mimeMessage);
    }

}
