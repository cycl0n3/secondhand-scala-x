package com.secondhand.role;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toRoleDto(Role role);

    List<RoleDto> toRoleDto(List<Role> roles);

    Role toRole(RoleDto roleDto);

    List<Role> toRole(List<RoleDto> roles);
}
