package net.serebryansky.carsharinghistory;

import net.serebryansky.carsharinghistory.domain.Event;
import net.serebryansky.carsharinghistory.domain.User;
import net.serebryansky.carsharinghistory.repository.EventRepository;
import net.serebryansky.carsharinghistory.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Collections.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SecurityIntegrationTests {

    @Autowired UserRepository userRepository;
    @Autowired EventRepository eventRepository;

    private User tom, ollie, admin;
    private UsernamePasswordAuthenticationToken olliAuth, tomAuth, adminAuth;
    private Event object1, object2, object3;

    @Before
    public void setup() {

        tom = userRepository.save(new User("thomas", "darimont", "tdarimont@example.org"));
        ollie = userRepository.save(new User("oliver", "gierke", "ogierke@example.org"));
        admin = userRepository.save(new User("admin", "admin", "admin@example.org"));

        object1 = eventRepository.save(new Event("object1", ollie));
        object2 = eventRepository.save(new Event("object2", ollie));
        object3 = eventRepository.save(new Event("object3", tom));

        olliAuth = new UsernamePasswordAuthenticationToken(ollie, "x");
        tomAuth = new UsernamePasswordAuthenticationToken(tom, "x");
        adminAuth = new UsernamePasswordAuthenticationToken(admin, "x",
                singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void findAllForCurrentUserShouldReturnOnlyEventsWhereCurrentUserIsOwner() {

        SecurityContextHolder.getContext().setAuthentication(tomAuth);

        List<Event> events = eventRepository.findAll();

        assertThat(events, hasSize(1));
        assertThat(events, contains(object3));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(ollie, "x"));

        events = eventRepository.findAll();

        assertThat(events, hasSize(2));
        assertThat(events, contains(object1, object2));
    }

    @Test
    public void findAllForCurrentUserShouldReturnAllObjectsForAdmin() {

        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        List<Event> events = eventRepository.findAll();

        assertThat(events, hasSize(3));
        assertThat(events, contains(object1, object2, object3));
    }

    @Test
    public void findBusinessObjectsForCurrentUserShouldReturnAllObjectsForAdmin() {

        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        List<Event> events = eventRepository.findAll();

        assertThat(events, hasSize(3));
        assertThat(events, contains(object1, object2, object3));
    }

    @Test
    public void findBusinessObjectsForCurrentUserByIdShouldReturnOnlyBusinessObjectsWhereCurrentUserIsOwner() {

        SecurityContextHolder.getContext().setAuthentication(tomAuth);

        List<Event> events = eventRepository.findAll();

        assertThat(events, hasSize(1));
        assertThat(events, contains(object3));

        SecurityContextHolder.getContext().setAuthentication(olliAuth);

        events = eventRepository.findAll();

        assertThat(events, hasSize(2));
        assertThat(events, contains(object1, object2));
    }

    @Test
    public void findBusinessObjectsForCurrentUserByIdShouldReturnAllObjectsForAdmin() {

        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        List<Event> events = eventRepository.findAll();

        assertThat(events, hasSize(3));
        assertThat(events, contains(object1, object2, object3));
    }

//    @Test
//    public void customUpdateStatementShouldAllowToUseSecurityContextInformationViaSpelParameters() {
//
//        SecurityContextHolder.getContext().setAuthentication(adminAuth);
//
//        eventRepository.modifiyDataWithRecordingSecurityContext();
//
//        for (Event bo : eventRepository.findAll()) {
//
//            assertThat(bo.getLastModifiedDate(), is(notNullValue()));
//            assertThat(bo.getLastModifiedBy().getFirstname(), is("admin"));
//        }
//    }
}
