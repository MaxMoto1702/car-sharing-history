package net.serebryansky.carsharinghistory.repository;

import net.serebryansky.carsharinghistory.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource
public interface EventRepository extends JpaRepository<Event, Long> {
    @Override
    @Query("select event from Event event where event.owner like ?#{hasRole('ROLE_ADMIN') ? '%' : principal}")
    List<Event> findAll();

    @Query("select event from Event event where event.owner like ?#{hasRole('ROLE_ADMIN') ? '%' : principal} order by event.date desc")
    List<Event> findLast();

    @Override
    @RestResource(exported = false)
    Event save(Event entity);

    @Override
    @RestResource(exported = false)
    void delete(Event entity);

//    /**
//     * Here we demonstrate the use of SecurityContext information in dynamic SpEL parameters in a JPQL update statement.
//     */
//    @Modifying(clearAutomatically = true)
//    @Query("update Event b set b.data = upper(b.data), b.lastModifiedBy = :#{#security.principal}, b.lastModifiedDate = :#{new java.util.Date()}")
//    void modifiyDataWithRecordingSecurityContext();
}
