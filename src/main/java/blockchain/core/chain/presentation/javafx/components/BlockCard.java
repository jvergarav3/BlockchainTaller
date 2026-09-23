package blockchain.core.chain.presentation.javafx.components;

import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.presentation.javafx.Fonts;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Locale;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class BlockCard extends VBox {

    public static final double NODE_WIDTH = 92;
    public static final double NODE_HEIGHT = 46;
    public static final double PILL_ROW_HEIGHT = 40;
    public static final double CAPTION_HEIGHT = 18;

    private final BlockInfo info;
    private final StackPane idCell = new StackPane();
    private final Region divider = new Region();
    private final Region pointerStem = new Region();
    private final HBox pointerCell = new HBox();
    private Circle pointerDot;
    private final HBox node;
    private boolean leftward;

    public BlockCard(BlockInfo info, boolean head, boolean tail, boolean annulled, boolean corrected,
                     boolean leftward) {
        this.info = info;
        getStyleClass().addAll("block-card", "type-" + info.type().name().toLowerCase(Locale.ROOT));
        if (annulled) {
            getStyleClass().add("annulled");
        }
        setAlignment(Pos.TOP_CENTER);
        setMinWidth(NODE_WIDTH);
        setPrefWidth(NODE_WIDTH);
        setMaxWidth(NODE_WIDTH);
        node = buildNode(info);
        setLeftward(leftward);
        getChildren().addAll(pillsRow(head, tail), node, caption(info, annulled, corrected));
    }

    public BlockInfo info() {
        return info;
    }

    public void setSelected(boolean on) {
        setFlag("selected", on);
    }

    public void setDimmed(boolean on) {
        setFlag("dimmed", on);
    }

    public void setMatch(boolean on) {
        setFlag("match", on);
    }

    public void setFresh(boolean on) {
        setFlag("fresh", on);
    }

    public void setVerified(boolean on) {
        setFlag("verified", on);
    }

    public void setBroken(boolean on) {
        setFlag("broken", on);
    }

    public void playEntrance() {
        FadeTransition fade = new FadeTransition(Duration.millis(380), this);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(420), this);
        slide.setFromY(38);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(fade, slide).play();
    }

    private void setFlag(String styleClass, boolean on) {
        getStyleClass().remove(styleClass);
        if (on) {
            getStyleClass().add(styleClass);
        }
    }

    private static HBox pillsRow(boolean head, boolean tail) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.BOTTOM_CENTER);
        row.setMinHeight(PILL_ROW_HEIGHT);
        row.setPrefHeight(PILL_ROW_HEIGHT);
        row.setMaxHeight(PILL_ROW_HEIGHT);
        if (head) {
            row.getChildren().add(tag("head", "head-tag"));
        }
        if (tail) {
            row.getChildren().add(tag("tail", "tail-tag"));
        }
        return row;
    }

    private static VBox tag(String text, String styleClass) {
        Label pill = new Label(text);
        pill.getStyleClass().addAll("pill", styleClass + "-pill");

        Region stem = new Region();
        stem.getStyleClass().add("tag-stem");
        stem.setMinSize(1.6, 7);
        stem.setPrefSize(1.6, 7);
        stem.setMaxSize(1.6, 7);

        Region arrow = new Region();
        arrow.getStyleClass().add("tag-arrow");
        arrow.setMinSize(9, 6);
        arrow.setPrefSize(9, 6);
        arrow.setMaxSize(9, 6);

        VBox tag = new VBox(pill, stem, arrow);
        tag.getStyleClass().add(styleClass);
        tag.setAlignment(Pos.TOP_CENTER);
        return tag;
    }

    private HBox buildNode(BlockInfo info) {
        Label id = new Label("#" + info.id());
        id.getStyleClass().add("block-id");
        Fonts.applyMono(id);
        idCell.getChildren().add(id);
        idCell.setPrefWidth(56);
        idCell.setMinWidth(56);

        divider.getStyleClass().add("block-divider");
        divider.setMinWidth(1.4);
        divider.setPrefWidth(1.4);
        divider.setMaxWidth(1.4);

        Circle dot = new Circle(3.5);
        dot.getStyleClass().add("block-dot");
        pointerDot = dot;
        pointerStem.getStyleClass().add("block-stem");
        pointerStem.setMinHeight(1.6);
        pointerStem.setPrefHeight(1.6);
        pointerStem.setMaxHeight(1.6);
        HBox.setHgrow(pointerStem, Priority.ALWAYS);
        HBox.setHgrow(pointerCell, Priority.ALWAYS);

        HBox node = new HBox();
        node.getStyleClass().add("block-node");
        node.setAlignment(Pos.CENTER_LEFT);
        node.setMinSize(NODE_WIDTH, NODE_HEIGHT);
        node.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        node.setMaxSize(NODE_WIDTH, NODE_HEIGHT);
        return node;
    }

    public void setLeftward(boolean leftward) {
        if (leftward == this.leftward && !node.getChildren().isEmpty()) {
            return;
        }
        this.leftward = leftward;
        if (leftward) {
            pointerCell.getChildren().setAll(pointerStem, pointerDot);
            pointerCell.setAlignment(Pos.CENTER_RIGHT);
            pointerCell.setPadding(new Insets(0, 9, 0, 0));
            node.getChildren().setAll(pointerCell, divider, idCell);
        } else {
            pointerCell.getChildren().setAll(pointerDot, pointerStem);
            pointerCell.setAlignment(Pos.CENTER_LEFT);
            pointerCell.setPadding(new Insets(0, 0, 0, 9));
            node.getChildren().setAll(idCell, divider, pointerCell);
        }
    }

    private static Label caption(BlockInfo info, boolean annulled, boolean corrected) {
        String text = "";
        if (info.type() == BlockType.CORRECTION) {
            text = "corrige #" + info.referencedBlockId();
        } else if (info.type() == BlockType.ANNULMENT) {
            text = "anula #" + info.referencedBlockId();
        } else if (annulled) {
            text = "anulado";
        } else if (corrected) {
            text = "rectificado";
        }
        Label caption = new Label(text);
        caption.getStyleClass().add("block-caption");
        caption.setMinHeight(CAPTION_HEIGHT);
        caption.setPrefHeight(CAPTION_HEIGHT);
        return caption;
    }
}
