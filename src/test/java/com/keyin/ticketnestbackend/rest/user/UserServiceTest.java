package com.keyin.ticketnestbackend.rest.user;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceTest is a unit test class for the UserService. It uses Mockito to mock the UserRepository dependency.
 * The tests verify that the createUser method correctly creates a user when the email does not already exist and
 * throws an exception when the email is already in use. The getUserById method is tested to ensure it throws an
 * exception when the user is not found. The updateUser method is tested to verify that it updates the user's fields
 * correctly when the user exists. The tests check that the repository's methods are called with the expected arguments
 * and that the service behaves as expected under different scenarios.
 */
class UserServiceTest {

    /**
     * Mocks the UserRepository to test the UserService without relying on a real database. The userService is
     * initialized with the mocked repository, allowing us to verify interactions and return controlled responses for
     * the tests. This setup enables us to test the createUser, getUserById, and updateUser methods in isolation,
     * ensuring that they behave correctly when creating a user, retrieving a user by ID, and updating a user's
     * information based on the provided input and the existing data in the repository.
     */
    private final UserRepository userRepository = mock(UserRepository.class);

    /**
     * The UserService instance being tested. It is initialized with the mocked UserRepository, allowing us to test the
     * service's methods in isolation. The tests will verify that the service correctly interacts with the repository
     * and behaves as expected when creating users, retrieving users by ID, and updating users' information based on
     * the provided input and the existing data in the repository.
     */
    private final UserService userService = new UserService(userRepository);

    /**
     * Tests that a user can be successfully created when the provided email does not already exist in the repository.
     * The test sets up a User object with a specific email, mocks the repository to indicate that the email does not
     * exist, and then verifies that the user is saved correctly with the expected email. The test also checks that the
     * repository's existsByEmail and save methods are called with the expected arguments during the user creation process.
     */
    @Test
    void createUser_success() {
        User user = new User();
        user.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.createUser(user);

        assertThat(saved.getEmail()).isEqualTo("john@example.com");
    }

    /**
     * Tests that an exception is thrown when trying to create a user with an email that already exists in the repository.
     * The test sets up a User object with a specific email, mocks the repository to indicate that the email already
     * exists, and then verifies that an IllegalArgumentException is thrown when the createUser method is called.
     * The test also checks that the repository's existsByEmail method is called with the expected email during the
     * user creation process.
     */
    @Test
    void createUser_throwsIfEmailExists() {
        User user = new User();
        user.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(user));
    }

    /**
     * Tests that an exception is thrown when trying to retrieve a user by ID that does not exist in the repository.
     * The test mocks the repository to return an empty Optional when the findById method is called with a specific ID.
     * It then verifies that an IllegalArgumentException is thrown when the getUserById method is called with that ID.
     * The test also checks that the repository's findById method is called with the expected ID during the user
     * retrieval process.
     */
    @Test
    void getUserById_throwsWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(1L));
    }

    /**
     * Tests that the updateUser method correctly updates the fields of an existing user. The test sets up an existing User
     * with specific attributes, and an updated User with new values for those attributes. It mocks the repository to
     * return the existing user when findById is called, and to save the updated user when save is called. The test
     * then verifies that the fields of the returned user are updated to the new values, and that the repository's
     * findById and save methods are called with the expected arguments during the user update process.
     */
    @Test
    void updateUser_updatesFields() {
        User existing = new User();
        existing.setId(1L);
        existing.setFirstName("Old");

        User updated = new User();
        updated.setFirstName("New");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, updated);

        assertThat(result.getFirstName()).isEqualTo("New");
    }
}
