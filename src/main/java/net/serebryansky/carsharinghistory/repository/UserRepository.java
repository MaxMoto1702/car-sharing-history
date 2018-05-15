package net.serebryansky.carsharinghistory.repository;

import net.serebryansky.carsharinghistory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Override
    @RestResource(exported = false)
    User save(User entity);

    @Override
    @RestResource(exported = false)
    void delete(User entity);

    boolean existsByVkUserId(Integer userId);

    User findByVkUserId(Integer userId);
}
