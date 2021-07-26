package org.vaadin.sebastian.view;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.*;

import java.util.HashMap;
import java.util.Map;

@Push
public class MainLayout extends HorizontalLayout implements RouterLayout, AppShellConfigurator {

    private final VerticalLayout menu = new VerticalLayout();
    private final Div content = new Div();

    Map<String, RouterLink> routerLinkMap = new HashMap<>() ;

    public MainLayout() {

        RouteConfiguration.forSessionScope().getAvailableRoutes().stream()
                .filter(routeData -> MainLayout.class.equals((routeData.getParentLayout())))
                .forEach(routeData -> {
                    RouterLink routerLink = new RouterLink(routeData.getTemplate(), routeData.getNavigationTarget());
                    routerLink.getElement().getStyle().set("white-space", "nowrap");
                    routerLink.setHighlightAction(this::setRouterLinkBold);
                    routerLink.setHighlightCondition(this::isRouterLinkBold);
                    menu.add(routerLink);

                    routerLinkMap.put(routeData.getTemplate(), routerLink);
                    routeData.getRouteAliases().stream()
                            .forEach(routeAliasData -> routerLinkMap.put(routeAliasData.getTemplate(), routerLink));
                });

        menu.setSizeUndefined();
        content.setSizeFull();

        add(menu, content);
        setFlexGrow(1, content);
        setSizeFull();
    }

    private boolean isRouterLinkBold(RouterLink routerLink, AfterNavigationEvent afterNavigationEvent) {
        return routerLinkMap.get(afterNavigationEvent.getLocation().getFirstSegment()).equals(routerLink) ;
    }

    @Override
    public void showRouterLayoutContent(HasElement newContent) {
        this.content.removeAll();
        this.content.getElement().appendChild(newContent.getElement());
    }

    private void setRouterLinkBold(RouterLink routerLink, Boolean isBold) {
        routerLink.getElement().getStyle().set("font-weight", isBold ? "bold" : "normal");
    }
}
