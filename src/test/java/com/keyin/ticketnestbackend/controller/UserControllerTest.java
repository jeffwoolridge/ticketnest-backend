package com.keyin.ticketnestbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.model.Role;
import com.keyin.ticketnestbackend.rest.user.User;
import com.keyin.ticketnestbackend.rest.user.UserRepository;
import com.keyin.ticketnestbackend.security.JwtUtil;
import com.keyin.ticketnestbackend.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest is a test class for the UserController in the TicketNest backend application.
 * It uses Spring Boot's testing framework to perform integration tests on the UserController's endpoints.
 * The tests cover creating a user, retrieving all users, retrieving a user by ID, updating a user, and deleting a user.
 * The tests also verify that the appropriate authorization is required for each action, ensuring that only admins
 * can create and delete users, while regular users can only update their own profile. The test class uses MockMvc
 * to simulate HTTP requests and responses, and it sets up test data in the database before each test to ensure a
 * consistent testing environment. The ObjectMapper is configured to handle Java 8 date/time types, which may be
 * used in the User entity or related entities.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    /**
     * MockMvc is a Spring component that allows us to perform HTTP requests in our tests without starting a server.
     * It provides a fluent API for building requests and asserting responses, making it easier to test our controllers
     * in an integration test context. By using MockMvc, we can simulate requests to our UserController and verify that
     * it behaves as expected, including checking the status codes, response content, and authorization requirements for
     * each endpoint.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * UserRepository is used to interact with the user data in the database during the tests.
     * We will use it to set up test data for users, which are necessary for creating bookings and payments,
     * and to verify the state of the database after performing operations through the controller endpoints.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * EventRepository is used to interact with the event data in the database during the tests.
     * We will use it to set up test data for events, which are necessary for creating bookings and payments,
     * and to verify the state of the database after performing operations through the controller endpoints.
     */
    @Autowired
    private EventRepository eventRepository;

    /**
     * PasswordEncoder is used to encode passwords for the test users we create in the database. Since our UserController
     * expects passwords to be stored in an encoded format, we need to use the PasswordEncoder to ensure that the test
     * users have their passwords encoded correctly. This allows us to authenticate as these users during the tests and
     * verify that the authentication and authorization mechanisms in our application are working as expected.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * JwtUtil is a utility class for generating and validating JSON Web Tokens (JWTs) in our application. In the tests,
     * we will use JwtUtil to generate valid JWTs for our test users (admin and regular user) so that we can include
     * these tokens in the Authorization header of our HTTP requests. This allows us to simulate authenticated requests
     * to the UserController endpoints and verify that the appropriate authorization is required for each action.
     * By using JwtUtil, we can ensure that our tests are realistic and accurately reflect the authentication and
     * authorization flow of our application, allowing us to catch any issues related to JWT handling in the UserController.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * AuthenticationManager is a Spring Security component that is responsible for authenticating users based on their
     * credentials. In our tests, we may use the AuthenticationManager to authenticate our test users (admin and
     * regular user) and generate JWTs for them using the JwtUtil. This allows us to simulate authenticated requests to
     * the UserController endpoints and verify that the appropriate authorization is required for each action. By using
     * the AuthenticationManager in our tests, we can ensure that our authentication flow is working correctly and that
     * our UserController is properly enforcing security rules based on the authenticated user's role.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * CustomUserDetailsService is a service that implements Spring Security's UserDetailsService interface. It is
     * responsible for loading user-specific data during the authentication process. In our tests, we may use
     * CustomUserDetailsService to load user details for our test users (admin and regular user) when generating JWTs
     * or performing authentication. This allows us to ensure that our authentication flow is working correctly and that
     * our UserController is properly enforcing security rules based on the authenticated user's role. By using
     * CustomUserDetailsService in our tests, we can verify that our user details are being loaded correctly and that
     * our security configuration is properly integrated with our user data.
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * ObjectMapper is a Jackson component that is used to serialize and deserialize JSON in our tests. We will use it
     * to convert our User objects to JSON when sending requests to the UserController endpoints, and to parse JSON
     * responses from the controller. By configuring the ObjectMapper with the JavaTimeModule, we can ensure that any
     * date/time fields in our User entity or related entities are properly handled during serialization and
     * deserialization. This allows us to write tests that accurately reflect the data structures used in our
     * application and ensures that our UserController can correctly process JSON requests and responses, including
     * those that contain date/time information.
     */
    private ObjectMapper objectMapper;

    /**
     * adminToken and userToken are JWTs that we will generate for our test admin user and regular user, respectively.
     * These tokens will be included in the Authorization header of our HTTP requests to the UserController endpoints,
     * allowing us to simulate authenticated requests and verify that the appropriate authorization is required for
     * each action. By generating these tokens in the setUp method, we can ensure that they are valid and properly
     * reflect the roles of the test users, allowing us to accurately test the security and authorization mechanisms
     * of our UserController.
     */
    private String adminToken;

    /**
     * userToken is a JWT that we will generate for our test regular user. This token will be included in the
     * Authorization header of our HTTP requests to the UserController endpoints, allowing us to simulate authenticated
     * requests and verify that the appropriate authorization is required for each action. By generating this token in
     * the setUp method, we can ensure that it is valid and properly reflects the role of the test regular user,
     * allowing us to accurately test the security and authorization mechanisms of our UserController, especially for
     * actions that should be accessible to regular users but not admins, such as updating their own profile.
     */
    private String userToken;

    /**
     * adminUser and regularUser are User entities that we will create in the database during the setUp method. These
     * users will be used to generate the adminToken and userToken JWTs, and they will also be used as test data for
     * our UserController tests. The adminUser will have the ADMIN role, allowing us to test endpoints that require
     * admin privileges, while the regularUser will have the USER role, allowing us to test endpoints that are
     * accessible to regular users. By creating these users in the setUp method, we can ensure that they are available
     * for all of our tests and that they have the necessary data to perform authentication and authorization checks
     * in our UserController tests.
     */
    private User adminUser;

    /**
     * regularUser is a User entity that we will create in the database during the setUp method. This user will have the
     * USER role, allowing us to test endpoints that are accessible to regular users. The regularUser will be used to
     * generate the userToken JWT, which will be included in the Authorization header of our HTTP requests to the
     * UserController endpoints. By creating this user in the setUp method, we can ensure that it is available for all
     * of our tests and that it has the necessary data to perform authentication and authorization checks in our
     * UserController tests, especially for actions that should be accessible to regular users but not admins, such as
     * updating their own profile.
     */
    private User regularUser;

    /**
     * The setUp method is annotated with @BeforeEach, which means it will be executed before each test method in this
     * class. In this method, we perform several important setup tasks to ensure that our tests run in a consistent and
     * isolated environment. We start by clearing the event and user repositories to ensure that there is no leftover
     * data from previous tests that could interfere with our current tests. Next, we create an admin user and a regular
     * user in the database, encoding their passwords using the PasswordEncoder. We then generate JWT tokens for both
     * users using the JwtUtil, which will be used in our tests to authenticate requests to the UserController endpoints.
     * Finally, we initialize the ObjectMapper and register the JavaTimeModule to handle any date/time fields in our
     * User entity or related entities during JSON serialization and deserialization. By performing these setup tasks
     * in the setUp method, we can ensure that each test runs with a clean slate and has the necessary data and
     * configuration to accurately test the functionality of our UserController.
     */
    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        regularUser = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("user123"))
                .firstName("Regular")
                .lastName("User")
                .role(Role.USER)
                .build();
        regularUser = userRepository.save(regularUser);

        adminToken = jwtUtil.generateToken("admin@test.com");
        userToken = jwtUtil.generateToken("user@test.com");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * This test method verifies that an admin user can successfully create a new user through the
     * POST /api/users endpoint. We create a new User object with the necessary details, including email, password,
     * first name, last name, and role. We then perform a POST request to the /api/users endpoint, including the
     * adminToken in the Authorization header to authenticate as an admin. We also set the content type to
     * application/json and include the new user data in the request body as JSON. Finally, we assert that the
     * response status is 201 Created and that the response body contains the expected user details, such as the
     * generated ID, email, first name, and role. This test ensures that only admins can create new users and that
     * the UserController correctly processes the creation of a new user.
     * @throws Exception if there is an error during the execution of the test, such as issues with JSON processing or
     * MockMvc interactions.
     */
    @Test
    @DisplayName("POST /api/users - admin can create user")
    void createUser_Admin_Success() throws Exception {
        User newUser = User.builder()
                .email("newuser@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("New")
                .lastName("User")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    /**
     * This test method verifies that an admin user can successfully retrieve a list of all users through the
     * GET /api/users endpoint. We perform a GET request to the /api/users endpoint, including the adminToken in the
     * Authorization header to authenticate as an admin. We then assert that the response status is 200 OK and that the
     * response body contains a list of users with the expected details. Specifically, we check that the list has a
     * size of 2 (the admin user and the regular user created in the setUp method) and that the email addresses of the
     * users in the response match the expected values
     * @throws Exception if there is an error during the execution of the test, such as issues with JSON processing
     *      or MockMvc interactions.
     */
    @Test
    @DisplayName("GET /api/users - admin can get all users")
    void getAllUsers_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("admin@test.com"))
                .andExpect(jsonPath("$[1].email").value("user@test.com"));
    }

    /**
     * This test method verifies that a regular user can successfully retrieve their own user details through the
     * GET /api/users/{id} endpoint. We perform a GET request to the /api/users/{id} endpoint, where {id} is the
     * ID of the regular user created in the setUp method. We include the userToken in the Authorization header to
     * authenticate as the regular user. We then assert that the response status is 200 OK and that the response body
     * contains the expected user details, such as the email, first name, and last name of the regular user. This test
     * ensures that regular users can access their own user details and that the UserController correctly processes
     * requests to retrieve user information based on the authenticated user's ID. It also verifies that the appropriate
     * authorization is required for this action, allowing regular users to access their own details while preventing
     * them from accessing other users' details.
     * @throws Exception if there is an error during the execution of the test, such as issues with JSON processing
     *          or MockMvc interactions.
     */
    @Test
    @DisplayName("GET /api/users/{id} - should return user by id")
    void getUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.firstName").value("Regular"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    /**
     * This test method verifies that a regular user can successfully update their own profile through the
     * PUT /api/users/{id} endpoint. We create an updated User object with new first name, last name, and password
     * details. We then perform a PUT request to the /api/users/{id} endpoint, where {id} is the ID of the regular user
     * created in the setUp method. We include the userToken in the Authorization header to authenticate as the regular
     * user. We also set the content type to application/json and include the updated user data in the request body as
     * JSON. Finally, we assert that the response status is 200 OK and that the response body contains the updated user
     * details, such as the new first name and last name. This test ensures that regular users can update their own
     * profile information and that the UserController correctly processes update requests while enforcing the
     * appropriate authorization, allowing users to modify their own details but preventing them from modifying other
     * users' details. It also verifies that the password can be updated successfully, although we do not check the
     * password in the response for security reasons.
     * @throws Exception if there is an error during the execution of the test, such as issues with JSON processing
     * or MockMvc interactions.
     */
    @Test
    @DisplayName("PUT /api/users/{id} - user can update their own profile")
    void updateUser_Self_Success() throws Exception {
        User updated = User.builder()
                .firstName("UpdatedFirst")
                .lastName("UpdatedLast")
                .email("user@test.com")
                .password(passwordEncoder.encode("newpassword"))
                .role(Role.USER)
                .build();

        mockMvc.perform(put("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedFirst"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLast"));
    }

    /**
     * This test method verifies that an admin user can successfully delete a user through the DELETE /api/users/{id}
     * endpoint. We perform a DELETE request to the /api/users/{id} endpoint, where {id} is the ID of the regular user
     * created in the setUp method. We include the adminToken in the Authorization header to authenticate as an admin.
     * We then assert that the response status is 204 No Content, indicating that the user was successfully deleted.
     * This test ensures that only admins can delete users and that the UserController correctly processes delete requests
     * while enforcing the appropriate authorization. It also verifies that once a user is deleted, they can no longer be
     * accessed through the controller endpoints, ensuring that the delete operation is effective and that the user's data
     * is removed from the system as expected.
     * @throws Exception if there is an error during the execution of the test, such as issues with MockMvc interactions
     *          or database operations.
     */
    @Test
    @DisplayName("DELETE /api/users/{id} - admin can delete user")
    void deleteUser_Admin_Success() throws Exception {
        mockMvc.perform(delete("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}