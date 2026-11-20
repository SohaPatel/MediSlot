package com.medislot.backend.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;

/** Prefix suggestions for the current doctor names and specializations. */
public class DoctorSearchTrie {
    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        final TreeSet<String> values = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    }
    private final Node root = new Node();
    public void add(String value) {
        if (value == null || value.isBlank()) return;
        Node node = root;
        for (char c : value.toLowerCase(Locale.ROOT).toCharArray()) {
            node = node.children.computeIfAbsent(c, ignored -> new Node());
            node.values.add(value);
        }
    }
    public List<String> suggest(String prefix, int limit) {
        if (prefix == null || prefix.isBlank() || limit <= 0) return List.of();
        Node node = root;
        for (char c : prefix.toLowerCase(Locale.ROOT).toCharArray()) {
            node = node.children.get(c);
            if (node == null) return List.of();
        }
        return node.values.stream().limit(limit).toList();
    }
}
