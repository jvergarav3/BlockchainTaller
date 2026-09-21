package blockchain.core.chain.presentation.javafx.components;

import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

import java.util.Map;

public class Icon extends Pane {

    private static final double GRID = 24;

    private static final Map<String, String> PATHS = Map.ofEntries(
            Map.entry("arrow-down", "M20 12l-1.41-1.41L13 16.17V4h-2v12.17l-5.58-5.59L4 12l8 8 8-8z"),
            Map.entry("block", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zM4 12c0-4.42 3.58-8 8-8 1.85 0 3.55.63 4.9 1.69L5.69 16.9A7.902 7.902 0 0 1 4 12zm8 8c-1.85 0-3.55-.63-4.9-1.69L18.31 7.1A7.902 7.902 0 0 1 20 12c0 4.42-3.58 8-8 8z"),
            Map.entry("check-circle", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"),
            Map.entry("chevron-left", "M15.41 7.41 14 6l-6 6 6 6 1.41-1.41L10.83 12z"),
            Map.entry("chevron-right", "M10 6 8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"),
            Map.entry("close", "M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"),
            Map.entry("copy", "M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"),
            Map.entry("edit", "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a.996.996 0 0 0 0-1.41l-2.34-2.34a.996.996 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"),
            Map.entry("error", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"),
            Map.entry("info", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"),
            Map.entry("layers", "M11.99 18.54l-7.37-5.73L3 14.07l9 7 9-7-1.63-1.27-7.38 5.74zM12 16l7.36-5.73L21 9l-9-7-9 7 1.63 1.27L12 16z"),
            Map.entry("link", "M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z"),
            Map.entry("plus", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"),
            Map.entry("search", "M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"),
            Map.entry("shield", "M12 1 3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z"));

    private final SVGPath path = new SVGPath();
    private String name;

    public Icon() {
        this(null);
    }

    public Icon(String name) {
        getStyleClass().add("icon");
        path.getStyleClass().add("icon-path");
        Scale scale = new Scale();
        scale.xProperty().bind(widthProperty().divide(GRID));
        scale.yProperty().bind(heightProperty().divide(GRID));
        path.getTransforms().add(scale);
        getChildren().add(path);
        setMouseTransparent(true);
        if (name != null) {
            setName(name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String content = PATHS.get(name);
        if (content == null) {
            throw new IllegalArgumentException("Unknown icon: " + name);
        }
        if (this.name != null) {
            getStyleClass().remove("icon-" + this.name);
        }
        this.name = name;
        getStyleClass().add("icon-" + name);
        path.setContent(content);
    }
}
