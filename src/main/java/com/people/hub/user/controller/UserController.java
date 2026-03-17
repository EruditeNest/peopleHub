package com.people.hub.user.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.user.dto.UserDto;
import com.people.hub.user.model.User;
import com.people.hub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        log.info("POST /api/users - Creating user with username: '{}'", userDto.getUsername());
        User created = userService.createUser(userDto);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("PUT /api/users/{} - Updating user", userId);
        User updated = userService.updateUser(userId, userDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getAllUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "created_at") String sortField,
        @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        log.info("GET /api/users - Fetching all users | page: {}, size: {}, sortField: {}, sortOrder: {}",
                page, size, sortField, sortOrder);
        RestApiResponse response = userService.getAllUsers(page, size, sortField, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-roles")
    public ResponseEntity<RestApiResponse> getAllUsersByRoleIds(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "created_at") String sortField,
        @RequestParam(defaultValue = "desc") String sortOrder,
        @RequestBody Set<Long> roleIds
    ) {
        log.info("GET /api/users/by-roles - Fetching users by roleIds: {} | page: {}, size: {}, sortField: {}, sortOrder: {}",
                roleIds, page, size, sortField, sortOrder);
        RestApiResponse response = userService.getAllUsersByRoleIds(page, size, sortField, sortOrder, roleIds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RestApiResponse> getUserById(@PathVariable Long userId) {
        log.info("GET /api/users/{} - Fetching user by id", userId);
        RestApiResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-username")
    public ResponseEntity<RestApiResponse> getUserByUsername(@RequestParam String username) {
        log.info("GET /api/users/by-username - Fetching user with username: '{}'", username);
        RestApiResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-email")
    public ResponseEntity<RestApiResponse> getUserByEmail(@RequestParam String email) {
        log.info("GET /api/users/by-email - Fetching user with email: '{}'", email);
        RestApiResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delete/{userId}")
    public ResponseEntity<RestApiResponse> deleteUser(@PathVariable Long userId) {
        log.info("DELETE /api/users/{} - Soft deleting user", userId);
        RestApiResponse response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }
}