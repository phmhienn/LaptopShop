package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.user.AddressDTO;
import com.laptopstore.application.dto.user.UserDTO;
import com.laptopstore.data.entity.Address;
import com.laptopstore.data.entity.Role;
import com.laptopstore.data.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setAvatar(user.getAvatar());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setRecipientName(address.getRecipientName());
        dto.setPhone(address.getPhone());
        dto.setAddressLine(address.getAddressLine());
        dto.setWard(address.getWard());
        dto.setDistrict(address.getDistrict());
        dto.setCity(address.getCity());
        dto.setIsDefault(address.getIsDefault());

        return dto;
    }
}
