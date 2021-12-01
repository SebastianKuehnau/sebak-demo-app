package org.vaadin.sebastian;

import org.ajbrown.namemachine.NameGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.vaadin.sebastian.entity.Person;
import org.vaadin.sebastian.service.PersonService;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@EnableCaching
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    final static Logger logger = Logger.getLogger("InitDataService");
    protected static final int COUNT = 10000;

    @Bean
    public CommandLineRunner createDemoDataIfNeeded(PersonService personService) {
        return args -> {
            if (personService.count() >= COUNT)
                return ;

            logger.info("add new data");

            var generator = new NameGenerator();

            var names = generator.generateNames(COUNT);

            var personList = names.stream()
                    .map(name -> {
                        var person = Person.Builder.create(name);

                        var counter = ThreadLocalRandom.current().nextInt(10) + 1 ;
                        person.setCounter(counter) ;

                        return person;
                    })
                    .collect(Collectors.toList());

            personService.saveAll(personList);

            logger.info("generated " + personService.count() + " items");
        };
    }
}
