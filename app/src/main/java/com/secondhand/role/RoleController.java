package com.secondhand.role;

import com.secondhand.exception.GenericException;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/role")
@AllArgsConstructor
public class RoleController {

    private final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoles() {
        Map<String, Object> response = new HashMap<>();

        List<Role> roles = roleService.getAllRoles();
        response.put("roles", roleMapper.toRoleDto(roles));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getRole(@PathVariable Long roleId) {
        Optional<Role> role = roleService.getRoleById(roleId);

        if(role.isEmpty()) {
            logger.error("Role with id {} not found.", roleId);
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleMapper.toRoleDto(role.get()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ex")
    public ResponseEntity<String> ex() throws GenericException {
        throw new GenericException("This is a generic exception from RoleController.");
    }
}
