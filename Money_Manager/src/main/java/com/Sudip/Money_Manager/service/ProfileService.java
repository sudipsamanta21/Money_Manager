package com.Sudip.Money_Manager.service;


import com.Sudip.Money_Manager.dataTransferObject.AuthDTO;
import com.Sudip.Money_Manager.dataTransferObject.ProfileDTO;
import com.Sudip.Money_Manager.entity.ProfileEntity;
import com.Sudip.Money_Manager.repository.ProfileRepository;
import com.Sudip.Money_Manager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;



    @Value("${money.manager.backend.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {

        if (profileRepository.existsByEmail(profileDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        String activationLink =
                activationURL+"/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Money Manager Account";

        String body = String.format("""
        <!DOCTYPE html>
            <html>
                <body style="margin:0;padding:40px;background:#f4f6f8;font-family:Arial,sans-serif;">

               <div style="max-width:600px;margin:auto;background:#ffffff;border-radius:12px;padding:40px;text-align:center;box-shadow:0 2px 10px rgba(0,0,0,.1);">

               <h1 style="color:#2c3e50;">Money Manager</h1>

               <h2 style="color:#333;">Welcome, %s 👋</h2>

               <p style="font-size:16px;color:#555;">
                   Thank you for registering.
                   Please click the button below to activate your account.
               </p>

               <a href="%s"
                   style="
                   display:inline-block;
                   margin:30px 0;
                   padding:15px 35px;
                   background:#4CAF50;
                   color:#ffffff;
                   text-decoration:none;
                   font-size:18px;
                   font-weight:bold;
                   border-radius:8px;">
                   Activate Account
               </a>

               <p style="font-size:14px;color:#777;">
                  If the button doesn't work, copy and paste this link into your browser:
               </p>

               <p style="word-break:break-all;color:#2196F3;">
               %s
               </p>

               <hr>

               <p style="font-size:13px;color:#999;">
               © 2026 Money Manager. All rights reserved.
               </p>

             </div>

            </body>
        </html>
        """,
                newProfile.getFullName(),
                activationLink,
                activationLink);

        try {
            emailService.sendEmail(newProfile.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
        return toDTO(newProfile);
    }





    public ProfileEntity toEntity(ProfileDTO profileDTO) {

        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())

                .build();
    }
    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setActive(true);
                    profile.setActivationToken(null);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found : " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if(email == null) {
            currentUser = getCurrentUserProfile();
        }else {
            currentUser= profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found : " + email));
        }
        return ProfileDTO .builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }


    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authDTO.getEmail(),
                        authDTO.getPassword()));

        String token = jwtUtil.generateToken(authDTO.getEmail());

        System.out.println("TOKEN = " + token);

        return Map.of(
                "token", token,
                "user", getPublicProfile(authDTO.getEmail())
        );
    }
}
