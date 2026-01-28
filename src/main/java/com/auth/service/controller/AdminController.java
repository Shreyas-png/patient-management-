package com.auth.service.controller;

import com.auth.service.dto.*;
import com.auth.service.entity.User;
import com.auth.service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("auth/admin")
public class AdminController {

    private final UserService userService;
    public AdminController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateUserResponseDto> creatAdmin(@Valid @RequestBody CreateUserRequestDto createUserRequestDto){
        //overriding the role value even if passed in request
        createUserRequestDto.setRole("ADMIN");
        CreateUserResponseDto createdUser = userService.createUser(createUserRequestDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GetUserResponseDto> getAdmin(Principal principal){
        String email = principal.getName();

        GetUserResponseDto user = userService.getUser(email);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<GetUserResponseDto>> getAllAdmin(){
        List<GetUserResponseDto> allAdmins = userService.getAllUsers("ROLE_ADMIN");
        return new ResponseEntity<>(allAdmins, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UpdateUserResponseDto> updateAdmin(@Valid @RequestBody UpdateUserRequestDto updateUserRequestDto, Authentication authentication) {

        log.info("request has reached update controller");
        String email = authentication.getName();
        UpdateUserResponseDto responseDto = userService.updateUser(email, updateUserRequestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMyAccount(Authentication authentication) {

        String currentUserEmail = authentication.getName();
        userService.deleteUser(currentUserEmail);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
