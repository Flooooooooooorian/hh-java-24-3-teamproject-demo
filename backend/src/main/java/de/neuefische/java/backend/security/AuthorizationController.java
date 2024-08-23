package de.neuefische.java.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AppUserRepository appUserRepository;

    @GetMapping("/me")
    public AppUser getLoggedInUser(@AuthenticationPrincipal OAuth2User user) {
        return appUserRepository.findById(user.getName())
                .orElseThrow(() -> new NoSuchElementException("User with id : " + user.getName() + " doe not exist."));
    }
}
