package org.example.i2iacademyohmguard2.users;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.zcommon.JwtService;
import org.example.i2iacademyohmguard2.zcommon.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final JwtService jwtService;
    private final UsersRepo usersRepo;

    /*token dan user ıd al kontrol ettir dön*/
    public UsersTable findByToken(String token) {
        UUID userId = jwtService.extractUserId(token);
        UsersTable findById = usersRepo.findByUserId(userId);
        if (findById == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return findById;
    }
}
