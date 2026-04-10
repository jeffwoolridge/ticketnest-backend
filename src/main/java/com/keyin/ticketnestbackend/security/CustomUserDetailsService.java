package com.keyin.ticketnestbackend.security;

import com.keyin.ticketnestbackend.rest.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService is a service that implements UserDetailsService
 * to load user details from the database.
 * It uses the UserRepository to find a user by their email and returns an
 * AppUserDetails object that Spring Security can use for authentication and authorization.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    /**
     * The UserRepository is injected into the CustomUserDetailsService
     * to access user data from the database.
     */
    private final UserRepository repo;

    /**
     * Constructor for CustomUserDetailsService that takes a UserRepository as a parameter.
     * @param repo the UserRepository to access user data from the database
     */
    public CustomUserDetailsService(UserRepository repo) { this.repo = repo; }

    /**
     * Loads the user details by their email. If the user is not found,
     * it throws a UsernameNotFoundException.
     * @param email the username identifying the user whose data is required.
     * @return a fully populated UserDetails object (never null)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = repo.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException("User not found");
        return new AppUserDetails(user);
    }
}

