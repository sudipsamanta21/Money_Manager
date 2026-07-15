package com.Sudip.Money_Manager.dataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {
    private String fullName;
    private String email;
    private String password;
    private String profileImageUrl;
    private Boolean isActive;
    private String activationToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long id;
}
