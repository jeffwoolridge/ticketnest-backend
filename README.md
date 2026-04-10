# TicketNest Backend

TicketNest Backend is the Spring Boot API for the **TicketNest Event Ticket Booking App**.  
It powers user authentication, event management, booking creation, and payment processing for the frontend application.

## Overview

This backend was built as part of a full-stack team project using:

- **Java**
- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **MySQL**
- **Maven**

The API supports a simple event booking workflow where:

1. Users register and log in
2. Users browse available events
3. Users create bookings by selecting ticket quantity
4. Users proceed to payment
5. Successful full payment confirms the booking

---

## Features

### Authentication
- User registration
- User login
- Protected API endpoints using username/password authentication

### Users
- Create user
- Get user by ID
- Get all users
- Update user
- Delete user

### Events
- Create event
- Get single event
- Get all events
- Update event
- Delete event

### Bookings
- Create booking
- Get booking by ID
- Get all bookings
- Get bookings by user
- Delete/cancel booking

### Payments
- Create payment
- Get payment by ID
- Get all payments
- Get payments by user

---

## Entity Relationships

The application uses the following entities:

- **User**
- **Event**
- **Booking**
- **Payment**

### Relationships

- One **User** can have many **Bookings**
- One **Event** can have many **Bookings**
- One **Booking** belongs to one **User**
- One **Booking** belongs to one **Event**
- One **Booking** has one **Payment**
- One **Payment** belongs to one **Booking**

### Relationship Diagram

```
User (1) ---- (M) Booking (M) ---- (1) Event
Booking (1) ---- (1) Payment
```

# Submission & Team Information

## Team Members
- Member 1: Abiodun Magret Oyedele
- Member 2: Jeff Wolridge

---

## Project Repositories

- **Backend Repository:**  
    ```
        https://github.com/jeffwoolridge/ticketnest-backend
    ```

- **Frontend Repository:**  
  ```
    https://github.com/Magret1730/ticketnest
  ```

> Ensure repositories are public or shared with instructors.

---

## Task Management Board

- **Project Board / Trello / GitHub Projects:**  
  ```
  https://github.com/users/jeffwoolridge/projects/2/views/1
  ```

> Used for sprint planning, task tracking, and collaboration.

---

## Demo Video

- **Demo Video Link:**  
  ```
  Link Here
  ```


> The demo showcases:
- user authentication  
- event browsing  
- booking flow  
- payment flow  
- admin functionality  
- deployed application (AWS)  

---

## Testing

### Backend Unit Testing
The backend includes unit tests covering:

- user creation  
- event creation  
- booking creation  
- ticket availability validation  
- total price calculation  
- payment validation  
- ticket deduction after successful payment  

Run tests using:

```
mvn test
```

### Javadoc
```
~/(file-path)/ticketnest-backend/javadoc/index.html
```