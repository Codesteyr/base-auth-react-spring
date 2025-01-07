package com.codesteyr.controlpanel.controller;

import jakarta.validation.Valid;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.codesteyr.controlpanel.dto.ReqRes;
import com.codesteyr.controlpanel.dto.UserLoginDTO;
import com.codesteyr.controlpanel.dto.UserRegisterDTO;
import com.codesteyr.controlpanel.dto.UserResponseDTO;
import com.codesteyr.controlpanel.entity.Users;
import com.codesteyr.controlpanel.service.UsersService;

@RestController
public class UserController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> regeister(@Valid @RequestBody UserRegisterDTO req) {
        return ResponseEntity.ok(usersService.register(req));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginDTO req) {
        System.out.println("Default TimeZone: " + TimeZone.getDefault().getID());
        return ResponseEntity.ok(usersService.login(req));
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String jwt = authorizationHeader.replace("Bearer ", "");
            Users user = usersService.getUserFromToken(jwt);

            UserResponseDTO response = new UserResponseDTO();
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setCoin(user.getCoin());
            response.setStatusCode(200);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            UserResponseDTO errorResponse = new UserResponseDTO();
            errorResponse.setMessage("Failed to fetch user data.");
            errorResponse.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        usersService.logout();
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody Users reqres) {
        return ResponseEntity.ok(usersService.updateUser(userId, reqres));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUSer(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersService.deleteUser(userId));
    }

}
