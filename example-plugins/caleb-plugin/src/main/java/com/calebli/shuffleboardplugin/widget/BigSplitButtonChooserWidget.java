package com.calebli.shuffleboardplugin.widget;

import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;
import edu.wpi.first.shuffleboard.api.widget.SimpleAnnotatedWidget;
import edu.wpi.first.shuffleboard.plugin.base.data.SendableChooserData;
import edu.wpi.first.shuffleboard.plugin.base.data.types.SendableChooserType;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 * Controller for a split button chooser that resizes buttons to maximize the space.
 */
@Description(name = "Big Split Button Chooser", dataTypes = SendableChooserType.class)
@ParametrizedController("BigSplitButtonChooserWidget.fxml")
public final class BigSplitButtonChooserWidget extends SimpleAnnotatedWidget<SendableChooserData> {

  private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
  private static final PseudoClass error = PseudoClass.getPseudoClass("error");

  @FXML
  private Pane root;
  @FXML
  private SegmentedButton buttons;
  @FXML
  private Pane selectionLabelContainer;

  private final Tooltip activeTooltip = new Tooltip();

  @FXML
  private void initialize() {
    buttons.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    buttons.prefHeightProperty().bind(root.heightProperty());
    buttons.prefWidthProperty().bind(root.widthProperty());


    buttons.setFocusTraversable(false);
    dataOrDefault.addListener((observableValue, old, data) -> {
      Map<String, Object> changes = data.changesFrom(old);

      if (changes.containsKey(SendableChooserData.OPTIONS_KEY)) {
        String selectedOption = data.getSelectedOption();
        buttons.getButtons().setAll(Stream.of(data.getOptions())
            .map(this::createToggleButton)
            .collect(Collectors.toList()));
        selectToggleButton(selectedOption);
      }

      if (changes.containsKey(SendableChooserData.DEFAULT_OPTION_KEY)
          && buttons.getToggleGroup().getSelectedToggle() == null) {
        selectToggleButton(data.getDefaultOption());
      }

      if (changes.containsKey(SendableChooserData.SELECTED_OPTION_KEY)) {
        selectToggleButton(data.getSelectedOption());
      }
      confirmationLabel(data.getActiveOption().equals(data.getSelectedOption()));
    });

    activeTooltip.textProperty().bind(
        dataOrDefault
            .map(SendableChooserData::getActiveOption)
            .map(option -> "Active option: '" + option + "'"));
  }

  private ToggleButton createToggleButton(String option) {
    ToggleButton button = new ToggleButton(option);

    button.prefHeightProperty().bind(buttons.heightProperty());
    buttons.getButtons().addListener(
        (ListChangeListener<? super ToggleButton>)
            (e) -> button.setPrefWidth(buttons.getPrefWidth() / buttons.getButtons().size()));
    buttons.prefWidthProperty().addListener(
        (e) -> button.setPrefWidth(buttons.getPrefWidth() / buttons.getButtons().size()));

    button.setFocusTraversable(false);
    button.setOnAction(e -> setData(dataOrDefault.get().withSelectedOption(option)));
    button.selectedProperty().addListener((observableValue, was, is) -> {
      if (buttons.getButtons().stream().noneMatch(ToggleButton::isSelected)) {
        button.setSelected(true);
      }
    });
    return button;
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

  @Override
  public Pane getView() {
    return root;
  }

  private void selectToggleButton(String option) {
    buttons.getButtons()
        .stream()
        .filter(b -> b.getText().equals(option))
        .findFirst()
        .ifPresent(b -> b.setSelected(true));
  }

}
