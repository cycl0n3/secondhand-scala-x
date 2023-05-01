package com.secondhand.role;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/role")
@AllArgsConstructor
public class RoleController {

    private final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoles() {
        List<Role> roles = roleService.getAllRoles();

        if(roles.isEmpty()) {
            logger.error("No roles found.");
            return ResponseEntity.noContent().build();
        }

        List<RoleDto> roleDtos = roleMapper.toRoleDto(roles);

        Map<String, Object> response = new HashMap<>();
        response.put("roles", roleDtos);

        return ResponseEntity.ok(response);
    }

    // save role to database\
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveRole(@Valid @RequestBody RoleDto roleDto) {
        Role role = roleMapper.toRole(roleDto);

        Role savedRole = roleService.saveRole(role);

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleMapper.toRoleDto(savedRole));

        URI location = URI.create(String.format("/api/v1/role/%s", savedRole.getId()));

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getRole(@Valid @PathVariable Long roleId) {
        Optional<Role> role = roleService.getRoleById(roleId);

        if(role.isEmpty()) {
            logger.error("Role with id {} not found.", roleId);
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleMapper.toRoleDto(role.get()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<Map<String, Object>> getRole(@PathVariable String roleName) {
        Optional<Role> role = roleService.getRoleByName(roleName);

        if(role.isEmpty()) {
            logger.error("Role with name {} not found.", roleName);
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleMapper.toRoleDto(role.get()));

        return ResponseEntity.ok(response);
    }
}
