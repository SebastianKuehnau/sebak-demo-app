package org.vaadin.sebastian.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.PageRequest;
import org.vaadin.sebastian.component.PersonDialog;
import org.vaadin.sebastian.entity.Person;
import org.vaadin.sebastian.service.PersonService;

@Route(value = "crud", layout = MainLayout.class)
public class CrudView extends HorizontalLayout {

    public CrudView(PersonService personService) {
        Grid<Person> personGrid = new Grid<>(Person.class);

        personGrid.setItems(query -> personService
                .findAll(
                        PageRequest.of(query.getPage(), query.getPageSize()))
                .stream()) ;

        personGrid.setColumns("id", "firstname", "lastname", "email");
        personGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        personGrid.setSizeFull();
        personGrid.addItemDoubleClickListener(event -> {
            PersonDialog dialog = new PersonDialog(event.getItem(), person -> personGrid.getDataProvider().refreshItem(person));
            dialog.open();
        });

        add(personGrid);

        setSizeFull();
    }
}
