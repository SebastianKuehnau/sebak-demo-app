package org.vaadin.sebastian.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.vaadin.sebastian.entity.Person;
import org.vaadin.sebastian.service.PersonService;
import org.vaadin.sebastian.util.SpringUtil;

import javax.annotation.PostConstruct;

@Route(value = "lazy-grid", layout = MainLayout.class)
public class LazyGridView extends VerticalLayout {

    Logger log = Logger.getLogger(LazyGridView.class);

    private final HeaderRow headerRow;

    @Autowired
    PersonService personService ;

    private final Grid<Person> grid = new Grid<>(Person.class);

    final FilterTextField lastnameField = new FilterTextField();
    final FilterTextField firstnameField = new FilterTextField();
    final FilterTextField emailField = new FilterTextField();
    final FilterComboBox<Integer> counterComboBox = new FilterComboBox<>();

    public LazyGridView() {

        grid.setColumns("id", "lastname", "firstname", "email", "counter");

        grid.setMultiSort(true);

        headerRow = grid.appendHeaderRow();
        headerRow.getCell(grid.getColumnByKey("lastname")).setComponent(lastnameField);
        headerRow.getCell(grid.getColumnByKey("firstname")).setComponent(firstnameField);
        headerRow.getCell(grid.getColumnByKey("email")).setComponent(emailField);
        headerRow.getCell(grid.getColumnByKey("counter")).setComponent(counterComboBox);

        grid.setSizeFull();

        grid.setPageSize(50);

        add(grid);

        setSizeFull();
    }

    @PostConstruct
    public void init() {

        var personExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING);

        //DataProvider with callback to send query with parameter to DB
        grid.setItems(
            // First callback fetches items based on a query
            query -> {
                // The index of the first item to load
                int offset = query.getOffset();

                // The number of items to load
                int limit = query.getLimit();

                var sort = SpringUtil.convertSortOrders(query.getSortOrders());

                // Spring specific API to page queries
                var pageRequest = PageRequest.of((offset / limit), limit, sort);

                //DTO for filtering
                var personFilterDto = new Person(lastnameField.getValue(), firstnameField.getValue(), emailField.getValue(), counterComboBox.getValue());

                long startTime = System.currentTimeMillis() ;

                var personPage = personService.findAll(Example.of(personFilterDto, personExampleMatcher), pageRequest);

                log.info("Grid Query time is " + (System.currentTimeMillis() - startTime));

                return personPage.stream();
        });

        counterComboBox.setItems(query -> {
            // The index of the first item to load
            int offset = query.getOffset();

            // The number of items to load
            int limit = query.getLimit();

            var sort = SpringUtil.convertSortOrders(query.getSortOrders());

            // Spring specific API to page queries
            var pageRequest = PageRequest.of((offset / limit), limit, sort);

            long startTime = System.currentTimeMillis() ;

            //DTO for filtering
            var personFilterDto = new Person(lastnameField.getValue(), firstnameField.getValue(), emailField.getValue(), counterComboBox.getValue());

            var personStream = personService.findAll(Example.of(personFilterDto, personExampleMatcher), pageRequest)
                    .stream()
                    .map(Person::getCounter)
                    .distinct()
                    .sorted();

            log.info("ComboBox Query time is " + (System.currentTimeMillis() - startTime));

            return personStream;
        });
    }

    private void update() {
        grid.getDataProvider().refreshAll();
        counterComboBox.getDataProvider().refreshAll();
    }

    public class FilterTextField extends TextField {
        public FilterTextField() {
            super(event -> update());
            setClearButtonVisible(true);
            setValueChangeMode(ValueChangeMode.EAGER);
        }
    }

    public class FilterComboBox<T> extends ComboBox<T> {
        public FilterComboBox() {
            addSelectedItemChangeListener(event -> update());
            setClearButtonVisible(true);
        }
    }
}
