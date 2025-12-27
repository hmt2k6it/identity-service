package com.microservice.identity.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.microservice.identity.dto.request.RoleCreationRequest;
import com.microservice.identity.dto.request.RoleUpdateRequest;
import com.microservice.identity.dto.response.RoleResponse;
import com.microservice.identity.entity.Permission;
import com.microservice.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreationRequest request);

    RoleResponse toRoleResponse(Role save);

    List<RoleResponse> toListRoleResponse(List<Role> roles);

    default String mapPermissionToString(Permission permission) {
        if (permission == null)
            return null;
        return permission.getName();
    }

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleUpdateRequest request);

    @Mapping(target = "permissions", ignore = true)
    void updateRole(@MappingTarget Role role, RoleUpdateRequest request);
}
