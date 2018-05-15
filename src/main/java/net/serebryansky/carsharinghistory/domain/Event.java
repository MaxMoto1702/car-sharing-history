package net.serebryansky.carsharinghistory.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date date;
    @ManyToOne
    private User owner;
    @ManyToOne
    private Car car;
    @ManyToMany(cascade = ALL)
    private Set<Photo> photos;

    public Event() {
    }

    public Event(String ignore, User owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }
}
