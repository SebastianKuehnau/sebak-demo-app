package org.vaadin.sebastian.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.function.SerializableConsumer;
import org.vaadin.sebastian.entity.Person;

public class PersonDialog extends Dialog {
    private final Span headline = new Span("Person Dialog");

    private final TextField firstnameField = new TextField("Firstname");
    private final TextField lastnameField = new TextField("Lastname");
    private final TextField emailField = new TextField("E-Mail");

    private final Button saveButton = new Button("save", this::save) ;
    private final Button cancelButton = new Button("cancel", this::cancel) ;


    private final Binder<Person> binder = new Binder<>();

    private final Person currentPerson ;
    private final SerializableConsumer<Person> consumer ;

    public PersonDialog(final Person person, final SerializableConsumer<Person> consumer) {
        currentPerson = person;
        this.consumer = consumer;
        setModal(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        headline.getStyle().set("font-weight", "700");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);
        buttonLayout.getStyle().set("padding-top", "var(--lumo-space-m)");

        firstnameField.setSizeFull();
        lastnameField.setSizeFull();
        emailField.setSizeFull();

        VerticalLayout rootLayout = new VerticalLayout(headline, firstnameField, lastnameField, emailField,
                buttonLayout);
        rootLayout.setAlignSelf(FlexComponent.Alignment.END, buttonLayout);
        rootLayout.setSpacing(false);
        rootLayout.setWidth("300px");
        add(rootLayout);

        binder.forField(firstnameField)
                .bind(Person::getFirstname, Person::setFirstname);
        binder.forField(lastnameField)
                .bind(Person::getLastname, Person::setLastname);
        binder.forField(emailField)
                .withValidator(new EmailValidator("Invalid mail address!"))
                .bind(Person::getEmail, Person::setEmail);

        binder.readBean(person);
    }


    private void save(ClickEvent<Button> buttonClickEvent) {
        try {
            binder.writeBean(currentPerson);
            consumer.accept(currentPerson);
            close();
        } catch (ValidationException e) {

        }
    }


    private void cancel(ClickEvent<Button> buttonClickEvent) {
        binder.readBean(currentPerson);
        close();
    }
}
