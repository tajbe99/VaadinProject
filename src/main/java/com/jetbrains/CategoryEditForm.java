package com.jetbrains;

import com.vaadin.data.*;
import com.vaadin.data.*;
import com.vaadin.ui.*;
public class CategoryEditForm extends FormLayout {
    private CategoryUI ui;
    private CategoryService categoryService = CategoryService.getInstance();
    private Category category;
    private TextField name = new TextField("Name");
    private Button save = new Button("Save");
    private Button close = new Button("Close");
    private Binder<Category> binder = new Binder<>(Category.class);
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

    public void setCategory(Category category) {
        this.category = category;
        binder.setBean(this.category);
        setVisible(true);
    }
    public Category getCategory(Category category) {
        return category;
    }
    public void prepareFields(){
        binder.forField(name).bind(Category::getName,Category::setName);
        binder.bindInstanceFields(this);
    }

}