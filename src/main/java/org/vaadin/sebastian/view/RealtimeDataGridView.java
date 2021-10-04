package org.vaadin.sebastian.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.leif.veaktor.Veactor;
import org.vaadin.sebastian.component.HighlightComponent;
import org.vaadin.sebastian.service.StockDataService;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Uses(HighlightComponent.class)
@Route(value = "realtime-grid", layout = MainLayout.class)
@CssImport(value = "./styles/stock-data-grid.css", themeFor = "vaadin-grid")
public class RealtimeDataGridView extends VerticalLayout {

    private final StockDataService stockDataService;
    private final Grid<StockDataService.StockData> stockDataGrid;

    public RealtimeDataGridView(StockDataService stockDataService) {
        this.stockDataService = stockDataService;

        stockDataGrid = new Grid<>();
        stockDataGrid.addColumn(StockDataService.StockData::getName).setHeader("Name");
        stockDataGrid.addColumn(TemplateRenderer.<StockDataService.StockData>
                of("<highlight-component value='[[item.price]]'></highlight-component>")
                        .withProperty("price", this::createPriceString))
                .setHeader("Price");
        stockDataGrid.setClassNameGenerator(stockData -> stockData.isUpdated() ?
                "warning" : "normal");

        stockDataGrid.setSizeFull();

        stockDataGrid.setItems(stockDataService.getStockData());

        Veactor.subscribe(this, Flux.interval(Duration.ofSeconds(5)), this::updateGrid);

        add(stockDataGrid);
        setSizeFull();
    }

    private String createPriceString(StockDataService.StockData stockData) {
        return String.format("%10.2f €", stockData.getPrice());
    }

    private <T> void updateGrid(T object) {


        final var fetch = stockDataGrid.getDataProvider().fetch(new Query<>());
        fetch.forEach(stockData -> stockData.setUpdated(false));
        stockDataGrid.getDataProvider().refreshAll();

        final var updatedStockData = stockDataService.updateStockPrice();

        updatedStockData.forEach(stockData -> {
            stockData.setUpdated(true);
            stockDataGrid.getDataProvider().refreshItem(stockData);
        });
    }
}
