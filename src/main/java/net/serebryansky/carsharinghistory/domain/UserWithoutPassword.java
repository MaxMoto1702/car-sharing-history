package net.serebryansky.carsharinghistory.domain;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "withoutPassword", types = User.class)
public interface UserWithoutPassword {
    public String getUsername();
}
