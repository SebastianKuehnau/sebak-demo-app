package org.vaadin.sebastian.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.sebastian.service.StockDataService;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Route(value = "realtime-grid", layout = MainLayout.class)
public class RealtimeDataGridView extends VerticalLayout {

    private final StockDataService stockDataService;
    private final Grid<StockDataService.StockData> stockDataGrid;
    private Disposable subscribe;

    public RealtimeDataGridView(StockDataService stockDataService) {
        this.stockDataService = stockDataService;

        stockDataGrid = new Grid<>();
        stockDataGrid.addColumn(StockDataService.StockData::getName).setHeader("Name");
        stockDataGrid.addColumn(StockDataService.StockData::getPrice).setHeader("Price");
        stockDataGrid.setSizeFull();

        stockDataGrid.setItems(stockDataService.getStockData());

        add(stockDataGrid);
        setSizeFull();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        subscribe = Flux.interval(Duration.ofMillis(1000))
            .subscribeOn(Schedulers.single())
            .subscribe(aLong ->
               attachEvent.getUI().access(() ->
                   stockDataService.updateStockPrice().forEach(stockData ->
                       stockDataGrid.getDataProvider().refreshItem(stockData))
               )
            );
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        subscribe.dispose();

        super.onDetach(detachEvent);
    }
}
