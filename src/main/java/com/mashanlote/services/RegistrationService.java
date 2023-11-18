package com.mashanlote.services;

import com.mashanlote.model.entities.Registration;
import com.mashanlote.model.entities.User;
import com.mashanlote.model.exceptions.ConflictException;
import com.mashanlote.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository repository;

    public RegistrationService(UserRepository repository) {
        this.repository = repository;
    }

    public void register(Registration registration) {
        if (repository.existsByName(registration.name())) {
            throw new ConflictException();
        }
        var user = User.builder()
                .role("USER")
                .password(registration.password())
                .name(registration.name())
                .build();
        repository.save(user);
    }
}
