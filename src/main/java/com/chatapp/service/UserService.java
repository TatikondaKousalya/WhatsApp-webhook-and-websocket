package com.chatapp.service;

import com.chatapp.data.entity.User;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.dto.request.UpdateUserRequest;
import com.chatapp.exception.BadRequestException;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    // Get User By Id
    public User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    //Get Current User
    public User getCurrentUser() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    //Get All Users
    public List<User> getAllUsers() {

        return userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getDeleted())).toList();
    }

    //Update Profile
    public User updateProfile(UpdateUserRequest request) {

        User user = getCurrentUser();

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(user.getPhoneNumber())) {

            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new BadRequestException("Phone number already exists.");
            }

            user.setPhoneNumber(request.getPhoneNumber());
        }

        return userRepository.save(user);
    }

    //Update Profile Picture
    public User updateProfilePicture(String imagePath) {

        User user = getCurrentUser();
        user.setProfilePicture(imagePath);
        return userRepository.save(user);
    }

    //Search Users
    public List<User> searchUsers(String keyword) {

        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(keyword, keyword, keyword
        );
    }

    //Soft Delete User
    public void deleteUser(Long userId) {

        User user = getUser(userId);
        user.setDeleted(true);
        user.setEnabled(false);

        userRepository.save(user);
    }
}