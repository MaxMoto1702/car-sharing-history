package net.serebryansky.carsharinghistory.service;

import net.serebryansky.carsharinghistory.domain.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    void createFromVk(Integer userId);

    void createFromVk(Integer userId, String secret);

    boolean existsVkUserId(Integer userId);

    User findByVkUserId(Integer userId);
}
