package org.vaadin.sebastian.view;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.leif.veaktor.Veactor;
import org.vaadin.sebastian.service.StockDataService;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Route(value = "realtime-grid", layout = MainLayout.class)
public class RealtimeDataGridView extends VerticalLayout {

    private final StockDataService stockDataService;
    private final Grid<StockDataService.StockData> stockDataGrid;

    public RealtimeDataGridView(StockDataService stockDataService) {
        this.stockDataService = stockDataService;

        stockDataGrid = new Grid<>();
        stockDataGrid.addColumn(StockDataService.StockData::getName).setHeader("Name");
        stockDataGrid.addColumn(TemplateRenderer.<StockDataService.StockData>
                of("<highlight-component value='[[item.price]]'></highlight-component>")
                        .withProperty("price", StockDataService.StockData::getPrice))
                .setHeader("Price");

        stockDataGrid.setSizeFull();

        stockDataGrid.setItems(stockDataService.getStockData());

        Veactor.subscribe(this, Flux.interval(Duration.ofSeconds(5)), this::updateGrid);

        add(stockDataGrid);
        setSizeFull();
    }

    private <T> void updateGrid(T object) {

        final var updatedStockData = stockDataService.updateStockPrice();

        updatedStockData.forEach(stockData -> {
            stockDataGrid.getDataProvider().refreshItem(stockData);
        });
    }
}
