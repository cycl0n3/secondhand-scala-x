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
import java.util.Map;

@Controller
@RequestMapping("/role")
@AllArgsConstructor
public class RoleController {

    private final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    @GetMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getRole(@PathVariable Long roleId) {
        Role role = roleService.getRoleById(roleId);

        Map<String, Object> response = new HashMap<>();

        response.put("role", roleMapper.toRoleDto(role));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ex")
    public ResponseEntity<String> ex() throws GenericException {
        throw new GenericException("This is a generic exception from RoleController.");
    }
}
