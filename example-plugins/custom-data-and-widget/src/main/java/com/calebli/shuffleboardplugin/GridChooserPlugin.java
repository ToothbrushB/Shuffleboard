package com.calebli.shuffleboardplugin;

import com.calebli.shuffleboardplugin.widget.GridChooserWidget;
import edu.wpi.first.shuffleboard.api.plugin.Description;
import edu.wpi.first.shuffleboard.api.plugin.Plugin;
import edu.wpi.first.shuffleboard.api.plugin.Requires;
import edu.wpi.first.shuffleboard.api.widget.ComponentType;
import edu.wpi.first.shuffleboard.api.widget.WidgetType;

import java.util.List;

/**
 * An example plugin that provides a custom data type (a 2D point) and a simple widget for viewing such data.
 */
@Requires(group = "edu.wpi.first.shuffleboard", name = "Base", minVersion = "1.0.0")
@Description(
    group = "com.calebli.shuffleboardplugin",
    name = "ThePlugin",
    version = "2023.2.18",
    summary = "An example plugin that does grid selector"
)
public final class GridChooserPlugin extends Plugin {

//  @Override
//  public List<DataType> getDataTypes() {
//    return List.of(
//        PointType.Instance
//    );
//  }

    @Override
    public List<ComponentType> getComponents() {
        return List.of(
                WidgetType.forAnnotatedWidget(GridChooserWidget.class)
        );
    }
}
