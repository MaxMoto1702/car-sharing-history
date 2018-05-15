package net.serebryansky.carsharinghistory.repository;

import net.serebryansky.carsharinghistory.domain.Event;
import net.serebryansky.carsharinghistory.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
