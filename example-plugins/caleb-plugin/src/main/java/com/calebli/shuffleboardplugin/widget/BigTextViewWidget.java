package com.calebli.shuffleboardplugin.widget;

import com.google.common.collect.ImmutableList;
import edu.wpi.first.shuffleboard.api.prefs.Group;
import edu.wpi.first.shuffleboard.api.prefs.Setting;
import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;
import edu.wpi.first.shuffleboard.api.widget.SimpleAnnotatedWidget;
import edu.wpi.first.shuffleboard.plugin.base.data.AnalogInputData;
import edu.wpi.first.shuffleboard.plugin.base.data.types.AnalogInputType;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


/**
 * A widget for displaying data as text. This supports text, numbers, and booleans.
 */
@Description(
    name = "Big Text View",
    summary = "Display a value as BIGtext",
    dataTypes = {
        String.class, Number.class, Boolean.class, AnalogInputType.class
    })
@ParametrizedController("BigTextViewWidget.fxml")
public class BigTextViewWidget extends SimpleAnnotatedWidget<Object> {

  @FXML
  private Pane root;
  @FXML
  private Label label;
  private final SimpleDoubleProperty selectionColorR = new SimpleDoubleProperty(1);
  private final SimpleDoubleProperty selectionColorG = new SimpleDoubleProperty(1);
  private final SimpleDoubleProperty selectionColorB = new SimpleDoubleProperty(1);
  private final SimpleDoubleProperty selectionColorOpacity = new SimpleDoubleProperty(1);
  private final SimpleObjectProperty<Color> selectionColor =
      new SimpleObjectProperty<>(
          new Color(selectionColorR.get(),
              selectionColorG.get(),
              selectionColorB.get(),
              selectionColorOpacity.get()));

  private final SimpleDoubleProperty widthFactor = new SimpleDoubleProperty(75);
  private final SimpleDoubleProperty heightFactor = new SimpleDoubleProperty(15);

  @FXML
  private void initialize() {
    dataProperty().addListener((__, prev, cur) -> {
      if (cur != null) {
        if (cur instanceof Number) {
          label.setText(String.valueOf(((Number) cur).doubleValue()));
        } else if (cur instanceof String || cur instanceof Boolean) {
          label.setText(cur.toString());
        } else if (cur instanceof AnalogInputData) {
          label.setText(((AnalogInputData) cur).getValue().toString());
        } else {
          throw new UnsupportedOperationException("Unsupported type: " + cur.getClass().getName());
        }
      }
    });


    label.scaleXProperty().bind(root.widthProperty().divide(widthFactor));
    label.scaleYProperty().bind(root.heightProperty().divide(heightFactor));

    label.textFillProperty().bind(selectionColor);
    selectionColorR.addListener((e) -> selectionColor.set(
        new Color(
            selectionColorR.get(),
            selectionColorG.get(),
            selectionColorB.get(),
            selectionColorOpacity.get())));
    selectionColorG.addListener((e) -> selectionColor.set(
        new Color(
            selectionColorR.get(),
            selectionColorG.get(),
            selectionColorB.get(),
            selectionColorOpacity.get())));
    selectionColorB.addListener((e) -> selectionColor.set(
        new Color(
            selectionColorR.get(),
            selectionColorG.get(),
            selectionColorB.get(),
            selectionColorOpacity.get())));
    selectionColorOpacity.addListener((e) -> selectionColor.set(
        new Color(
            selectionColorR.get(),
            selectionColorG.get(),
            selectionColorB.get(),
            selectionColorOpacity.get())));

  }
  @Override
  public List<Group> getSettings() {
    return ImmutableList.of(
        Group.of("Font Color",
            Setting.of("R", selectionColorR, Double.class),
            Setting.of("G", selectionColorG, Double.class),
            Setting.of("B", selectionColorB, Double.class),
            Setting.of("Opacity", selectionColorOpacity, Double.class)
        ),
        Group.of("Size",
          Setting.of("Width", widthFactor, Double.class),
          Setting.of("Height", heightFactor, Double.class)
        )
    );
  }
  @Override
  public Pane getView() {
    return root;
  }

}
