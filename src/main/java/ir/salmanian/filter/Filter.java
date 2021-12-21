package ir.salmanian.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic Filter class is used to show filter criteria name and filter items of that criteria in
 * filterTreeView of {@link ir.salmanian.controllers.RequirementsController} class. This class also
 * contains a Field property which indicates this filter is belongs to which filed of
 * {@link ir.salmanian.models.Requirement} class
 */
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
