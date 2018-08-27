package com.jetbrains;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import java.util.List;
import java.util.Set;

public class CategoryUI extends VerticalLayout implements View {
    final Grid<Category> categoryGrid = new Grid<>(Category.class);
    final VerticalLayout layout = new VerticalLayout();
    final HorizontalLayout panelContent = new HorizontalLayout();
    final Panel panel = new Panel();
    final Button addCategory = new Button("Add Category");
    final Button delCategory = new Button("Delete Category");
    final Button editCategory = new Button("Edit Category");
    private Set<Category> categories;
    private CategoryService categoryService = CategoryService.getInstance();
    private CategoryEditForm categoryEditForm = new CategoryEditForm(this);
    final HorizontalLayout forEditForm = new HorizontalLayout();


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.setMargin(false);
        initCategoryGrid();
        initUI();
        initToolBtn();
        addComponent(layout);
    }

    private void initUI() {
        categoryEditForm.setVisible(false);
        panelContent.addComponents(addCategory,delCategory,editCategory);
        panel.setContent(panelContent);
        forEditForm.addComponents(categoryGrid, categoryEditForm);
        panel.setWidthUndefined();
        layout.addComponents(panel, forEditForm);
    }

    private void initToolBtn() {
        editCategory.setEnabled(false);
        delCategory.setEnabled(false);
        addCategory.addClickListener(e -> {
            categoryEditForm.setCategory(new Category());
        });
        delCategory.addClickListener(e -> {
            Set<Category> delCandidate = categoryGrid.getSelectedItems();
            categoryService.delete(delCandidate);
            delCategory.setEnabled(false);
            updateList();
            categoryEditForm.setVisible(false);
            editCategory.setEnabled(false);
        });
        editCategory.addClickListener(e -> {
            categoryEditForm.setCategory(categoryGrid.asSingleSelect().getValue());
        });
    }

    private void initCategoryGrid() {
        categoryGrid.setItems(categoryService.findAll());
        categoryGrid.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null){
                delCategory.setEnabled(true);
                editCategory.setEnabled(true);
            }
        });
        categoryGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        categoryGrid.addSelectionListener(event -> {
            Set<Category> selected = event.getAllSelectedItems();
            if (selected.size() > 1){
                delCategory.setEnabled(true);
            editCategory.setEnabled(false);
            }
            if (selected.size() == 1){
                editCategory.setEnabled(true);
            delCategory.setEnabled(true);
            }
            if (selected.size() == 0){
                delCategory.setEnabled(false);
                editCategory.setEnabled(false);
            }
        });
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
        editCategory.setCaption("Edit");
        if (categories.size() == 1) {
            editCategory.setVisible(true);
            if (!categories.toArray(new Category[1])[0].isPersisted()) {
                editCategory.setCaption("Save");
            }
        } else {
            editCategory.setVisible(false);
        }
        setVisible(true);
    }

    void updateList() {
        Set<Category> categories = categoryService.findAll();
        categoryGrid.setItems(categories);
    }
}
