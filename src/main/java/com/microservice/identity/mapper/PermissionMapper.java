package com.microservice.identity.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.microservice.identity.dto.request.PermissionCreationRequest;
import com.microservice.identity.dto.response.PermissionResponse;
import com.microservice.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionCreationRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    List<PermissionResponse> toListPermissionResponse(List<Permission> permissions);

}
