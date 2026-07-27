package org.example.i2iacademyohmguard2.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateNameDto {
    @NotBlank(message = "firs name is required")
    @Size(min = 2,max =100)
    private String newFirstName;
    @NotBlank(message = "last name is required")
    @Size(min = 2,max =100)
    private String newLastName;
    @NotBlank(message = "password is required")
    private String newPassword;
}
