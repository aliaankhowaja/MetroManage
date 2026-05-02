package com.metromanage.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Passenger white-box tests (pure logic)")
class PassengerTest {

    @Test
    @DisplayName("GenerateHash: deterministic for identical input")
    void generateHash_shouldBeDeterministic_whenSameInput() {
        String hashA = Passenger.GenerateHash("secret");
        String hashB = Passenger.GenerateHash("secret");

        assertNotNull(hashA, "hash must not be null for valid input");
        assertEquals(hashA, hashB, "hashing the same password twice must produce the same digest");
        assertEquals(64, hashA.length(), "SHA-256 hex digest must be 64 hex characters");
    }

    @Test
    @DisplayName("GenerateHash: different inputs produce different digests")
    void generateHash_shouldDiffer_whenInputDiffers() {
        String a = Passenger.GenerateHash("password1");
        String b = Passenger.GenerateHash("password2");

        assertNotEquals(a, b, "Distinct passwords must yield distinct hashes");
    }

    @Test
    @DisplayName("GenerateHash: produces all lowercase hex characters")
    void generateHash_shouldBeLowerCaseHex_whenCalled() {
        String hash = Passenger.GenerateHash("anything");

        assertTrue(hash.matches("[0-9a-f]{64}"),
                "hash must be 64 lowercase hex characters, was: " + hash);
    }

    @Test
    @DisplayName("Passenger no-arg constructor + setters round-trip")
    void noArgConstructor_shouldAllowFieldPopulation_viaSetters() {
        Passenger p = new Passenger();
        p.setPassengerID(10);
        p.setName("Bob");
        p.setEmail("bob@example.com");
        p.setPhoneNumber("123");
        p.setPasswordHash("hash");
        p.setStatus("Active");
        p.setWalletBalance(50.0f);

        assertEquals(10, p.getPassengerID());
        assertEquals("Bob", p.getName());
        assertEquals("bob@example.com", p.getEmail());
        assertEquals("123", p.getPhoneNumber());
        assertEquals("hash", p.getPasswordHash());
        assertEquals("Active", p.getStatus());
        assertEquals(50.0f, p.getWalletBalance(), 0.0001f);
    }
}
