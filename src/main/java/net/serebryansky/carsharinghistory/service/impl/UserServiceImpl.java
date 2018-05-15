package net.serebryansky.carsharinghistory.service.impl;

import net.serebryansky.carsharinghistory.domain.Role;
import net.serebryansky.carsharinghistory.domain.User;
import net.serebryansky.carsharinghistory.repository.RoleRepository;
import net.serebryansky.carsharinghistory.repository.UserRepository;
import net.serebryansky.carsharinghistory.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    public void setupDefaultRoles() {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);
    }

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void createFromVk(Integer userId) {
        createFromVk(userId, null);
    }

    @Override
    public void createFromVk(Integer userId, String secret) {
        if (!userRepository.existsByVkUserId(userId)) {
            User user = new User();
            user.setUsername("user" + System.currentTimeMillis());
            user.setVkUserId(userId);
            user.setVkSecret(secret);
            user.setPassword(UUID.randomUUID().toString());
            save(user);
        }
    }

    @Override
    public boolean existsVkUserId(Integer userId) {
        return userRepository.existsByVkUserId(userId);
    }

    @Override
    public User findByVkUserId(Integer userId) {
        return userRepository.findByVkUserId(userId);
    }
}
