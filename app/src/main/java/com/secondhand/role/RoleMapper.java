package com.secondhand.role;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleMapper {

    public RoleDto toRoleDto(Role role) {
        RoleDto roleDto = new RoleDto();

        roleDto.setId(role.getId());
        roleDto.setName(role.getName());

        return roleDto;
    }

    public List<RoleDto> toRoleDto(List<Role> roles) {
        return roles.stream().map(this::toRoleDto).toList();
    }

    public Role toRole(RoleDto roleDto) {
        Role role = new Role();

        role.setId(roleDto.getId());
        role.setName(roleDto.getName());

        return role;
    }

    public List<Role> toRole(List<RoleDto> roles) {
        return roles.stream().map(this::toRole).toList();
    }
}
