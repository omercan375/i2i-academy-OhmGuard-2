package org.example.i2iacademyohmguard2.auth;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.auth.dto.CreateAccountDto;
import org.example.i2iacademyohmguard2.auth.dto.LoginDto;
import org.example.i2iacademyohmguard2.users.UsersRepo;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.example.i2iacademyohmguard2.zcommon.JwtService;
import org.example.i2iacademyohmguard2.zcommon.exception.AlreadyExistsException;
import org.example.i2iacademyohmguard2.zcommon.exception.InvalidResourceException;
import org.example.i2iacademyohmguard2.zcommon.exception.ResourceNotFoundException;
import org.example.i2iacademyohmguard2.zcommon.exception.SaveException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(LoginDto loginDto) {
        /*email exist control*/
        UsersTable findByEmail = usersRepo.findByEmail(loginDto.getEmail());
        if (findByEmail == null) {
            throw new ResourceNotFoundException("EMAIL DOES NOT EXIST");
        }
        /*match hashed password */
        boolean matchPassword = passwordEncoder.matches(loginDto.getPassword(), findByEmail.getPasswordHash());
        if (!matchPassword) {
            throw new InvalidResourceException("PASSWORD DOES NOT MATCH");
        }
        return jwtService.generateToken(findByEmail.getId());
    }
    public void createAccount(CreateAccountDto createAccountDto) {
        /*email exist control*/
        UsersTable findByEmail = usersRepo.findByEmail(createAccountDto.getEmail());
        if (findByEmail != null) {
            throw new AlreadyExistsException("EMAIL ALREADY EXISTS");
        }
        String hashedPassword = passwordEncoder.encode(createAccountDto.getPassword());
        UsersTable newUser = UsersTable.builder()
                .email(createAccountDto.getEmail())
                .passwordHash(hashedPassword)
                .firstName(createAccountDto.getFirstName())
                .lastName(createAccountDto.getSecondName())
                .build();

        try{
            usersRepo.save(newUser);
        }catch (DataIntegrityViolationException e) {
            throw new SaveException("user cant save");
        }
    }
}
