package blockchain.core.chain.presentation.javafx.components;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ToastHost extends VBox {

    public enum Kind {
        SUCCESS("check-circle", "toast-success"),
        ERROR("error", "toast-error"),
        INFO("info", "toast-info");

        private final String icon;
        private final String styleClass;

        Kind(String icon, String styleClass) {
            this.icon = icon;
            this.styleClass = styleClass;
        }
    }

    private static final int MAX_VISIBLE = 3;

    public ToastHost() {
        setAlignment(Pos.BOTTOM_CENTER);
        setSpacing(8);
        setPickOnBounds(false);
        setMouseTransparent(true);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        getStyleClass().add("toast-host");
    }

    public void show(String message, Kind kind) {
        Label text = new Label(message);
        text.setWrapText(true);
        text.setMaxWidth(520);
        HBox toast = new HBox(10, Icons.large(kind.icon), text);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.getStyleClass().addAll("toast", kind.styleClass);
        toast.setOpacity(0);

        while (getChildren().size() >= MAX_VISIBLE) {
            getChildren().remove(0);
        }
        getChildren().add(toast);

        FadeTransition in = new FadeTransition(Duration.millis(180), toast);
        in.setToValue(1);
        TranslateTransition rise = new TranslateTransition(Duration.millis(220), toast);
        rise.setFromY(14);
        rise.setToY(0);
        PauseTransition hold = new PauseTransition(Duration.millis(3400));
        FadeTransition out = new FadeTransition(Duration.millis(260), toast);
        out.setToValue(0);
        SequentialTransition sequence = new SequentialTransition(new ParallelTransition(in, rise), hold, out);
        sequence.setOnFinished(event -> getChildren().remove(toast));
        sequence.play();
    }
}
