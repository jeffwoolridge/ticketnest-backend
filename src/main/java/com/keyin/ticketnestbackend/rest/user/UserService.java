package com.keyin.ticketnestbackend.rest.user;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing user-related operations.
 */
@Service
public class UserService {

    /**
     * User repository
     */
    private final UserRepository userRepository;

    /**
     * Constructs a UserService with the required repository.
     *
     * @param userRepository repository used for user persistence
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the saved user
     * @throws IllegalArgumentException if email already exists
     */
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the found user
     * @throws IllegalArgumentException if user is not found
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    /**
     * Retrieves all users.
     *
     * @return list of users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param updatedUser the new user data
     * @return the updated user
     * @throws IllegalArgumentException if user is not found
     */
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setRole(updatedUser.getRole());

        return userRepository.save(existingUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @throws IllegalArgumentException if user is not found
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
