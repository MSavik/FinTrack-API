package com.fintrack.fintrack_api.mapper;

import com.fintrack.fintrack_api.dto.response.AdminUserProfileResponseDTO;
import com.fintrack.fintrack_api.dto.response.UserProfileResponseDTO;
import com.fintrack.fintrack_api.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileResponseDTO toUserProfileResponseDTO(Users user);

    @Mapping(target = "profile", source = ".")
    AdminUserProfileResponseDTO toAdminUserResponseDTO(Users user);
}
