package org.example.i2iacademyohmguard2.users;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.users.dto.CurrentUserDto;
import org.example.i2iacademyohmguard2.users.dto.UpdateMailDto;
import org.example.i2iacademyohmguard2.users.dto.UpdateNameDto;
import org.example.i2iacademyohmguard2.users.dto.UpdatePasswordDto;
import org.example.i2iacademyohmguard2.zcommon.JwtService;
import org.example.i2iacademyohmguard2.zcommon.exception.AlreadyExistsException;
import org.example.i2iacademyohmguard2.zcommon.exception.ResourceNotFoundException;
import org.example.i2iacademyohmguard2.zcommon.exception.UpdateException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final JwtService jwtService;
    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;

    /*token dan user ıd al kontrol ettir dön*/
    public UsersTable findByToken(String token) {
        UUID userId = jwtService.extractUserId(token);
        UsersTable findById = usersRepo.findByUserId(userId);
        if (findById == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return findById;
    }



    // kullanıcı bilgilerini al
    public CurrentUserDto userInfo(UsersTable user){
        /*find user*/
        UsersTable findById = usersRepo.findByUserId(user.getId());
        if (findById == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return CurrentUserDto.builder()
                .email(findById.getEmail())
                .firstName(findById.getFirstName())
                .lastName(findById.getLastName())
                .build();

    }

    // emaili değiştir
    @Transactional
    public void updateEmail(UsersTable user, UpdateMailDto updateMailDto) {
        /*email exist control*/
        UsersTable emailExistControl = usersRepo.findByEmail(updateMailDto.getNewEmail());
        if (emailExistControl != null) {
            throw new AlreadyExistsException("Email already exists");
        }
        /*user email confirm*/
        if (!updateMailDto.getOldEmail().equals(updateMailDto.getNewEmail())) {
            throw new IllegalArgumentException("EMAIL DOES NOT MATCH");
        }
        /*password control*/
        boolean matches = passwordEncoder.matches(user.getPasswordHash(), user.getPasswordHash());
        if (!matches) {
            throw new IllegalArgumentException("PASSWORD DOES NOT MATCH");
        }
        /*update email after all confirm*/
        int updateEmail = usersRepo.updateEmail(updateMailDto.getNewEmail(), user.getId(), user.getVersion());
        if (updateEmail == 0) {
            throw new UpdateException("NEW EMAIL CANT UPDATE");
        }

    }

    // şifreyi değiştir
    public void updatePassword(UsersTable user, UpdatePasswordDto updatePasswordDto) {
        /*önceki şifreyi doğrula*/
        boolean matchPassword = passwordEncoder.matches(updatePasswordDto.getNewPassword(), user.getPasswordHash());
        if (!matchPassword) {
            throw new IllegalArgumentException("PASSWORD DOES NOT MATCH");
        }
        /*yeni şifreyi hashle*/
        String hashedPassword = passwordEncoder.encode(updatePasswordDto.getNewPassword());

        /*yeni şifreyi update et*/
        int updatePassword = usersRepo.updatePassword(hashedPassword, user.getId(), user.getVersion());
        if (updatePassword == 0) {
            throw new UpdateException("NEW PASSWORD CANT UPDATE");
        }

    }

    // isimleri değiştir
    public void updateName(UsersTable user, UpdateNameDto updateNameDto) {
        /*password confirm*/
        boolean matches = passwordEncoder.matches(updateNameDto.getNewPassword(), user.getPasswordHash());
        if (!matches) {
            throw new IllegalArgumentException("PASSWORD DOES NOT MATCH");
        }
        int updateNames = usersRepo.updateName(updateNameDto.getNewFirstName(), updateNameDto.getNewLastName(), user.getId(), user.getVersion());
        if (updateNames == 0) {
            throw new UpdateException("NEW NAME CANT UPDATE");
        }

    }
}
