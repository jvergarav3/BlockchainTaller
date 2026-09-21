package blockchain.core.chain.presentation.javafx.components;

import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.presentation.javafx.Fonts;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Optional;

public final class BlockDialogs {

    private BlockDialogs() {
    }

    public static Optional<String> rectify(Window owner, BlockInfo block) {
        Label current = Fonts.applyMono(new Label(block.data()));
        current.getStyleClass().add("readonly-box");
        current.setWrapText(true);
        current.setMaxWidth(Double.MAX_VALUE);

        TextArea input = new TextArea(block.data());
        input.getStyleClass().add("input");
        input.setWrapText(true);
        input.setPrefRowCount(3);

        Label error = new Label("Los datos no pueden estar vacíos.");
        error.getStyleClass().add("form-error");
        error.setVisible(false);
        error.setManaged(false);

        Button cancel = button("Cancelar", "btn-ghost", null);
        Button save = button("Guardar corrección", "btn-primary", "edit");

        VBox card = card(
                header("edit", "tone-warning", "Rectificar bloque #" + block.id(),
                        "Se agregará un bloque de corrección. El original no se modifica."),
                field("Datos actuales", current),
                field("Nuevos datos", input),
                error,
                footer(cancel, save));

        String[] result = new String[1];
        Stage stage = stage(owner, card);
        cancel.setOnAction(event -> stage.close());
        save.setOnAction(event -> {
            String text = input.getText();
            if (text == null || text.isBlank()) {
                error.setVisible(true);
                error.setManaged(true);
                return;
            }
            result[0] = text;
            stage.close();
        });
        stage.setOnShown(event -> input.requestFocus());
        stage.showAndWait();
        return Optional.ofNullable(result[0]);
    }

    public static boolean confirmAnnul(Window owner, BlockInfo block) {
        Label message = new Label("Se agregará un bloque de anulación al final de la cadena. "
                + "El bloque #" + block.id() + " no se borra: la cadena es inmutable.");
        message.setWrapText(true);
        message.getStyleClass().add("modal-text");

        Button cancel = button("Cancelar", "btn-ghost", null);
        Button confirm = button("Anular bloque", "btn-danger-solid", "block");

        VBox card = card(
                header("block", "tone-danger", "¿Anular el bloque #" + block.id() + "?", "Esta acción no se puede deshacer."),
                message,
                footer(cancel, confirm));

        boolean[] result = new boolean[1];
        Stage stage = stage(owner, card);
        cancel.setOnAction(event -> stage.close());
        confirm.setOnAction(event -> {
            result[0] = true;
            stage.close();
        });
        stage.setOnShown(event -> cancel.requestFocus());
        stage.showAndWait();
        return result[0];
    }

    private static Stage stage(Window owner, VBox card) {
        StackPane root = new StackPane(card);
        root.getStyleClass().add("modal-root");
        root.setStyle("-fx-font-family: '" + Fonts.ui() + "';");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().addAll(owner.getScene().getStylesheets());

        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        double[] drag = new double[2];
        card.setOnMousePressed(event -> {
            drag[0] = event.getScreenX() - stage.getX();
            drag[1] = event.getScreenY() - stage.getY();
        });
        card.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - drag[0]);
            stage.setY(event.getScreenY() - drag[1]);
        });

        stage.setOnShowing(event -> {
            stage.setX(owner.getX() + (owner.getWidth() - 520) / 2);
            stage.setY(owner.getY() + owner.getHeight() / 5);
        });
        return stage;
    }

    private static VBox card(Node... children) {
        VBox card = new VBox(18, children);
        card.getStyleClass().add("modal-card");
        card.setPrefWidth(520);
        card.setMaxWidth(520);
        return card;
    }

    private static HBox header(String icon, String tone, String title, String subtitle) {
        StackPane badge = new StackPane(Icons.large(icon));
        badge.getStyleClass().addAll("modal-badge", tone);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("modal-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("modal-subtitle");
        subtitleLabel.setWrapText(true);
        HBox header = new HBox(14, badge, new VBox(3, titleLabel, subtitleLabel));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private static VBox field(String name, Node content) {
        Label label = new Label(name.toUpperCase());
        label.getStyleClass().add("field-label");
        return new VBox(6, label, content);
    }

    private static HBox footer(Button... buttons) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(10, spacer);
        footer.getChildren().addAll(buttons);
        footer.setAlignment(Pos.CENTER_RIGHT);
        return footer;
    }

    private static Button button(String text, String styleClass, String icon) {
        Button button = new Button(text);
        button.getStyleClass().addAll("btn", styleClass);
        if (icon != null) {
            button.setGraphic(Icons.of(icon));
        }
        return button;
    }
}
