package com.laptopstore.application.facade;

import com.laptopstore.application.dto.user.UserDTO;
import com.laptopstore.application.dto.user.UserProfileUpdateDTO;
import com.laptopstore.application.mapper.UserMapper;
import com.laptopstore.business.service.UserService;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserDTO getUserProfile(String username) {
        User user = userService.getUserByUsername(username);
        return userMapper.toUserDTO(user);
    }

    @Transactional
    public UserDTO updateUserProfile(String username, UserProfileUpdateDTO updateDTO) {
        User user = userService.getUserByUsername(username);
        
        User userDetails = new User();
        userDetails.setFullName(updateDTO.getFullName());
        userDetails.setPhone(updateDTO.getPhone());
        userDetails.setAddress(updateDTO.getAddress());
        
        User updatedUser = userService.updateUser(user.getId(), userDetails);
        
        if (updateDTO.getCurrentPassword() != null && updateDTO.getNewPassword() != null) {
            userService.updatePassword(user.getId(), updateDTO.getCurrentPassword(), updateDTO.getNewPassword());
        }
        
        return userMapper.toUserDTO(updatedUser);
    }

    public PagedResponse<UserDTO> searchUsers(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage = userService.searchUsers(keyword, pageable);

        List<UserDTO> content = usersPage.getContent().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(content, usersPage.getNumber(), usersPage.getSize(),
                usersPage.getTotalElements(), usersPage.getTotalPages());
    }

    public UserDTO getUserById(Long id) {
        return userMapper.toUserDTO(userService.getUserById(id));
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        userService.toggleUserStatus(id);
    }
}
