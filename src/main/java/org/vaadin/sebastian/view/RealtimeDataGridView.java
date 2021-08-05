package org.vaadin.sebastian.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.leif.veaktor.Veactor;
import org.vaadin.sebastian.service.StockDataService;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Collectors;

@Route(value = "realtime-grid", layout = MainLayout.class)
@CssImport(value = "./styles/shared-styles.css")
@JavaScript("./src/update-stock-grid.js")
public class RealtimeDataGridView extends VerticalLayout {

    private final StockDataService stockDataService;
    private final Grid<StockDataService.StockData> stockDataGrid;

    public RealtimeDataGridView(StockDataService stockDataService) {
        this.stockDataService = stockDataService;

        stockDataGrid = new Grid<>();
        stockDataGrid.addColumn(StockDataService.StockData::getName).setHeader("Name");
        stockDataGrid.addColumn(TemplateRenderer.<StockDataService.StockData>
                of("<span id=\"element-[[item.id]]\">[[item.price]]</span>")
                        .withProperty("price", StockDataService.StockData::getPrice)
                        .withProperty("id", StockDataService.StockData::getId))
                .setHeader("Price");
        stockDataGrid.setSizeFull();

        stockDataGrid.setItems(stockDataService.getStockData());

        Veactor.subscribe(this, Flux.interval(Duration.ofSeconds(5)), this::updateGrid);

        add(stockDataGrid);
        setSizeFull();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        //init java script class
        attachEvent.getUI().getPage().executeJs("window.Vaadin.Flow.stockdataGridConnector.init();");
    }

    private <T> void updateGrid(T object) {

        final var updatedStockData = stockDataService.updateStockPrice();

        if (updatedStockData.size() > 0) {
            //remove "updated" style name from element
            UI.getCurrent().getPage().executeJs("window.Vaadin.Flow.stockdataGridConnector.removeUpdatableStyleClass();");

            //create comma separated string with ids to identify elements
            var idString = updatedStockData.stream()
                    .map(StockDataService.StockData::getId)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));

            //add "updated" style name to elements
            UI.getCurrent().getPage().executeJs("window.Vaadin.Flow.stockdataGridConnector.addUpdatableStyleClasses($0);", idString);

            updatedStockData.forEach(stockData -> {
                stockDataGrid.getDataProvider().refreshItem(stockData);
            });
        }
    }
}
