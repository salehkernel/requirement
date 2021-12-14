package ir.salmanian.cells;

import ir.salmanian.controllers.RequirementsController;
import ir.salmanian.models.EvaluationStatus;
import ir.salmanian.models.Requirement;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import org.apache.commons.lang.WordUtils;

import java.net.URISyntaxException;

public class RequirementTreeCell extends TreeCell<Requirement> {
    FlowPane pane;
    ImageView requirementIcon;
    Label requirementTitleLabel;

    public RequirementTreeCell() {
        super();
    }

    @Override
    public void updateItem(Requirement item, boolean empty) {
        super.updateItem(item, empty);
        pane = new FlowPane(Orientation.HORIZONTAL, 15, 15);
        requirementIcon = new ImageView();
        requirementTitleLabel = new Label();
        if (item != null && !empty) {
            requirementTitleLabel.setText(item.toString());
            requirementTitleLabel.setWrapText(true);
            try {
                requirementIcon.setImage(new Image(getClass()
                        .getResource(String.format("/img/%s.png", item.getLevel() == 1 ? "user" : "system"))
                        .toURI().toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            pane.getChildren().add(requirementIcon);
            pane.getChildren().add(requirementTitleLabel);
            requirementTitleLabel.styleProperty().bind(Bindings.createStringBinding(
                    () -> concatAllStyles(getTextFillStyle(item))));
            styleProperty().bind(Bindings.createStringBinding(
                    () -> concatAllStyles(getBorderColorStyle(item), "-fx-border-radius: 5px;",
                            getBorderInsetsStyle(item), "-fx-indent: 0px;", "-fx-border-width: 0px 0px 1px 1px;")
            ));
            updateTreeItem(getTreeItem());
            EventDispatcher eventDispatcher = getEventDispatcher();
            pane.setEventDispatcher(new EventDispatcher() {
                @Override
                public Event dispatchEvent(Event event, EventDispatchChain tail) {
                    if (event instanceof MouseEvent) {
                        if (((MouseEvent) event).getClickCount() == 2) {
                            if (!event.isConsumed()) {
                                RequirementsController.openRequirementForm(item);
                            }
                            event.consume();
                        }
                    }
                    return eventDispatcher.dispatchEvent(event, tail);
                }
            });
            setGraphic(pane);
        } else {
            setText("");
            setGraphic(null);
            styleProperty().bind(Bindings.createStringBinding(() -> ""));
        }
    }

    private String getBorderColorStyle(Requirement requirement) {
        String styleName = "-fx-border-color: ";
        String borderColor = getRequirementLevelColor(requirement);
        return String.format("%s%s;", styleName, borderColor);
    }

    private String getRequirementLevelColor(Requirement requirement) {
        String borderColor = "";
        switch (requirement.getLevel() % 10) {
            case 1:
                borderColor = "#6DA472";
                break;
            case 2:
                borderColor = "#D2B48C";
                break;
            case 3:
                borderColor = "#0000FF";
                break;
            case 4:
                borderColor = "#2E8B57";
                break;
            case 5:
                borderColor = "#8B4513";
                break;
            case 6:
                borderColor = "#00FFFF";
                break;
            case 7:
                borderColor = "#B0E0E6";
                break;
            case 8:
                borderColor = "#DEB887";
                break;
            case 9:
                borderColor = "#708090";
                break;
            case 0:
                borderColor = "#8A2BE2";
                break;

        }
        return borderColor;
    }

    private String getTextFillStyle(Requirement requirement) {
        String styleName = "-fx-text-fill: ";
        String fillColor = requirement.getEvaluationStatus() == EvaluationStatus.MET ? "green" : "black";
        return String.format("%s%s;", styleName, fillColor);
    }

    private String getBorderInsetsStyle(Requirement requirement) {
        String styleName = "-fx-border-insets: ";
        String value = String.format("1px 1px 1px %dpx", (requirement.getLevel() - 1) * 25 + 1);
        return String.format("%s%s;", styleName, value);
    }

    private String concatAllStyles(String... styles) {
        StringBuilder allStyles = new StringBuilder("");
        for (String style : styles) {
            if (style != null)
                allStyles.append(style);
        }
        return allStyles.toString();
    }
}
