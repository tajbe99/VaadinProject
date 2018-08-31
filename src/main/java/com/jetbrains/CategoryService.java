package com.jetbrains;


import java.util.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryService {

    private static CategoryService instance;
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    private final HashMap<Long, Category> categoryMap = new HashMap<>();
    private long nextId = 0;

    private CategoryService() {
    }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
            String[] initialCategories = {"Hotel", "Hostel", "GuestHouse", "Appartments"};
            for (String s : initialCategories) {
                Category ht = new Category(s);
                instance.save(ht);
            }
        }
        return instance;
    }

    public synchronized void save(Category category) {
        if (category == null) {
            LOGGER.log(Level.SEVERE, "Category is null");
            return;
        }
        if (category.getId() == null) {
            category.setId(nextId++);
        }
        try {
            category = (Category) category.clone();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        categoryMap.put(category.getId(), category);
    }

    public synchronized void delete(Set<Category> category) {

        for (Category delCategory : category) {
            categoryMap.remove(delCategory.getId());
        }
        List<Hotel> hotelList = HotelService.getInstance().findAll();
        HashMap<Long, Hotel> newHotelsMap = new HashMap<>();
        for (Category oldCategory : category) {
            for (int i = 0; i < hotelList.size(); i++) {

                if (hotelList.get(i).getCategory().getName().equals(oldCategory.getName())) {
                    hotelList.get(i).setCategory(new Category("No category"));
                }
                newHotelsMap.put(Long.valueOf(i), hotelList.get(i));
            }
            HotelService.hotels = newHotelsMap;
        }
    }

    public synchronized Set<Category> findAll() {
        Set<Category> result = new HashSet<>();
        try {
            for (Category c : categoryMap.values())
                result.add(c.clone());
        } catch (CloneNotSupportedException e) {
            LOGGER.log(Level.SEVERE, "Clone not supported");
        }
        return result;
    }

    public synchronized boolean findOneCategory(String name) {
        for (Category category: categoryMap.values()){
            if (name.equals(category.getName())){
                return true;
            }
        }
        return false;
    }

}
