package com.medislot.backend.service;
import com.medislot.backend.model.*;
import com.medislot.backend.repository.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service public class NotificationService {
 private final NotificationRepository notifications;private final PatientRepository patients;private final ObjectProvider<JavaMailSender> mail;
 private final String from;
 public NotificationService(NotificationRepository notifications,PatientRepository patients,ObjectProvider<JavaMailSender> mail,@Value("${app.mail.from:}") String from){this.notifications=notifications;this.patients=patients;this.mail=mail;this.from=from;}
 @Transactional public void notifyPatient(Patient patient,String type,String message,String key,String emailSubject){
  if(notifications.existsByDedupeKey(key)) return;
  notifications.save(new NotificationItem(patient,type,message,key));
  JavaMailSender sender=mail.getIfAvailable();
  if(sender!=null && !from.isBlank()) try {SimpleMailMessage email=new SimpleMailMessage();email.setFrom(from);email.setTo(patient.getEmail());email.setSubject(emailSubject);email.setText(message);sender.send(email);}catch(RuntimeException ignored){/* In-app delivery remains available if mail is temporarily down. */}
 }
 public List<NotificationItem> forPatient(String email){Patient p=patients.findByEmail(email).filter(Patient::isActive).orElseThrow();return notifications.findByPatient_IdOrderByCreatedAtDesc(p.getId());}
 @Transactional public void markRead(String email,Long id){Patient p=patients.findByEmail(email).filter(Patient::isActive).orElseThrow();NotificationItem item=notifications.findByIdAndPatient_Id(id,p.getId()).orElseThrow();item.setReadAt(java.time.LocalDateTime.now());}
}
