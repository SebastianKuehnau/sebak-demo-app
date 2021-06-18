package org.vaadin.sebastian.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.*;
import org.vaadin.sebastian.entity.Result;
import org.vaadin.sebastian.service.BitcoinService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route(value = "restful", layout = MainLayout.class)
@RouteAlias(value = "rest-ful", layout = MainLayout.class)
public class RestfulView extends VerticalLayout implements BeforeLeaveObserver {

    private final BigDecimalField totalPriceField = new BigDecimalField("Current Bitcoin Price");;
    private final NumberField numberField = new NumberField("Amount");
    private final BigDecimalField currentBitcoinPrice = new BigDecimalField("Current Bitcoin Price");;
    private Notification showNotification;

    final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final BitcoinService bitcoinService;

    public RestfulView(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;

        Span cryptowatchText = new Span("The bitcoin price is provided by api.cryptowat.ch");

        currentBitcoinPrice.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        currentBitcoinPrice.setPrefixComponent(new Icon(VaadinIcon.EURO));
        currentBitcoinPrice.setValue(BigDecimal.ZERO);
        currentBitcoinPrice.addValueChangeListener(event -> totalPriceField.setValue(BigDecimal.valueOf(numberField.getValue()).multiply(currentBitcoinPrice.getValue()).setScale(2, RoundingMode.HALF_UP)));
        currentBitcoinPrice.setWidth("300px");

        numberField.setHasControls(true);
        numberField.setValue(1.0);
        numberField.setStep(0.01d);
        numberField.setMin(0);
        numberField.setMax(10);
        numberField.setWidth("300px");

        totalPriceField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        totalPriceField.setPrefixComponent(new Icon(VaadinIcon.EURO));
        totalPriceField.setWidth("300px");

        add(cryptowatchText, currentBitcoinPrice, numberField, totalPriceField);
        setSizeFull();
    }


    private void setResult(Optional<Result> resultOptional) {
        this.setEnabled(resultOptional.isPresent());

        currentBitcoinPrice.setValue(BigDecimal.valueOf(resultOptional.orElseGet(this::createEmptyBitcoinValue).getPrice()));

        numberField.setEnabled(resultOptional.isPresent());
        numberField.addValueChangeListener(event -> {
            totalPriceField.setValue(BigDecimal.valueOf(event.getValue()).multiply(currentBitcoinPrice.getValue()).setScale(2, RoundingMode.HALF_UP));
        });

        totalPriceField.setEnabled(resultOptional.isPresent());
        totalPriceField.setValue(BigDecimal.valueOf(numberField.getValue()).multiply(currentBitcoinPrice.getValue()).setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        final UI ui = attachEvent.getUI();

        bitcoinService.fetchBitcoinPriceAsync()
                .addCallback(
                        resultOptional -> ui.access(() -> setResult(resultOptional)),
                        ex -> {
                            showNotification = Notification.show("Bitcoin currency service is currently not available", Integer.MAX_VALUE, Notification.Position.BOTTOM_CENTER);
                            showNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        });
    }

    private Result createEmptyBitcoinValue() {
        Result result = new Result();
        result.setPrice(0d);
        return result;
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (showNotification != null)
            showNotification.close();
    }
}
