package ir.salmanian.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Filter<T> {
    private String criteriaName;
    private Field field;
    private List<FilterItem<T>> items = new ArrayList<>();

    public Filter(String criteriaName, Field field, List<FilterItem<T>> items) {
        this.criteriaName = criteriaName;
        this.items = items;
        this.field = field;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public Filter<T> setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
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
