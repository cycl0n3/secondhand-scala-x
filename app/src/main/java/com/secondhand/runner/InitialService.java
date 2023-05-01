package com.secondhand.runner;

import com.secondhand.role.Role;
import com.secondhand.role.RoleRepository;
import com.secondhand.user.User;
import com.secondhand.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
@Transactional
public class InitialService {

    private final Logger logger = LoggerFactory.getLogger(InitialService.class);

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void init() {
        logger.info("Initializing database...");

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        Role userRole = new Role();
        userRole.setName("USER");

        Role guestRole = new Role();
        guestRole.setName("GUEST");

        Role moderatorRole = new Role();
        moderatorRole.setName("MODERATOR");

        if(roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(adminRole);
        }

        if(roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(userRole);
        }

        if(roleRepository.findByName("GUEST").isEmpty()) {
            roleRepository.save(guestRole);
        }

        if(roleRepository.findByName("MODERATOR").isEmpty()) {
            roleRepository.save(moderatorRole);
        }

        adminRole = roleRepository.findByName("ADMIN").orElse(null);
        userRole = roleRepository.findByName("USER").orElse(null);
        guestRole = roleRepository.findByName("GUEST").orElse(null);
        moderatorRole = roleRepository.findByName("MODERATOR").orElse(null);

        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setEmail("admin@localhost");
        if(adminRole != null) {
            adminUser.getRoles().add(adminRole);
        }

        if(userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(adminUser);
        }

        User userUser = new User();
        userUser.setUsername("user");
        userUser.setPassword(passwordEncoder.encode("user"));
        userUser.setEmail("user@localhost");
        if(userRole != null) {
            userUser.getRoles().add(userRole);
        }

        if(userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(userUser);
        }

        User guestUser = new User();
        guestUser.setUsername("guest");
        guestUser.setPassword(passwordEncoder.encode("guest"));
        guestUser.setEmail("guest@localhost");
        if(guestRole != null) {
            guestUser.getRoles().add(guestRole);
        }

        if(userRepository.findByUsername("guest").isEmpty()) {
            userRepository.save(guestUser);
        }

        User sam = new User();
        sam.setUsername("sam");
        sam.setPassword(passwordEncoder.encode("sam"));
        sam.setEmail("sam@localhost");
        sam.getRoles().add(userRole);
        if(moderatorRole != null) {
            sam.getRoles().add(moderatorRole);
        }

        if(userRepository.findByUsername("sam").isEmpty()) {
            userRepository.save(sam);
        }

        logger.info("Database initialized.");
    }
}
