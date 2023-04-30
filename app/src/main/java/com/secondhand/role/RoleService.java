package com.secondhand.role;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public void delete(Role role) {
        roleRepository.delete(role);
    }

    public void deleteAll() {
        roleRepository.deleteAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Iterable<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public long count() {
        return roleRepository.count();
    }

    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    public void deleteAll(Iterable<Role> roles) {
        roleRepository.deleteAll(roles);
    }

    public Iterable<Role> saveAll(Iterable<Role> roles) {
        return roleRepository.saveAll(roles);
    }

    public Iterable<Role> findAllById(Iterable<Long> ids) {
        return roleRepository.findAllById(ids);
    }
}
