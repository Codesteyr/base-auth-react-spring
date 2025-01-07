package com.codesteyr.controlpanel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codesteyr.controlpanel.dto.ReqRes;
import com.codesteyr.controlpanel.dto.UserLoginDTO;
import com.codesteyr.controlpanel.dto.UserRegisterDTO;
import com.codesteyr.controlpanel.dto.UserResponseDTO;
import com.codesteyr.controlpanel.entity.Users;
import com.codesteyr.controlpanel.repository.UsersRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDTO register(UserRegisterDTO registrationRequest) {
        UserResponseDTO response = new UserResponseDTO();
        String defaultRole = "USER";

        // test
        if (usersRepo.existsByEmail(registrationRequest.getEmail())) {
            response.setStatusCode(400);
            response.setMessage("Email is already in use");
            return response;
        }

        // test
        if (usersRepo.existsByName(registrationRequest.getName())) {
            response.setStatusCode(400);
            response.setMessage("Name is already in use");
            return response;
        }

        try {
            Users user = new Users();
            user.setEmail(registrationRequest.getEmail());
            user.setRole(defaultRole);
            user.setCoin(0);
            user.setAgreeToRules(registrationRequest.getAgreeToRules());
            user.setName(registrationRequest.getName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            Users UsersResult = usersRepo.save(user);

            if (UsersResult.getId() > 0) {
                var jwt = jwtUtils.generateToken(UsersResult);
                var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), UsersResult);
                response.setToken(jwt);
                response.setRefreshToken(refreshToken);
                response.setRole(user.getRole());
                response.setMessage("User Saved Successfully");
                response.setStatusCode(200);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public UserResponseDTO login(UserLoginDTO loginRequest) {
        UserResponseDTO response = new UserResponseDTO();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setCoin(user.getCoin());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Users getUserFromToken(String token) {
        String email = jwtUtils.extractUserEmail(token);
        return usersRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUserEmail(refreshTokenReqiest.getToken());
            Users users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<Users> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            Users usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, Users updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                Users existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                Users savedUser = usersRepo.save(existingUser);
                reqRes.setUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }
}
