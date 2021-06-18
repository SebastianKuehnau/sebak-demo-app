package org.vaadin.sebastian.view.nasa;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.sebastian.view.MainLayout;

@Route(value = "nasa", layout = MainLayout.class)
public class NasaMapView extends VerticalLayout {
    public NasaMapView() {
        add(new Span("Hello Nasa World Map View"));
        add(new WorldWindMap());
    }
}
