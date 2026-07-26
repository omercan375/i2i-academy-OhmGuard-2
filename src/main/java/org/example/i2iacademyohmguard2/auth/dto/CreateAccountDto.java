package org.example.i2iacademyohmguard2.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountDto {

    @Email(message = "email is required")
    @NotBlank(message = "email must not be blank")
    @Size(min = 5,max = 255,message = "email must be between 5 and 255")
    private String email;
    @NotBlank(message = "password is required")
    @Size(min = 4,max = 255,message = "password must be between 5 and 255")
    private String password;
    @NotBlank(message = "first name is required")
    @Size(min = 3,max = 100,message = "first name must be between 3 and 255")
    private String firstName;
    @NotBlank(message = "second name is required")
    @Size(min = 2,max = 100,message = "second name must be between 2 and 100")
    private String secondName;


}
