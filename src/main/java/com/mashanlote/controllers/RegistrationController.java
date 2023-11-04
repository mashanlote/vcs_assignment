package com.mashanlote.controllers;

import com.mashanlote.model.entities.Registration;
import com.mashanlote.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Registration registration) {
        registrationService.register(registration);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

}
