package com.medislot.backend.service;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.repository.AppointmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
@Component public class AppointmentReminderJob {
 private final AppointmentRepository appointments;private final NotificationService notifications;
 private final WaitingListService waitingList;
 public AppointmentReminderJob(AppointmentRepository appointments,NotificationService notifications,WaitingListService waitingList){this.appointments=appointments;this.notifications=notifications;this.waitingList=waitingList;}
 @Scheduled(fixedDelay=60000,initialDelay=30000)
 public void remindUpcoming(){waitingList.expireOffers();LocalDateTime now=LocalDateTime.now();LocalDateTime until=now.plusHours(24);
  for(Appointment a:appointments.findAll()) if("CONFIRMED".equals(a.getStatus())) {
   LocalDateTime starts=LocalDateTime.of(a.getAppointmentDate(),a.getAppointmentTime());
   if(!starts.isBefore(now)&&!starts.isAfter(until)) notifications.notifyPatient(a.getPatient(),"REMINDER","Reminder: appointment "+a.getId()+" with "+a.getDoctorName()+" is scheduled for "+starts+".","reminder-24h-"+a.getId(),"MediSlot appointment reminder");
  }
 }
}
