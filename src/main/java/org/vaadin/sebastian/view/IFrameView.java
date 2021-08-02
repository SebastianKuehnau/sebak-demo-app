package org.vaadin.sebastian.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "iframe", layout = MainLayout.class)
public class IFrameView extends VerticalLayout {

    public IFrameView() {
        H2 headline = new H2("IFrame-Headline");

        IFrame iFrame = new IFrame();
        iFrame.setSrc("https://www.google.com/webhp?igu=1");
        iFrame.setSizeFull();

        add(headline, iFrame);
        setSizeFull();
    }
}
