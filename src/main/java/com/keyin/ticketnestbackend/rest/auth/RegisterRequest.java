package com.keyin.ticketnestbackend.rest.auth;

public record RegisterRequest(String email, String password, String firstName, String lastName) {}