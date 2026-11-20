package com.medislot.backend.algorithm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoctorSearchTrieTest {
    @Test void suggestsCaseInsensitivePrefixes() {
        DoctorSearchTrie trie = new DoctorSearchTrie();
        trie.add("Dr. Priya Mehta");
        trie.add("Dermatologist");
        assertEquals("Dermatologist", trie.suggest("derm", 5).get(0));
        assertEquals("Dr. Priya Mehta", trie.suggest("DR.", 5).get(0));
    }
    @Test void returnsNoSuggestionsForMissingOrBlankPrefix() {
        DoctorSearchTrie trie = new DoctorSearchTrie();
        trie.add("Cardiologist");
        assertTrue(trie.suggest("neuro", 5).isEmpty());
        assertTrue(trie.suggest(" ", 5).isEmpty());
    }
}
