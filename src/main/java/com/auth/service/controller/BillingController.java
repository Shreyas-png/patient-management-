package com.auth.service.controller;

import com.auth.service.dto.*;
import com.auth.service.entity.User;
import com.auth.service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("auth/billing")
public class BillingController {

    private final UserService userService;
    public BillingController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateUserResponseDto> creatBillingUser(@Valid @RequestBody CreateUserRequestDto createUserRequestDto){
        //overriding the role value even if passed in request
        createUserRequestDto.setRole("BILLING");
        CreateUserResponseDto createdUser = userService.createUser(createUserRequestDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GetUserResponseDto> getBillingUser(Principal principal){
        String email = principal.getName();
        GetUserResponseDto user = userService.getUser(email);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<GetUserResponseDto>> getAllBillingUsers(){
        List<GetUserResponseDto> allBillingUsers = userService.getAllUsers("ROLE_BILLING");
        return new ResponseEntity<>(allBillingUsers, HttpStatus.OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('BILLING')")
    public ResponseEntity<UpdateUserResponseDto> updateBillingUser(@Valid @RequestBody UpdateUserRequestDto updateUserRequestDto, Authentication authentication) {

        String email = authentication.getName();
        UpdateUserResponseDto responseDto = userService.updateUser(email, updateUserRequestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('BILLING')")
    public ResponseEntity<String> deleteMyAccount(Authentication authentication) {

        String currentUserEmail = authentication.getName();
        userService.deleteUser(currentUserEmail);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
