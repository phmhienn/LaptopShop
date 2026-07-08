package com.laptopstore.business.service;

import com.laptopstore.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    
    User createUser(User user, String roleName);
    
    User getUserById(Long id);
    
    User getUserByUsername(String username);
    
    User getUserByEmail(String email);
    
    User updateUser(Long id, User userDetails);
    
    void updatePassword(Long id, String currentPassword, String newPassword);
    
    void toggleUserStatus(Long id);
    
    Page<User> searchUsers(String keyword, Pageable pageable);
    
    Page<User> getAllUsers(Pageable pageable);
    
    long countActiveUsers();
}
