package com.medislot.backend.algorithm;
import com.medislot.backend.model.Appointment;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class AppointmentBinarySearchTest {
    @Test void findsAllAppointmentsOnDateFromUnsortedInput() {
        var day=LocalDate.of(2026, 12, 1);
        var later=LocalDate.of(2026, 12, 2);
        var results=AppointmentBinarySearch.onDate(List.of(
                new Appointment(1L,1L,"A",1L,"D",later,LocalTime.NOON,"CONFIRMED"),
                new Appointment(2L,1L,"A",1L,"D",day,LocalTime.of(10,0),"CONFIRMED"),
                new Appointment(3L,1L,"A",1L,"D",day,LocalTime.of(9,0),"CONFIRMED")), day);
        assertEquals(2, results.size());
        assertEquals(LocalTime.of(9,0), results.get(0).getAppointmentTime());
    }
    @Test void returnsEmptyForMissingDate() {
        assertTrue(AppointmentBinarySearch.onDate(List.of(), LocalDate.now()).isEmpty());
    }
}
