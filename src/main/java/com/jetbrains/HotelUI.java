package com.jetbrains;
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@linknRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
public class HotelUI extends VerticalLayout implements View {
    final VerticalLayout contentLayout = new VerticalLayout();
    final HotelService hotelService = HotelService.getInstance();
    final Grid<Hotel> hotelGrid = new Grid<>(Hotel.class);
    final TextField filterName = new TextField();
    final TextField filterAddress = new TextField();
    final Panel panel = new Panel();
    final HorizontalLayout panelContent = new HorizontalLayout();
    final HorizontalLayout gridEditformContent = new HorizontalLayout();
    final Button addHotel = new Button("Add Hotel");
    final Button delHotel = new Button("Delete Hotel");
    final Button editHotel = new Button("Edit Hotel");
    final CategoryUI categoryUI = new CategoryUI();
    private HotelEditForm form = new HotelEditForm(this);

    public HotelUI(){

    }
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        editHotel.setEnabled(false);
        form.setVisible(false);
        initGrid();
        filterName.addValueChangeListener(e -> updateFilterByName());
        filterName.setPlaceholder("Input name...");
        filterAddress.setPlaceholder("Input address...");
        filterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterAddress.addValueChangeListener(e -> updateFilterByAddress());
        panelContent.addComponents(filterName, filterAddress, addHotel,delHotel,editHotel);
        delHotel.setEnabled(false);
        delHotel.addClickListener(e -> {
            Set<Hotel> delCandidate = hotelGrid.getSelectedItems();
            hotelService.delete(delCandidate);
            delHotel.setEnabled(false);
            updateList();
            form.setVisible(false);
            editHotel.setEnabled(false);
        });
        editHotel.addClickListener(e -> {
            form.setHotel(hotelGrid.asSingleSelect().getValue());
        });
        panel.setContent(panelContent);
        panel.setWidthUndefined();
        gridEditformContent.addComponents(hotelGrid,form);
        contentLayout.addComponents(panel, gridEditformContent);
        gridEditformContent.setMargin(false);
        this.setMargin(false);
        addComponent(contentLayout);
        addHotel.addClickListener(e -> form.setHotel(new Hotel()));
    }

    void updateFilterByName() {
        List<Hotel> hotelList = hotelService.findName(filterName.getValue()) ;
        hotelGrid.setItems(hotelList);
    }

    void updateFilterByAddress() {
        List<Hotel> hotelList = hotelService.findAddress(filterAddress.getValue()) ;
        hotelGrid.setItems(hotelList);
    }

    private void initGrid() {
        hotelGrid.setWidth("950");
        hotelGrid.setItems(hotelService.findAll());
        hotelGrid.setColumnOrder("name", "address", "rating", "category");
        hotelGrid.removeColumn("url");
        hotelGrid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null){
                delHotel.setEnabled(true);
                form.setHotel(e.getValue());
            }
        });
        hotelGrid.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>Link at booking.com</a>",
                new HtmlRenderer()).setCaption("url");
        hotelGrid.setRowHeight(50);
        hotelGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        hotelGrid.addSelectionListener(event -> {
            Set<Hotel> selected = event.getAllSelectedItems();
            if (selected.size() > 1){
                delHotel.setEnabled(true);
                editHotel.setEnabled(false);
            }
            if (selected.size() == 1){
                editHotel.setEnabled(true);
                delHotel.setEnabled(true);
            }
            if (selected.size() == 0){
                delHotel.setEnabled(false);
                editHotel.setEnabled(false);
            }
        });
    }
    void updateList() {
        List<Hotel> hotelList = hotelService.findAll("") ;
        hotelGrid.setItems(hotelList);
    }

}