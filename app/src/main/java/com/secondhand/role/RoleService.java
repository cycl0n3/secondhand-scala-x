package com.secondhand.role;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Role saveRole(Role role) {
        logger.info("Saving role {}", role);

        return roleRepository.save(role);
    }

    public void deleteRole(Role role) {
        logger.info("Deleting role {}", role);

        roleRepository.delete(role);
    }

    public void deleteRoleById(Long id) {
        logger.info("Deleting role with id {}", id);

        roleRepository.deleteById(id);
    }
}
