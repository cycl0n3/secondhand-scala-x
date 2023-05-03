package com.secondhand.role;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/role")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoles() {
        List<Role> roles = roleService.getAllRoles();

        if(roles.isEmpty()) {
            log.error("No roles found.");
            return ResponseEntity.noContent().build();
        }

        List<RoleDto> roleDtos = roleMapper.toRoleDto(roles);

        Map<String, Object> response = new HashMap<>();
        response.put("roles", roleDtos);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // save role to database\
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveRole(@Valid @RequestBody RoleDto roleDto) {
        Role role = roleMapper.toRole(roleDto);

        Role savedRole = roleService.saveRole(role);

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleMapper.toRoleDto(savedRole));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getRole(@PathVariable @Min(0) Long roleId) {
        Optional<Role> role = roleService.getRoleById(roleId);

        if(role.isEmpty()) {
            log.error("Role with id {} not found.", roleId);
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleMapper.toRoleDto(role.get()));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<Map<String, Object>> getRole(@PathVariable String roleName) {
        Optional<Role> role = roleService.getRoleByName(roleName);

        if(role.isEmpty()) {
            log.error("Role with name {} not found.", roleName);
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleMapper.toRoleDto(role.get()));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
