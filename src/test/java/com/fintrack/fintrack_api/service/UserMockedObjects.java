package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.model.enums.Role;

import java.util.List;
import java.util.Random;

public class UserMockedObjects {

    public static Users mockUser() {
        return Users.builder()
                .id(1L)
                .email("user@testexample.com")
                .password("password")
                .firstName("Name")
                .lastName("Surname")
                .phoneNumber("+1234567890")
                .roles(List.of(
                        Role.USER.getValue()
                ))
                .enabled(true)
                .build();
    }

    public static Users mockAdminUser() {
        return Users.builder()
                .id(2L)
                .email("admin@testexample.com")
                .password("admin")
                .firstName("Admin Name")
                .lastName("Admin Surname")
                .phoneNumber("+9876543210")
                .roles(List.of(
                        Role.USER.getValue(),
                        Role.ADMIN.getValue()
                ))
                .enabled(true)
                .build();
    }
}
