package com.calebli.shuffleboardplugin.widget;

import com.google.common.collect.ImmutableList;
import edu.wpi.first.shuffleboard.api.prefs.Group;
import edu.wpi.first.shuffleboard.api.prefs.Setting;
import edu.wpi.first.shuffleboard.api.widget.ComplexAnnotatedWidget;
import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;
import edu.wpi.first.shuffleboard.plugin.base.data.SendableChooserData;
import edu.wpi.first.shuffleboard.plugin.base.data.types.SendableChooserType;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;




/**
 * A widget for Shuffleboard that aligns options in a grid.
 */
@Description(name = "GridChooserWidget", dataTypes = SendableChooserType.class)
@ParametrizedController("GridChooserWidget.fxml")
public class GridChooserWidget extends ComplexAnnotatedWidget<SendableChooserData> {

  private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
  private static final PseudoClass error = PseudoClass.getPseudoClass("error");
  private final SimpleDoubleProperty selectionColorR = new SimpleDoubleProperty(0);
  private final SimpleDoubleProperty selectionColorG = new SimpleDoubleProperty(0);
  private final SimpleDoubleProperty selectionColorB = new SimpleDoubleProperty(0.8);
  private final SimpleDoubleProperty selectionColorOpacity = new SimpleDoubleProperty(0.5);
  private final SimpleObjectProperty<Color> selectionColor =
      new SimpleObjectProperty<>(
          new Color(selectionColorR.get(),
              selectionColorG.get(),
              selectionColorB.get(),
              selectionColorOpacity.get()));
  private final SimpleStringProperty coneUrl = new SimpleStringProperty("");
  private final SimpleStringProperty cubeUrl = new SimpleStringProperty("");
  private final SimpleStringProperty hybridUrl = new SimpleStringProperty("");
  private final SimpleObjectProperty<GridChild> selectedNode = new SimpleObjectProperty<>();
  private final ArrayList<GridChild> children = new ArrayList<>();
  private final Tooltip activeTooltip = new Tooltip();
  @FXML
  private Pane root;
  @FXML
  private GridPane gridPane;
  @FXML
  private Pane selectionLabelContainer;
  @FXML
  private Label selectedLabel;

  @FXML
  private void initialize() {
    dataOrDefault.addListener((observableValue, oldData, newData) -> {
      final Map<String, Object> changes = newData.changesFrom(oldData);
      if (changes.containsKey(SendableChooserData.OPTIONS_KEY)) {
        updateOptions(newData.getOptions());
      }
      if (changes.containsKey(SendableChooserData.DEFAULT_OPTION_KEY)) {
        updateDefaultValue(newData.getDefaultOption());
      }
      if (changes.containsKey(SendableChooserData.SELECTED_OPTION_KEY)) {
        updateSelectedValue(newData.getSelectedOption());
      }
      confirmationLabel(newData.getActiveOption().equals(newData.getSelectedOption()));
    });
    activeTooltip.textProperty().bind(
        dataOrDefault
            .map(SendableChooserData::getActiveOption)
            .map(option -> "Active option: '" + option + "'"));

    selectedNode.addListener((observable) -> {
      SendableChooserData currentData = getData();
      if (selectedNode.get() == null) {
        String defaultOption = currentData.getDefaultOption();
        setData(currentData.withSelectedOption(defaultOption));
        for (GridChild child : children) {
          if (child.friendlyName.equals(defaultOption)) {
            selectedNode.set(child);
          }
        }
      } else {
        setData(currentData.withSelectedOption(selectedNode.get().friendlyName));
      }
      selectedLabel.setText(selectedNode.get().friendlyName);
    });

    gridPane.setHgap(5);
    gridPane.setVgap(5);

    gridPane.prefWidthProperty().bind(Bindings.min(root.widthProperty(), root.heightProperty()));
    gridPane.prefHeightProperty().bind(Bindings.min(root.widthProperty(), root.heightProperty()));

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

  private void confirmationLabel(boolean confirmation) {
    Label activeSelectionLabel;
    if (confirmation) {
      activeSelectionLabel = fontAwesome.create(FontAwesome.Glyph.CHECK);
    } else {
      activeSelectionLabel = fontAwesome.create(FontAwesome.Glyph.EXCLAMATION);
    }
    activeSelectionLabel.getStyleClass().add("confirmation-label");
    activeSelectionLabel.pseudoClassStateChanged(error, !confirmation);
    activeSelectionLabel.setTooltip(activeTooltip);
    selectionLabelContainer.getChildren().setAll(activeSelectionLabel);
  }

  private void updateOptions(String... options) {
    int n = 0;
    for (int i = 0; i < Math.sqrt(options.length); i++) {
      for (int j = 0; j < Math.sqrt(options.length) && n < options.length; j++) {
        GridChild child = new GridChild(n, options[n]);
        child.prefWidthProperty().bind(
            gridPane.widthProperty().divide(Math.sqrt(options.length) - 1));
        child.prefHeightProperty().bind(
            gridPane.prefHeightProperty().divide(Math.sqrt(options.length) - 1));

        child.setOnMouseClicked((e) -> {
          if (selectedNode.get() != null) {
            selectedNode.get().deselect();
          }
          selectedNode.set(child); // set selected node to this node
          selectedNode.get().select();
        });
        children.add(child);
        gridPane.add(child, j, i);
        n++;
      }
    }
  }

  private void updateDefaultValue(String defaultValue) {
    if (selectedNode.get() == null) { // if there is nothing selected
      for (GridChild child : children) {
        if (child.friendlyName.equals(defaultValue)) { // find the right child
          if (selectedNode.get() != null) {
            selectedNode.get().deselect();
          }
          selectedNode.set(child); // set selected node to this node
          selectedNode.get().select();
        }
      }
    }
  }

  private void updateSelectedValue(String selectedValue) {
    for (GridChild child : children) {
      if (child.friendlyName.equals(selectedValue)) {
        if (selectedNode.get() != null) {
          selectedNode.get().deselect();
        }
        selectedNode.set(child); // set selected node to this node
        selectedNode.get().select();
      }
    }
  }

  @Override
  public Pane getView() {
    return root;
  }

  @Override
  public List<Group> getSettings() {
    return ImmutableList.of(
        Group.of("Icon Settings",
            Setting.of("Cube", cubeUrl, String.class),
            Setting.of("Cone", coneUrl, String.class),
            Setting.of("Hybrid", hybridUrl, String.class)
        ),
        Group.of("Selection Color",
            Setting.of("R", selectionColorR, Double.class),
            Setting.of("G", selectionColorG, Double.class),
            Setting.of("B", selectionColorB, Double.class),
            Setting.of("Opacity", selectionColorOpacity, Double.class)
        )
    );
  }

  /**
   * Class for a box in the grid chooser.
   */
  public class GridChild extends StackPane {
    @SuppressWarnings("checkstyle:MemberName")
    private final int i;
    private final String friendlyName;
    private final Rectangle highlight;
    private final ImageView imageView = new ImageView();

    /**
     * Makes a grid child given its position in the frame and the name of the item it represents.

     * @param i the position in the frame
     * @param friendlyName name of the item it represents
     */
    public GridChild(int i, String friendlyName) {
      this.i = i;
      this.friendlyName = friendlyName;
      this.highlight = new Rectangle(50, 50);
      highlight.fillProperty().bind(selectionColor);
      highlight.widthProperty().bind(widthProperty().multiply(0.9));
      highlight.heightProperty().bind(heightProperty().multiply(0.9));

      highlight.setStroke(Color.BLACK);
      highlight.setStrokeWidth(2);
      highlight.setVisible(false);

      setImageView();
      cubeUrl.addListener((e) -> setImageView());
      coneUrl.addListener((e) -> setImageView());
      hybridUrl.addListener((e) -> setImageView());

      imageView.fitHeightProperty().bind(heightProperty().multiply(0.9));
      imageView.fitWidthProperty().bind(widthProperty().multiply(0.9));

      getChildren().addAll(imageView, highlight);
    }

    private void setImageView() {
      Image cubeImage;
      Image coneImage;
      Image hybridImage;

      try (InputStream cubeImageStream = new FileInputStream(cubeUrl.getValue())) {
        cubeImage = new Image(cubeImageStream);
      } catch (IOException e) {
        cubeImage =
            new Image(Objects.requireNonNull(
                GridChooserWidget.class.getResourceAsStream(
                    "/com/calebli/shuffleboardplugin/widget/cube.png")));
      }

      try (InputStream cubeImageStream = new FileInputStream(coneUrl.getValue())) {
        coneImage = new Image(cubeImageStream);
      } catch (IOException e) {
        coneImage =
            new Image(Objects.requireNonNull(
                GridChooserWidget.class.getResourceAsStream(
                    "/com/calebli/shuffleboardplugin/widget/cone.png")));
      }

      try (InputStream cubeImageStream = new FileInputStream(hybridUrl.getValue())) {
        hybridImage = new Image(cubeImageStream);
      } catch (IOException e) {
        hybridImage =
            new Image(Objects.requireNonNull(
                GridChooserWidget.class.getResourceAsStream(
                    "/com/calebli/shuffleboardplugin/widget/hybrid.png")));
      }
      switch (i + 1) {
        case 8, 5 -> imageView.setImage(cubeImage);
        case 4, 9, 6, 7 -> imageView.setImage(coneImage);
        default -> imageView.setImage(hybridImage); // 1, 2, 3
      }
    }

    public void select() {
      highlight.setVisible(true);
    }

    public void deselect() {
      highlight.setVisible(false);
    }

    @Override
    public String toString() {
      return "GridChild{" + "i=" + i + ", friendlyName='" + friendlyName + '\'' + '}';
    }
  }
}
