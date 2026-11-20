package com.medislot.backend.algorithm;
import com.medislot.backend.model.Appointment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Binary searches chronologically sorted appointment records by date. */
public final class AppointmentBinarySearch {
    private AppointmentBinarySearch() { }
    public static List<Appointment> onDate(List<Appointment> source, LocalDate date) {
        List<Appointment> sorted = new ArrayList<>(source);
        sorted.sort(Comparator.comparing(Appointment::getAppointmentDate).thenComparing(Appointment::getAppointmentTime));
        int low=0, high=sorted.size()-1, found=-1;
        while (low<=high) {
            int mid=(low+high) >>> 1;
            int cmp=sorted.get(mid).getAppointmentDate().compareTo(date);
            if (cmp<0) low=mid+1;
            else if (cmp>0) high=mid-1;
            else { found=mid; high=mid-1; }
        }
        if (found<0) return List.of();
        List<Appointment> matches=new ArrayList<>();
        for (int i=found; i<sorted.size() && sorted.get(i).getAppointmentDate().equals(date); i++) matches.add(sorted.get(i));
        return matches;
    }
}
