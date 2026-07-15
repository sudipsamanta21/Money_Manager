package com.Sudip.Money_Manager.service;


import com.Sudip.Money_Manager.entity.ProfileEntity;
import com.Sudip.Money_Manager.repository.ProfileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
           ProfileEntity existingProfile = profileRepository.findByEmail(email)
                  .orElseThrow(() -> new UsernameNotFoundException("Profile not found: " + email));

        return User.builder()
                  .username(existingProfile.getEmail())
                  .password(existingProfile.getPassword())
                  .authorities(Collections.emptyList()) // You can set roles based on your requirements
                  .build();

    }

}
