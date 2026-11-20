package com.medislot.backend.config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;
@Configuration @ConditionalOnProperty(name="app.mail.enabled",havingValue="true")
public class MailConfig {
 @Bean JavaMailSender javaMailSender(@Value("${MAIL_HOST}") String host,@Value("${MAIL_PORT:587}") int port,@Value("${MAIL_USERNAME:}") String user,@Value("${MAIL_PASSWORD:}") String password){
  JavaMailSenderImpl sender=new JavaMailSenderImpl();sender.setHost(host);sender.setPort(port);sender.setUsername(user);sender.setPassword(password);sender.getJavaMailProperties().put("mail.smtp.auth","true");sender.getJavaMailProperties().put("mail.smtp.starttls.enable","true");return sender;
 }
}
