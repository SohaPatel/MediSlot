
package com.medislot.backend.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BloomFilterTest {

    // Test 1: Insert and detect an email
    @Test
    void testAddAndMightContain() {

        BloomFilter filter = new BloomFilter(1000, 5);

        filter.add("patient@gmail.com");

        assertTrue(filter.mightContain("patient@gmail.com"));
    }

    // Test 2: Empty filter should report an absent email
    @Test
    void testEmptyFilter() {

        BloomFilter filter = new BloomFilter(1000, 5);

        assertFalse(filter.mightContain("unknown@gmail.com"));
    }

    // Test 3: Multiple emails can be inserted
    @Test
    void testMultipleEmails() {

        BloomFilter filter = new BloomFilter(1000, 5);

        filter.add("patient1@gmail.com");
        filter.add("patient2@gmail.com");
        filter.add("patient3@gmail.com");

        assertTrue(filter.mightContain("patient1@gmail.com"));
        assertTrue(filter.mightContain("patient2@gmail.com"));
        assertTrue(filter.mightContain("patient3@gmail.com"));
    }

    // Test 4: Adding the same email repeatedly
    @Test
    void testDuplicateInsertion() {

        BloomFilter filter = new BloomFilter(1000, 5);

        filter.add("patient@gmail.com");
        filter.add("patient@gmail.com");
        filter.add("patient@gmail.com");

        assertTrue(filter.mightContain("patient@gmail.com"));
    }

    // Test 5: Null and blank lookup handling
    @Test
    void testNullAndBlankLookup() {

        BloomFilter filter = new BloomFilter(1000, 5);

        assertFalse(filter.mightContain(null));
        assertFalse(filter.mightContain(""));
        assertFalse(filter.mightContain("   "));
    }

    // Test 6: Invalid filter configuration
    @Test
    void testInvalidConfiguration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new BloomFilter(0, 5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new BloomFilter(1000, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new BloomFilter(-100, 5)
        );
    }

    // Test 7: Null and blank insertion should be rejected
    @Test
    void testInvalidInsertion() {

        BloomFilter filter = new BloomFilter(1000, 5);

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.add(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.add("")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> filter.add("   ")
        );
    }

    // Test 8: Filter configuration getters
    @Test
    void testConfigurationGetters() {

        BloomFilter filter = new BloomFilter(2000, 7);

        assertEquals(2000, filter.getSize());
        assertEquals(7, filter.getHashCount());
    }

}
