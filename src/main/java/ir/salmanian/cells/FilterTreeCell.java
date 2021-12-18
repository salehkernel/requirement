package ir.salmanian.cells;

import ir.salmanian.filter.Filter;
import ir.salmanian.filter.FilterItem;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

public class FilterTreeCell extends TreeCell<Filter<Object>> {

    @Override
    protected void updateItem(Filter<Object> item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            if (item.getName() != null) {
                Label filterNameLabel = new Label(item.getName());
                setGraphic(filterNameLabel);
            } else {
                ListView<FilterItem<Object>> filterItemListView = new ListView<>();
                ObservableList<FilterItem<Object>> filterItemObservableList = FXCollections.observableArrayList();
                filterItemObservableList.addAll(item.getItems());
                filterItemListView.setItems(filterItemObservableList);
                filterItemListView.setCellFactory(CheckBoxListCell.forListView(new Callback<FilterItem<Object>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(FilterItem<Object> param) {
                        return param.selectedProperty();
                    }
                }));
                filterItemListView.prefHeightProperty().bind(Bindings.size(filterItemObservableList).multiply(27));
                filterItemListView.setPrefWidth(200);
                setGraphic(filterItemListView);
            }
        } else {
            setText("");
            setGraphic(null);
        }
    }
}