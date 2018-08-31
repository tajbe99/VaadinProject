package com.jetbrains;

import com.vaadin.data.*;
import com.vaadin.ui.*;

import java.util.Set;

public class HotelEditForm extends FormLayout {
    private HotelUI ui;
    private HotelService hotelService = HotelService.getInstance();
    private Hotel hotel;
    private Binder<Hotel> binder = new Binder<>(Hotel.class);
    private TextField name = new TextField("Name");
    private TextField address = new TextField("Address");
    private TextField rating = new TextField("Rating");
    private DateField operatesFrom = new DateField("Date");
    private CategoryService categoryService = CategoryService.getInstance();
    private Set<Category> category = categoryService.findAll();
    private NativeSelect<Category> categoryMenu = new NativeSelect<>("Category");
    private TextField url = new TextField("Url");
    private TextArea description = new TextArea("Description");
    private Button save = new Button("Save");
    private Button close = new Button("Close");

    public HotelEditForm(HotelUI hotelUI){
        this.ui = hotelUI;
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(save, close);
        addComponents(name, address, rating, operatesFrom, categoryMenu,
                url, description, buttons);
        categoryMenu.setItems(category);
        save.addClickListener(e -> save());
        close.addClickListener(e -> setVisible(false));
        prepareFields();
    }
    private void save() {
        if (binder.isValid()){
            try{
                binder.writeBean(hotel);
            } catch (ValidationException e){
                Notification.show("Unable to save"+ e.getMessage(), Notification.Type.HUMANIZED_MESSAGE);
            }
            hotelService.save(hotel);
            exit();
        } else {
            Notification.show("Unable to save! Please review error and fix them.", Notification.Type.ERROR_MESSAGE);
        }
    }

    private void exit() {
        hotelService.save(hotel);
        ui.updateList();
        setVisible(false);
    }

    public void setHotel(Set<Hotel> hotel) {
        updateCategory();
        this.hotel = hotel.iterator().next();
        binder.setBean(this.hotel);
        setVisible(true);
    }

    public void setHotel(Hotel hotel) {
        updateCategory();
        this.hotel = hotel;
        binder.setBean(this.hotel);
        setVisible(true);
    }

    private void updateCategory() {
        this.category = categoryService.findAll();
        categoryMenu.setItems(category);
    }

    @SuppressWarnings("serial")
    public void prepareFields(){
        binder.forField(name).asRequired("Please enter the name...").bind(Hotel::getName, Hotel::setName);
        Validator<String> addresValidator = (Validator<String>) (s, valueContext) -> {
            if (s.length()<5)
                return ValidationResult.error("The address is too short");
            return ValidationResult.ok();
        };
        Validator<Long> dateValidator = (Validator<Long>) (s, valueContext) -> {
            if (s < 0)
                return ValidationResult.error("The date is incorrect");
            return ValidationResult.ok();
        };
        Validator<String> ratingValidator = (Validator<String>) (s, valueContext) -> {
            long rating = Integer.valueOf(s);
            if (rating < 0 || rating > 5 )
                return ValidationResult.error("Incorrect rating");
            return ValidationResult.ok();
        };
        binder.forField(address)
                .withValidator(addresValidator)
                .bind(Hotel::getAddress, Hotel::setAddress);
        binder.forField(rating)
                .withValidator(ratingValidator)
                .asRequired("Please set the rating...")
                .bind(Hotel::getRating, Hotel::setRating);

        binder.forField(operatesFrom)
                .withConverter(new DateConverter())
                .withValidator(dateValidator)
                .asRequired("Please enter the date...")
                .bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);
        binder.forField(categoryMenu)
                .withNullRepresentation(new Category("No category"))
                .asRequired("Please enter the category...")
                .bind(Hotel::getCategory, Hotel::setCategory);
        binder.forField(url)
                .asRequired("Please enter the url...")
                .bind(Hotel::getUrl, Hotel::setUrl);
        binder.forField(description)
                .bind(Hotel::getDescription, Hotel::setDescription);
        binder.bindInstanceFields(this);

        name.setDescription("Hotel name");
        address.setDescription("Hotel adress");
        rating.setDescription("Hotel rating");
        operatesFrom.setDescription("Hotel create date");
        categoryMenu.setDescription("Hotel category");
        url.setDescription("Hotel Link");
        description.setDescription("Hotel description");
    }
}