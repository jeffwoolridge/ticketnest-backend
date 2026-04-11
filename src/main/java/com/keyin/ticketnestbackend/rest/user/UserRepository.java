package com.keyin.ticketnestbackend.rest.user;

import com.keyin.ticketnestbackend.rest.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

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

    /**
     * Checks if a user exists with the given email, first name, last name, role, and password.
     * @param email the email address of the user
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param role the role of the user
     * @param password the password of the user
     * @return true if a user with the specified email, first name, last name, role, and password exists, false otherwise
     */
    boolean existsByEmailAndFirstNameAndLastNameAndRoleAndPassword(
            String email,
            String firstName,
            String lastName,
            Role role,
            String password
    );
}
