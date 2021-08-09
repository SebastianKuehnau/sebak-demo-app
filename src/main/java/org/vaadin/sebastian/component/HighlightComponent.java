package org.vaadin.sebastian.component;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;

import java.math.BigDecimal;

@Tag("highlight-component")
@JsModule("./src/highlight-component.ts")
public class HighlightComponent extends LitTemplate {

    public HighlightComponent(BigDecimal price) {
        getElement().setProperty("value", String.valueOf(price));
    }

    public HighlightComponent() {

    }
}
