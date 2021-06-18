package org.vaadin.sebastian.view;

import com.vaadin.addon.leaflet4vaadin.LeafletMap;
import com.vaadin.addon.leaflet4vaadin.layer.map.options.DefaultMapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.map.options.MapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.ui.marker.Marker;
import com.vaadin.addon.leaflet4vaadin.layer.vectors.Polyline;
import com.vaadin.addon.leaflet4vaadin.options.FitBoundsOptions;
import com.vaadin.addon.leaflet4vaadin.types.Icon;
import com.vaadin.addon.leaflet4vaadin.types.LatLng;
import com.vaadin.addon.leaflet4vaadin.types.LatLngBounds;
import com.vaadin.addon.leaflet4vaadin.types.Point;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dnd.*;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.sebastian.entity.Customer;

import java.util.*;
import java.util.stream.Collectors;

@RouteAlias(value = "", layout = MainLayout.class)
@Route(value = "map", layout = MainLayout.class)
public class MapView extends HorizontalLayout {

    private final Map<Customer, Marker> customerMarkerMap = new HashMap<>();

    private final Grid<Customer> customerGrid = new Grid<>(Customer.class);
    private final Grid<Customer> receiverGrid = new Grid<>(Customer.class);

    private List<Customer> draggedItems;
    private Grid<Customer> dragSource;

    ComponentEventListener<GridDragStartEvent<Customer>> dragStartListener = event -> {
        draggedItems = event.getDraggedItems();
        dragSource = event.getSource();
        customerGrid.setDropMode(GridDropMode.BETWEEN);
        receiverGrid.setDropMode(GridDropMode.BETWEEN);
    };

    ComponentEventListener<GridDragEndEvent<Customer>> dragEndListener = event -> {
        draggedItems = null;
        dragSource = null;
        customerGrid.setDropMode(null);
        receiverGrid.setDropMode(null);
    };

    ComponentEventListener<GridDropEvent<Customer>> dropListener = event -> {
        Optional<Customer> target = event.getDropTargetItem();
        if (target.isPresent() && draggedItems.contains(target.get())) {
            return;
        }

        // Remove the items from the source grid
        @SuppressWarnings("unchecked")
        ListDataProvider<Customer> sourceDataProvider = (ListDataProvider<Customer>) dragSource
                .getDataProvider();
        List<Customer> sourceItems = new ArrayList<>(
                sourceDataProvider.getItems());
        sourceItems.removeAll(draggedItems);
        dragSource.setItems(sourceItems);

        // Add dragged items to the target Grid
        Grid<Customer> targetGrid = event.getSource();
        @SuppressWarnings("unchecked")
        ListDataProvider<Customer> targetDataProvider = (ListDataProvider<Customer>) targetGrid
                .getDataProvider();
        List<Customer> targetItems = new ArrayList<>(
                targetDataProvider.getItems());

        int index = target.map(person -> targetItems.indexOf(person)
                + (event.getDropLocation() == GridDropLocation.BELOW ? 1
                : 0))
                .orElse(0);
        targetItems.addAll(index, draggedItems);
        targetGrid.setItems(targetItems);

        draggedItems.stream().forEach(customer -> {
            customerMarkerMap.get(customer).setIcon(targetGrid == customerGrid ? ICON_GOLD : ICON_GREEN);
        });
    };

    public static final Icon ICON_GOLD = new Icon("images/marker-icon-2x-gold.png");
    public static final Icon ICON_RED = new Icon("images/marker-icon-2x-red.png");
    public static final Icon ICON_BLUE = new Icon("images/marker-icon-2x-blue.png");
    public static final Icon ICON_GREEN = new Icon("images/marker-icon-2x-green.png");

    Set<Customer> customerSet = Set.of(
            new Customer("Frankfurt", "Römerberg 23, 60311 Frankfurt am Main", 50.1109, 8.6821),
            new Customer("Munich", "Marienplatz 8, 80331 München", 48.1351,11.5820),
            new Customer("Stuttgart", "Marktplatz 1, 70173 Stuttgart", 48.7758, 9.1829),
            new Customer("Bern", "Rathauspl. 2, 3011 Bern", 46.9480, 7.4474),
            new Customer("Zurich", "Limmatquai 55, 8001 Zürich", 47.3769, 8.5417),
            new Customer("Vienna", "Friedrich-Schmidt-Platz 1, 1010 Wien", 48.2082, 16.3738),
            new Customer("Florence", "P.za della Signoria, 50122 Firenze FI", 43.7496, 11.2558),
            new Customer("Genoa", "Via Garibaldi, 9, 16124 Genova GE", 44.4056, 8.9463),
            new Customer("Naples", "P.za Municipio, 80133 Napoli NA", 40.8518, 14.2681),
            new Customer("Prague", "Staroměstské nám. 1/3, 110 00 Staré Město", 50.0755, 14.4378)
    );
    private final LeafletMap leafletMap;

    public MapView() {

        final VerticalLayout gridLayout = new VerticalLayout();

        customerGrid.setItems(customerSet);
        customerGrid.setColumns("name", "address", "lat", "lng");
        customerGrid.addDragStartListener(dragStartListener);
        customerGrid.addDragEndListener(dragEndListener);
        customerGrid.addDropListener(dropListener);
        customerGrid.setRowsDraggable(true);
        customerGrid.setSizeFull();

        receiverGrid.addDragStartListener(dragStartListener);
        receiverGrid.setColumns("name", "address", "lat", "lng");
        receiverGrid.addDragEndListener(dragEndListener);
        receiverGrid.addDropListener(dropListener);
        receiverGrid.setRowsDraggable(true);
        receiverGrid.setSizeFull();

        gridLayout.setWidth("800px");
        gridLayout.add(new Span("Drag items to empty grid below!"), customerGrid, receiverGrid);

        LatLngBounds bounds = new LatLngBounds(
                customerSet.stream()
                        .map(customer -> new LatLng(customer.getLat(), customer.getLng()))
                        .collect(Collectors.toList()));

        DefaultMapOptions mapOptions = new DefaultMapOptions();
        leafletMap = new LeafletMap(mapOptions);
        leafletMap.setBaseUrl("https://api.maptiler.com/maps/c0cb4b60-017a-4c47-abd0-42f67b372811/{z}/{x}/{y}.png?key=maCVN7eaIRvFz61y6YSj");
        leafletMap.setWidth("60%");
        leafletMap.setHeight("1000px");

        Marker start = new Marker();
        start.setLatLng(new LatLng(52.5200, 13.4050));
        start.setIcon(ICON_BLUE);
        start.bindPopup("Start");
        start.addTo(leafletMap);

        Marker destination = new Marker();
        destination.setLatLng(new LatLng(41.9028,12.4964));
        destination.bindPopup("Destination");
        destination.setIcon(ICON_RED);
        destination.addTo(leafletMap);

        bounds.extend(start.getLatLng(), destination.getLatLng());

        customerSet.forEach(customer -> {
            final Marker customerMarker = new Marker();
            customerMarker.setLatLng(new LatLng(customer.getLat(), customer.getLng()));
            customerMarker.bindPopup(customer.getName());
            customerMarker.setIcon(ICON_GOLD);
            customerMarker.onClick(event -> {
                customerGrid.select(customer);
            });
            customerMarker.addTo(leafletMap);
            customerMarkerMap.put(customer, customerMarker);
        });

        leafletMap.whenReady(event -> {
            leafletMap.fitBounds(bounds);
        });

        add(leafletMap, gridLayout);
        setSizeFull();
    }
}
