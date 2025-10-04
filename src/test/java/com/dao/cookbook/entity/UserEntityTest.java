package com.dao.cookbook.entity;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testDefaultValues() {
        UserEntity user = new UserEntity();
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getFullName());
        assertNull(user.getAvatarUrl());
        assertNull(user.getBio());
        assertNull(user.getHometown());
        assertEquals("local", user.getProvider());
        assertEquals(0, user.getFollowersCount());
        assertEquals(0, user.getFollowingCount());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        UserEntity user = new UserEntity();
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String password = "hashedPassword";
        String fullName = "John Doe";
        String avatarUrl = "http://avatar.com/img.png";
        String bio = "Bio";
        String hometown = "Hometown";
        String provider = "google";
        int followersCount = 10;
        int followingCount = 5;
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Timestamp updatedAt = new Timestamp(System.currentTimeMillis());

        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setAvatarUrl(avatarUrl);
        user.setBio(bio);
        user.setHometown(hometown);
        user.setProvider(provider);
        user.setFollowersCount(followersCount);
        user.setFollowingCount(followingCount);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, user.getFullName());
        assertEquals(avatarUrl, user.getAvatarUrl());
        assertEquals(bio, user.getBio());
        assertEquals(hometown, user.getHometown());
        assertEquals(provider, user.getProvider());
        assertEquals(followersCount, user.getFollowersCount());
        assertEquals(followingCount, user.getFollowingCount());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        UUID id = UUID.randomUUID();
        user1.setId(id);
        user2.setId(id);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        String toString = user.toString();
        assertTrue(toString.contains("test@example.com"));
    }
}