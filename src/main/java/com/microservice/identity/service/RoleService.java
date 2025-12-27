package com.microservice.identity.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import com.microservice.identity.dto.request.RoleCreationRequest;
import com.microservice.identity.dto.request.RoleUpdateRequest;
import com.microservice.identity.dto.response.RoleResponse;
import com.microservice.identity.entity.Permission;
import com.microservice.identity.entity.Role;
import com.microservice.identity.exception.AppException;
import com.microservice.identity.exception.ErrorCode;
import com.microservice.identity.mapper.RoleMapper;
import com.microservice.identity.repository.PermissionRepository;
import com.microservice.identity.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    public RoleResponse createRole(RoleCreationRequest request) {
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public String deleteRole(String name) {
        roleRepository.deleteById(name);
        return "Role has been deleted!";
    }

    public List<RoleResponse> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toListRoleResponse(roles);
    }

    public RoleResponse updateRole(String name, RoleUpdateRequest request) {
        Role role = roleRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roleMapper.updateRole(role, request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }
}
