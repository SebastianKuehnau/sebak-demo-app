package org.vaadin.sebastian.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.vaadin.sebastian.entity.Person;

public interface PersonService extends JpaRepository<Person, Long> {

    @Cacheable("findAllByLastnameContainingIgnoreCaseAndFirstnameContainingIgnoreCaseAndEmailContainingIgnoreCase")
    Page<Person> findAllByLastnameContainingIgnoreCaseAndFirstnameContainingIgnoreCaseAndEmailContainingIgnoreCase(
            @Param("lastname") String lastname, @Param("firstname") String firstname,
            @Param("email") String email, Pageable pageable);
}
