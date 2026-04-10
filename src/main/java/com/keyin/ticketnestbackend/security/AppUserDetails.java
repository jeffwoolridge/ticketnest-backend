package com.keyin.ticketnestbackend.security;

import com.keyin.ticketnestbackend.rest.model.Role;
import com.keyin.ticketnestbackend.rest.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * AppUserDetails is a custom implementation of Spring Security's UserDetails interface.
 * It wraps our User entity and provides necessary information for authentication and
 * authorization. The getAuthorities method converts the user's role into a
 * GrantedAuthority format that Spring Security can understand.
 * The other methods indicate that the user's account is active and valid.
 */
@Getter
public class AppUserDetails implements UserDetails {
    /**
     * The User entity that this AppUserDetails wraps.
     * It contains the user's email, password, and role.
     * This information is used to authenticate the user and determine their permissions.
     */
    private final User user;

    /**
     * Constructor that initializes the AppUserDetails with a User entity.
     * @param user the User entity to wrap.
     *             This should contain the user's email, password, and role.
     */
    public AppUserDetails(User user) { this.user = user; }

    /**
     * Returns a collection of authorities granted to the user. In this implementation,
     * we convert the user's role into a SimpleGrantedAuthority with the prefix "ROLE_".
     * @return a collection of GrantedAuthority objects representing the user's permissions.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Returns the password used to authenticate the user. In this implementation, we return
     * the password from the wrapped User entity.
     * @return the user's password.
     */
    @Override
    public String getPassword() { return user.getPassword(); }

    /**
     * Returns the username used to authenticate the user. In this implementation, we return
     * the email from the wrapped User entity, as we are using email as the username.
     * @return the user's email.
     */
    @Override
    public String getUsername() { return user.getEmail(); }

    /**
     * Indicates whether the user's account has expired. In this implementation, we return true,
     * indicating that the account is valid and has not expired.
     * @return true if the account has not expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Indicates whether the user's account is locked. In this implementation, we return true,
     * indicating that the account is not locked and is valid.
     * @return true if the account is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indicates whether the user's credentials (password) has expired. In this implementation, we return true,
     * indicating that the credentials are valid and have not expired.
     * @return true if the credentials have not expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indicates whether the user is enabled or disabled. In this implementation, we return true,
     * indicating that the user is enabled and can authenticate.
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() { return true; }

}

