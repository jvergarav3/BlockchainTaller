package blockchain.core.chain.presentation.javafx.components;

import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class LinkArrow extends Group {

    private static final double HEAD_LENGTH = 8;
    private static final double HEAD_HALF_WIDTH = 4.5;

    private final Polyline line = new Polyline();
    private final Polygon head = new Polygon();
    private final Text nullLabel = new Text("null");

    public LinkArrow(boolean toNull) {
        getStyleClass().add("link-arrow");
        line.getStyleClass().add("link-line");
        head.getStyleClass().add("link-head");
        nullLabel.getStyleClass().add("link-null");
        getChildren().addAll(line, head);
        if (toNull) {
            getChildren().add(nullLabel);
        }
        setManaged(false);
        setMouseTransparent(true);
    }

    public void route(double direction, double... points) {
        int last = points.length - 2;
        double tipX = points[last];
        double tipY = points[last + 1];
        double baseX = tipX - direction * HEAD_LENGTH;

        line.getPoints().clear();
        for (int i = 0; i < points.length; i += 2) {
            line.getPoints().add(i == last ? baseX : points[i]);
            line.getPoints().add(points[i + 1]);
        }
        head.getPoints().setAll(tipX, tipY, baseX, tipY - HEAD_HALF_WIDTH, baseX, tipY + HEAD_HALF_WIDTH);
    }

    public void placeNullLabel(double x, double centerY) {
        nullLabel.setX(x);
        nullLabel.setY(centerY + 4);
    }

    public void setLinked(boolean on) {
        getStyleClass().remove("linked");
        if (on) {
            getStyleClass().add("linked");
        }
    }
}
