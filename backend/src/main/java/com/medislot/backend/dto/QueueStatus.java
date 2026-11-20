package com.medislot.backend.dto;
public record QueueStatus(Long appointmentId,int position,int patientsAhead,int estimatedWaitMinutes,String status,boolean estimateOnly) { }
