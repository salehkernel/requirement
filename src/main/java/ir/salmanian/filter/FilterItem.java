package ir.salmanian.filter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Generic FilterItem class is used to be show in the listview of filtering items for each criteria.
 * the listview cell factory is set to checkbox list cell.
 * so each item of this list view should have a {@link BooleanProperty} filed.
 * see also {@link Filter}
 */
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
