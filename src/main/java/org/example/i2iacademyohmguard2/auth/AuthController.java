package org.example.i2iacademyohmguard2.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.auth.dto.CreateAccountDto;
import org.example.i2iacademyohmguard2.users.dto.CurrentUserDto;
import org.example.i2iacademyohmguard2.auth.dto.LoginDto;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/create-account")
    public ResponseEntity<String> createAccount(@Valid @RequestBody CreateAccountDto createAccountDto) {
        authService.createAccount(createAccountDto);
        return ResponseEntity.ok("Account created");
    }


    @PostMapping("/admin-enter")
    public ResponseEntity<String> adminEnter() {
        String adminToken = authService.adminEnter();
        return ResponseEntity.ok(adminToken);
    }

}
