package org.vaadin.sebastian.view.nasa;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;


@JavaScript("https://files.worldwind.arc.nasa.gov/artifactory/web/0.9.0/worldwind.min.js")
@JavaScript("./src/nasaMap.js")
@Tag("canvas")
public class WorldWindMap extends Div {
    public WorldWindMap() {

        this.getElement().setAttribute("id", "canvasOne");
        this.getElement().getStyle().set("width", "1024px");
        this.getElement().getStyle().set("height", "768px");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        attachEvent.getUI().getPage().executeJs("window.Vaadin.Flow.nasaMapConnector.init();");
    }
}
