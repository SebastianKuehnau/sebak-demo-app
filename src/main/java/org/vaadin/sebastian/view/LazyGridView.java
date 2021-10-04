package org.vaadin.sebastian.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    final TextField lastnameField = new FilterTextField();
    final TextField firstnameField = new FilterTextField();
    final TextField emailField = new FilterTextField();

    public LazyGridView() {
        grid.setColumns("id", "lastname", "firstname", "email");

        grid.setMultiSort(true);

        headerRow = grid.appendHeaderRow();
        headerRow.getCell(grid.getColumnByKey("lastname")).setComponent(lastnameField);
        headerRow.getCell(grid.getColumnByKey("firstname")).setComponent(firstnameField);
        headerRow.getCell(grid.getColumnByKey("email")).setComponent(emailField);

        grid.setSizeFull();

        grid.setPageSize(50);

        add(grid);
        setSizeFull();
    }

    @PostConstruct
    public void init() {
        //DataProvider with callback to send query with parameter to DB
        grid.setItems(
            // First callback fetches items based on a query
            query -> {
                // The index of the first item to load
                int offset = query.getOffset();

                // The number of items to load
                int limit = query.getLimit();

                Sort sort = SpringUtil.convertSortOrders(query.getSortOrders());

                // Spring specific API to page queries
                final PageRequest pageRequest = PageRequest.of((offset / limit), limit, sort);

                long startTime = System.currentTimeMillis() ;
                // pass parameter to backend
                Page<Person> allByLastnameContainingIgnoreCaseAndFirstnameContainingIgnoreCaseAndEmailContainingIgnoreCase = personService.findAllByLastnameContainingIgnoreCaseAndFirstnameContainingIgnoreCaseAndEmailContainingIgnoreCase(lastnameField.getValue(), firstnameField.getValue(), emailField.getValue(), pageRequest);

                log.info("Query time is " + (System.currentTimeMillis() - startTime));

                return allByLastnameContainingIgnoreCaseAndFirstnameContainingIgnoreCaseAndEmailContainingIgnoreCase.stream();
        });
    }

    public class FilterTextField extends TextField {
        public FilterTextField() {
            super(event-> grid.getDataProvider().refreshAll());
            setClearButtonVisible(true);
            setValueChangeMode(ValueChangeMode.EAGER);
        }
    }
}
