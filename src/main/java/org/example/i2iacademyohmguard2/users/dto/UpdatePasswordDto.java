package org.example.i2iacademyohmguard2.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordDto {
    @NotBlank(message = "password is required")
    private String oldPassword;
    @NotBlank(message = "password is required")
    private String newPassword;
}
