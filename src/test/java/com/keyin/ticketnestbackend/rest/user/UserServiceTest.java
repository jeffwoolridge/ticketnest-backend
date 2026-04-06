package com.keyin.ticketnestbackend.rest.user;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);

    @Test
    void createUser_success() {
        User user = new User();
        user.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.createUser(user);

        assertThat(saved.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void createUser_throwsIfEmailExists() {
        User user = new User();
        user.setEmail("john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(user));
    }

    @Test
    void getUserById_throwsWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(1L));
    }

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
