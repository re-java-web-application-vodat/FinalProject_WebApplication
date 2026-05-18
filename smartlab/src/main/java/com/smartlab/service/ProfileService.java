package com.smartlab.service;

import com.smartlab.entity.User;
import com.smartlab.entity.UserProfile;
import com.smartlab.repository.UserProfileRepository;
import com.smartlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    public UserProfile getProfile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        return profileRepository
                .findByUserId(user.getId())
                .orElse(new UserProfile());
    }

    public void updateProfile(String username, UserProfile profile) {
        User user = userRepository.findByUsername(username).orElseThrow();

        profile.setUser(user);

        profileRepository.save(profile);
    }
}
