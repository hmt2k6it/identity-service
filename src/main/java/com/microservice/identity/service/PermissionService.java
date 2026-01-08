package com.microservice.identity.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.microservice.identity.dto.request.PermissionCreationRequest;
import com.microservice.identity.dto.response.PermissionResponse;
import com.microservice.identity.entity.Permission;
import com.microservice.identity.mapper.PermissionMapper;
import com.microservice.identity.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionCreationRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public String deletePermission(String name) {
        permissionRepository.deleteById(name);
        return "Permission has been deleted!";
    }

    public List<PermissionResponse> getAllPermission() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissionMapper.toListPermissionResponse(permissions);
    }
}