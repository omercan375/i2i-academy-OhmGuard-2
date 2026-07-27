package org.example.i2iacademyohmguard2.users.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentUserDto {

    private String email;
    private String firstName;
    private String lastName;

}
