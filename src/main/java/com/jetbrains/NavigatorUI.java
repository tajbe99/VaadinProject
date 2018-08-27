package com.jetbrains;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;
public class NavigatorUI extends UI implements View {
    final NativeButton menuCategoryButton = new NativeButton();
    final NativeButton menuHotelButton = new NativeButton();
    protected VerticalLayout menuLayout;
    protected VerticalLayout contentPanel = new VerticalLayout();
    final HotelUI hotelView = new HotelUI();
    Navigator navigator;

    private MenuBar.Command openMenu(final String viewId) {
        return new MenuBar.Command() {
            private static final long serialVersionUID = 1L;

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                if(!viewId.equals("not-implemented")) {
                    // go to viewId
                    navigator.navigateTo(viewId);
                }
            }
        };
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        VerticalLayout content = new VerticalLayout();

        navigator = new Navigator(this, content);
        navigator.addView("", new NavigatorUI());
        navigator.addView("Hotel", new HotelUI());
        navigator.addView("Category", new CategoryUI());
        VerticalLayout divs = new VerticalLayout();

        MenuBar menu = new MenuBar();
        menu.setAutoOpen(true);
        MenuBar.MenuItem hotelItem  = menu.addItem("Hotel", openMenu("Hotel"));
        MenuBar.MenuItem categoryItem = menu.addItem("Category", openMenu("Category"));
        content.setMargin(false);
        divs.addComponents(menu, content);
        layout.addComponent(divs);
        layout.setMargin(false);

    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = NavigatorUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
