package blockchain.core.chain.presentation.javafx.components;

import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.presentation.UserMessages;
import blockchain.core.chain.presentation.javafx.Fonts;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Locale;

public class BlockDetailsPanel extends VBox {

    public enum StateKind {
        ACTIVE("state-active"),
        CORRECTED("state-corrected"),
        ANNULLED("state-annulled"),
        ANNULMENT("state-annulment");

        private final String styleClass;

        StateKind(String styleClass) {
            this.styleClass = styleClass;
        }
    }

    public record Link(String caption, int blockId) {
    }

    public record View(BlockInfo block, int position, int total, String state, StateKind stateKind,
                       boolean canAnnul, List<Link> links) {
    }

    public interface Actions {
        void rectify();

        void annul();

        void close();

        void goTo(int blockId);

        void previous();

        void next();

        void copied(String what);
    }

    public BlockDetailsPanel() {
        getStyleClass().add("details");
        setPrefWidth(350);
        setMinWidth(350);
        setMaxWidth(350);
        showPlaceholder();
    }

    public void showPlaceholder() {
        StackPane badge = new StackPane(Icons.large("layers"));
        badge.getStyleClass().add("placeholder-badge");
        Label title = new Label("Ningún bloque seleccionado");
        title.getStyleClass().add("placeholder-title");
        Label hint = new Label("Haz clic en un bloque de la cadena para ver sus datos, hashes y relaciones. "
                + "También puedes moverte con las flechas del teclado.");
        hint.getStyleClass().add("placeholder");
        hint.setWrapText(true);
        hint.setTextAlignment(TextAlignment.CENTER);

        VBox box = new VBox(14, badge, title, hint);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("placeholder-box");
        VBox.setVgrow(box, Priority.ALWAYS);
        getChildren().setAll(box);
    }

    public void show(View view, Actions actions) {
        BlockInfo block = view.block();

        Label title = new Label("Bloque #" + block.id());
        title.getStyleClass().add("details-title");
        Fonts.applyMono(title);
        Label badge = new Label(UserMessages.typeLabel(block.type()));
        badge.getStyleClass().addAll("badge", "badge-" + block.type().name().toLowerCase(Locale.ROOT));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button close = iconButton("close", "Cerrar (Esc)");
        close.setOnAction(event -> actions.close());
        HBox header = new HBox(10, title, badge, spacer, close);
        header.setAlignment(Pos.CENTER_LEFT);

        FlowPane chips = new FlowPane(6, 6);
        chips.getChildren().add(chip("Posición " + view.position() + " de " + view.total(), "chip"));
        if (view.position() == 1) {
            chips.getChildren().add(chip("head", "chip-head"));
        }
        if (view.position() == view.total()) {
            chips.getChildren().add(chip("tail", "chip-tail"));
        }

        Button previous = navButton("Anterior", "chevron-left", true);
        previous.setDisable(view.position() == 1);
        previous.setOnAction(event -> actions.previous());
        Button next = navButton("Siguiente", "chevron-right", false);
        next.setDisable(view.position() == view.total());
        next.setOnAction(event -> actions.next());
        HBox nav = new HBox(8, previous, next);

        VBox content = new VBox(18, header, chips, nav,
                section("Información",
                        field("Datos", valueLabel(block.data(), false)),
                        field("Estado", stateNode(view))),
                section("Enlace criptográfico",
                        hashField("Hash anterior", block.previousHash(), actions),
                        linkIcon(),
                        hashField("Hash actual", block.hash(), actions)));
        content.getStyleClass().add("details-content");

        if (!view.links().isEmpty()) {
            FlowPane links = new FlowPane(8, 8);
            for (Link link : view.links()) {
                Button button = new Button(link.caption());
                button.getStyleClass().addAll("btn", "chip-link");
                button.setGraphic(Icons.of("link"));
                button.setOnAction(event -> actions.goTo(link.blockId()));
                links.getChildren().add(button);
            }
            content.getChildren().add(section("Relaciones", links));
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("details-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button rectify = new Button("Rectificar");
        rectify.getStyleClass().addAll("btn", "btn-primary");
        rectify.setGraphic(Icons.of("edit"));
        rectify.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(rectify, Priority.ALWAYS);
        rectify.setOnAction(event -> actions.rectify());
        Button annul = new Button("Anular");
        annul.getStyleClass().addAll("btn", "btn-danger");
        annul.setGraphic(Icons.of("block"));
        annul.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(annul, Priority.ALWAYS);
        annul.setDisable(!view.canAnnul());
        annul.setOnAction(event -> actions.annul());
        HBox footer = new HBox(10, rectify, annul);
        footer.getStyleClass().add("details-footer");

        getChildren().setAll(scroll, footer);
    }

    private static Node stateNode(View view) {
        Region dot = new Region();
        dot.getStyleClass().addAll("dot", view.stateKind().styleClass);
        Label label = new Label(view.state());
        label.getStyleClass().add("field-value");
        HBox box = new HBox(8, dot, label);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private static VBox section(String title, Node... children) {
        Label label = new Label(title);
        label.getStyleClass().add("section-title");
        VBox body = new VBox(12, children);
        body.getStyleClass().add("section-body");
        VBox section = new VBox(8, label, body);
        return section;
    }

    private static VBox field(String name, Node value) {
        Label label = new Label(name.toUpperCase(Locale.ROOT));
        label.getStyleClass().add("field-label");
        return new VBox(4, label, value);
    }

    private static Label valueLabel(String text, boolean mono) {
        Label value = new Label(text);
        value.getStyleClass().add("field-value");
        value.setWrapText(true);
        return mono ? Fonts.applyMono(value) : value;
    }

    private static VBox hashField(String name, String hash, Actions actions) {
        boolean missing = hash == null;
        Label value = valueLabel(missing ? "None" : hash, true);
        value.getStyleClass().add("hash-value");
        if (missing) {
            value.getStyleClass().add("hash-missing");
        }
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(8, value, spacer);
        row.setAlignment(Pos.CENTER_LEFT);
        if (!missing) {
            Button copy = iconButton("copy", "Copiar " + name.toLowerCase(Locale.ROOT));
            copy.setOnAction(event -> {
                ClipboardContent clip = new ClipboardContent();
                clip.putString(hash);
                Clipboard.getSystemClipboard().setContent(clip);
                actions.copied(name);
            });
            row.getChildren().add(copy);
        }
        return field(name, row);
    }

    private static Node linkIcon() {
        StackPane holder = new StackPane(Icons.of("arrow-down"));
        holder.getStyleClass().add("hash-link");
        holder.setAlignment(Pos.CENTER_LEFT);
        return holder;
    }

    private static Label chip(String text, String styleClass) {
        Label chip = new Label(text);
        chip.getStyleClass().addAll("chip", styleClass);
        return chip;
    }

    private static Button navButton(String text, String icon, boolean iconFirst) {
        Button button = new Button(text);
        button.getStyleClass().addAll("btn", "btn-secondary", "btn-small");
        button.setGraphic(Icons.of(icon));
        if (!iconFirst) {
            button.setContentDisplay(ContentDisplay.RIGHT);
        }
        button.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    private static Button iconButton(String icon, String tooltip) {
        Button button = new Button();
        button.getStyleClass().add("icon-btn");
        button.setGraphic(Icons.of(icon));
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }
}
