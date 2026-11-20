package com.medislot.backend.algorithm;
import com.medislot.backend.model.Patient;
import com.medislot.backend.model.WaitingListEntry;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class WaitingListPrioritizerTest {
 @Test void approvedHigherPriorityEntriesAreServedFirst(){
  Patient patient=new Patient(null,"A","a@example.test","1234567890",30);
  WaitingListEntry regular=new WaitingListEntry(patient,"Cardiology",LocalDate.now(),null);
  WaitingListEntry urgent=new WaitingListEntry(patient,"Cardiology",LocalDate.now(),null);
  regular.setPriorityScore(0); urgent.setPriorityScore(4);
  var queue=WaitingListPrioritizer.queue(List.of(regular,urgent));
  assertSame(urgent,queue.poll()); assertSame(regular,queue.poll()); assertTrue(queue.isEmpty());
 }
 @Test void rejectsPriorityOutsideClinicianScale(){
  WaitingListEntry entry=new WaitingListEntry(new Patient(null,"A","a@example.test","1234567890",30),"x",LocalDate.now(),null);
  assertThrows(IllegalArgumentException.class,()->entry.setPriorityScore(6));
 }
}
