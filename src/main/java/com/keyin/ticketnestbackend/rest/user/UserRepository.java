package com.keyin.ticketnestbackend.rest.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return the User with the specified email, or null if not found
     */
    User findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check for existence
     * @return true if a user with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
