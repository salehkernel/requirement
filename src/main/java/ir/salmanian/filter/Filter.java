package ir.salmanian.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Filter<T> {
    private String name;
    private Field field;
    private List<FilterItem<T>> items = new ArrayList<>();

    public Filter(String name, Field field, List<FilterItem<T>> items) {
        this.name = name;
        this.items = items;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public Filter<T> setName(String name) {
        this.name = name;
        return this;
    }

    public Field getField() {
        return field;
    }

    public Filter<T> setField(Field field) {
        this.field = field;
        return this;
    }

    public List<FilterItem<T>> getItems() {
        return items;
    }

    public Filter<T> setItems(List<FilterItem<T>> items) {
        this.items = items;
        return this;
    }
}
