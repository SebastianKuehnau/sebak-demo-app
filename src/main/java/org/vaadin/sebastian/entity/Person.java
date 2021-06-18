package org.vaadin.sebastian.entity;

import org.ajbrown.namemachine.Name;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id ;

    private String lastname ;
    private String firstname ;
    private String email ;

    public Person() {
    }

    public Person(String lastname, String firstname, String email) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static class Builder {
        public static Person create(Name name) {
            String email = name.getFirstName() + "." + name.getLastName() + "@gmail.com";
            return new Person(name.getLastName(), name.getFirstName(), email);
        }
    }

}
