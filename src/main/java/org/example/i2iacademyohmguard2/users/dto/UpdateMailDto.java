package org.example.i2iacademyohmguard2.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMailDto {
    @NotBlank(message = "email is required")
    @Size(min = 5,max = 255,message = "email must be between 5 and 255")
    private String oldEmail;
    @NotBlank(message = "email is required")
    @Size(min = 5,max = 255,message = "emai must be berween 5 and 255")
    private String newEmail;
    @NotBlank(message = "password is required")
    private String password;


}
