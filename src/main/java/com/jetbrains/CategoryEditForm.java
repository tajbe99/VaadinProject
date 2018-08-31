package com.jetbrains;

import com.vaadin.data.*;
import com.vaadin.ui.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CategoryEditForm extends FormLayout {
    private CategoryUI ui;
    private CategoryService categoryService = CategoryService.getInstance();
    private Category category;
    private TextField name = new TextField("Name");
    private Button save = new Button("Save");
    private Button close = new Button("Close");
    private Binder<Category> binder = new Binder<>(Category.class);
    private String oldCategoryName;
    public CategoryEditForm(CategoryUI categoryUI){
        this.ui = categoryUI;
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(save, close);
        VerticalLayout textFieldLayout = new VerticalLayout();
        addComponents(name, buttons);
        save.addClickListener(e -> save());
        close.addClickListener(e -> setVisible(false));
        prepareFields();
    }
    private void save() {
        editOnHotelAndCategories(this.category);
        try{
            binder.writeBean(category);
        } catch (ValidationException e){
            Notification.show("Unable to save"+ e.getMessage(), Notification.Type.HUMANIZED_MESSAGE);
        }
        categoryService.save(category);
        ui.editCategory.setEnabled(false);
        ui.delCategory.setEnabled(false);
        exit();
    }

    private void exit() {
        categoryService.save(category);
        ui.updateList();
        setVisible(false);
    }

    public void setCategory(Set<Category> category) {
        this.category = category.iterator().next();
        binder.setBean(this.category);
        setVisible(true);
        oldCategoryName = name.getValue();
    }

    private void editOnHotelAndCategories(Category newCategory) {
        List<Hotel> hotelList = HotelService.getInstance().findAll();
        HashMap<Long, Hotel> newHotelsMap = new HashMap<>();
        for (int i = 0; i < hotelList.size(); i++) {
            if (hotelList.get(i).getCategory().getName().equals(oldCategoryName)) {
                hotelList.get(i).setCategory(new Category(newCategory.getName()));
            }
            newHotelsMap.put(Long.valueOf(i), hotelList.get(i));
        }
            HotelService.hotels = newHotelsMap;
    }

    public void setCategory(Category category) {
        this.category = category;
        binder.setBean(this.category);
        setVisible(true);
    }

    public void prepareFields(){
        binder.forField(name).bind(Category::getName,Category::setName);
        binder.bindInstanceFields(this);
    }

}