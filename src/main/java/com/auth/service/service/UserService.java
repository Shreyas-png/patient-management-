package com.auth.service.service;

import com.auth.service.dto.*;
import com.auth.service.entity.User;
import com.auth.service.exception.InvalidOperationException;
import com.auth.service.exception.InvalidPasswordException;
import com.auth.service.exception.ResourceNotFoundException;
import com.auth.service.exception.ResourceAlreadyExistsException;
import com.auth.service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<GetUserResponseDto> getAllUsers(String role){
        List<User> allAdmins = userRepository.findUsersByRole(role);

        List<GetUserResponseDto> allAdminsDto = new ArrayList<>();
        for(User admin : allAdmins){
            allAdminsDto.add(
                    GetUserResponseDto.builder()
                            .email(admin.getEmail())
                            .role(admin.getRole())
                            .build()
            );
        }
        return allAdminsDto;
    }

    public GetUserResponseDto getUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        return GetUserResponseDto.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto){
        if(userRepository.existsUserByEmail(createUserRequestDto.getEmail()))
            throw  new ResourceAlreadyExistsException("User with %s email already exists".formatted(createUserRequestDto.getEmail()));

        User user = User.builder()
                .email(createUserRequestDto.getEmail())
                .password(passwordEncoder.encode(createUserRequestDto.getPassword()))
                .role("ROLE_" + createUserRequestDto.getRole())
                .build();
        User savedUser = userRepository.save(user);

        return CreateUserResponseDto.builder()
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    @Transactional
    public UpdateUserResponseDto updateUser(String email, UpdateUserRequestDto updateUserRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with email %s not found".formatted(email)));

        boolean isUpdated = false;

        // Update email if provided and different
        if (updateUserRequestDto.getEmail() != null &&
                !updateUserRequestDto.getEmail().equals(user.getEmail())) {

            if (userRepository.existsUserByEmail(updateUserRequestDto.getEmail())) {
                throw new ResourceAlreadyExistsException(
                        "User with email %s already exists".formatted(updateUserRequestDto.getEmail()));
            }
            user.setEmail(updateUserRequestDto.getEmail());
            isUpdated = true;
        }

        // Update password if provided and different
        if (updateUserRequestDto.getPassword() != null &&
                !updateUserRequestDto.getPassword().isBlank()) {

            // Check if new password is same as old password
            if (passwordEncoder.matches(updateUserRequestDto.getPassword(), user.getPassword())) {
                throw new InvalidPasswordException("New password cannot be the same as old password");
            }

            user.setPassword(passwordEncoder.encode(updateUserRequestDto.getPassword()));
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new InvalidOperationException("No changes detected");
        }

        User updatedUser = userRepository.save(user);

        return UpdateUserResponseDto.builder()
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .message("User updated successfully")
                .build();
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email %s not found".formatted(email)));

        userRepository.delete(user);
    }

}

