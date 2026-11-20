package com.medislot.backend.algorithm;
import com.medislot.backend.model.WaitingListEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
public final class WaitingListPrioritizer {
 private WaitingListPrioritizer(){}
 public static PriorityQueue<WaitingListEntry> queue(Collection<WaitingListEntry> eligible){
  PriorityQueue<WaitingListEntry> queue=new PriorityQueue<>(Comparator.comparingInt(WaitingListEntry::getPriorityScore).reversed().thenComparing(WaitingListEntry::getCreatedAt));
  queue.addAll(eligible);return queue;
 }
}
