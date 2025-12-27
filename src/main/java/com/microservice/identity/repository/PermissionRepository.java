package com.microservice.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.identity.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

}
