package ir.salmanian.filter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FilterItem<T> {
    private T item;
    private BooleanProperty selected;

    public FilterItem(T item) {
        this.item = item;
        selected = new SimpleBooleanProperty(false);
    }

    public T getItem() {
        return item;
    }

    public FilterItem<T> setItem(T item) {
        this.item = item;
        return this;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }


    @Override
    public String toString() {
        return item.toString();
    }
}
