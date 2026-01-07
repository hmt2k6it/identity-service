package com.microservice.identity.mapper;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.microservice.identity.dto.request.UserCreationRequest;
import com.microservice.identity.dto.request.UserUpdateRequest;
import com.microservice.identity.dto.response.UserResponse;
import com.microservice.identity.entity.Role;
import com.microservice.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    default Set<String> mapRoleToString(Set<Role> roles) {
        if (roles == null)
            return null;
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    List<UserResponse> toListUserResponse(List<User> users);

}
