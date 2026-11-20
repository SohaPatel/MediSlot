package com.medislot.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="notifications",uniqueConstraints=@UniqueConstraint(name="uk_notification_dedupe",columnNames="dedupe_key"))
public class NotificationItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="patient_id",nullable=false) @JsonIgnore private Patient patient;
 @Column(nullable=false,length=40) private String type;
 @Column(nullable=false,length=1000) private String message;
 @Column(nullable=false,length=190) private String dedupeKey;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 private LocalDateTime readAt;
 protected NotificationItem(){}
 public NotificationItem(Patient p,String type,String message,String key){this.patient=p;this.type=type;this.message=message;this.dedupeKey=key;}
 public Long getId(){return id;} public String getType(){return type;} public String getMessage(){return message;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getReadAt(){return readAt;} public void setReadAt(LocalDateTime t){this.readAt=t;}
}
