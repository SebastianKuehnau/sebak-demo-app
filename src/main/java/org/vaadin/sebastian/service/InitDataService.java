package org.vaadin.sebastian.service;

import org.ajbrown.namemachine.Name;
import org.ajbrown.namemachine.NameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.sebastian.entity.Person;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
* Service to generate random data
* const COUNT manage the amount of data created 
**/
@Service
public class InitDataService {

	final static Logger logger = Logger.getLogger("InitDataService");
	protected static final int COUNT = 100000;

	@Autowired
    PersonService personService ;

	@PostConstruct
	public void init() {

		if (personService.count() >= COUNT)
			return ;

		logger.info("add new data");

		NameGenerator generator = new NameGenerator();

		final List<Name> names = generator.generateNames(COUNT);
		Set<Person> personList = names.stream()
				.map(name -> Person.Builder.create(name))
				.collect(Collectors.toSet());
		personService.saveAll(personList);

		logger.info("ratingService.findAll().size() " + personService.findAll().size());
	}
}
