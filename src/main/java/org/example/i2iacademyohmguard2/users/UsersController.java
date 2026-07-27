package org.example.i2iacademyohmguard2.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.users.dto.CurrentUserDto;
import org.example.i2iacademyohmguard2.users.dto.UpdateMailDto;
import org.example.i2iacademyohmguard2.users.dto.UpdateNameDto;
import org.example.i2iacademyohmguard2.users.dto.UpdatePasswordDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@RequestHeader("Authorization")String token){
        UsersTable findUser = usersService.findByToken(token);
        CurrentUserDto currentUserDto = usersService.userInfo(findUser);
        return ResponseEntity.ok(currentUserDto);
    }

    @PutMapping("/update-email")
    public ResponseEntity<String> updateEmail(@RequestHeader("Authorization") String token,@Valid @RequestBody UpdateMailDto updateMailDto) {
       UsersTable findUser = usersService.findByToken(token);
        usersService.updateEmail(findUser,updateMailDto);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token,@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        UsersTable findUser = usersService.findByToken(token);
        usersService.updatePassword(findUser,updatePasswordDto);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/update-name")
    public ResponseEntity<?> updateName(@RequestHeader("Authorization") String token,@Valid @RequestBody UpdateNameDto updateNameDto) {
        UsersTable findUser = usersService.findByToken(token);
        usersService.updateName(findUser,updateNameDto);
        return ResponseEntity.ok().build();
    }

}
