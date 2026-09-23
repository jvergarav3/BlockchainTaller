package blockchain.core.chain.presentation.javafx.components;

import blockchain.core.chain.application.dtos.BlockInfo;
import javafx.animation.PauseTransition;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class ChainView extends Pane {

    private static final double ROOM = 64;
    private static final double TURN = 30;
    private static final double GAP = 54;
    private static final double LINK_LENGTH = 20;
    private static final double NULL_TEXT_WIDTH = 27;
    private static final double CARD_HEIGHT =
            BlockCard.PILL_ROW_HEIGHT + BlockCard.NODE_HEIGHT + BlockCard.CAPTION_HEIGHT;
    private static final double ROW_STRIDE = CARD_HEIGHT + 30;
    private static final double CENTER_Y = BlockCard.PILL_ROW_HEIGHT + BlockCard.NODE_HEIGHT / 2;

    private static final double WAVE_STEP_MS = 110;
    private static final double WAVE_HOLD_MS = 650;
    private static final double FRESH_HOLD_MS = 1800;

    private final Map<Integer, BlockCard> cards = new HashMap<>();
    private final Map<Integer, LinkArrow> arrowsIn = new HashMap<>();
    private final List<BlockCard> orderedCards = new ArrayList<>();
    private final List<LinkArrow> orderedArrows = new ArrayList<>();
    private final Button sampleButton = new Button();
    private final VBox emptyState = buildEmptyState();
    private List<BlockInfo> blocks = List.of();

    public ChainView() {
        getStyleClass().add("chain-view");
        setMaxHeight(Region.USE_PREF_SIZE);
    }

    public void render(List<BlockInfo> blocks, Set<Integer> annulledIds, Set<Integer> correctedIds,
                       Consumer<BlockInfo> onSelect, Integer freshId) {
        this.blocks = blocks;
        cards.clear();
        arrowsIn.clear();
        orderedCards.clear();
        orderedArrows.clear();
        getChildren().clear();

        if (blocks.isEmpty()) {
            getChildren().add(emptyState);
            return;
        }

        int columns = columnsFor(getWidth());
        LinkArrow previousArrow = null;
        for (int i = 0; i < blocks.size(); i++) {
            BlockInfo block = blocks.get(i);
            boolean last = i == blocks.size() - 1;
            BlockCard card = new BlockCard(block, i == 0, last,
                    annulledIds.contains(block.id()), correctedIds.contains(block.id()), isLeftward(i, columns));
            card.setOnMouseClicked(event -> {
                onSelect.accept(block);
                event.consume();
            });
            cards.put(block.id(), card);
            orderedCards.add(card);
            LinkArrow arrow = new LinkArrow(last);
            if (i > 0) {
                arrowsIn.put(block.id(), previousArrow);
            }
            previousArrow = arrow;
            orderedArrows.add(arrow);
        }
        getChildren().addAll(orderedArrows);
        getChildren().addAll(orderedCards);
        layoutCards();

        if (freshId != null && cards.containsKey(freshId)) {
            showFresh(freshId);
        }
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected double computePrefWidth(double height) {
        return 2 * ROOM + BlockCard.NODE_WIDTH;
    }

    @Override
    protected double computePrefHeight(double width) {
        if (blocks.isEmpty()) {
            return emptyState.prefHeight(-1);
        }
        int rows = rowsFor(columnsFor(width < 0 ? getWidth() : width));
        return (rows - 1) * ROW_STRIDE + CARD_HEIGHT;
    }

    @Override
    protected void layoutChildren() {
        if (blocks.isEmpty()) {
            emptyState.autosize();
            emptyState.relocate(Math.max(0, (getWidth() - emptyState.getWidth()) / 2), 0);
            return;
        }
        layoutCards();
    }

    private void layoutCards() {
        int columns = columnsFor(getWidth());
        int count = orderedCards.size();
        for (int i = 0; i < count; i++) {
            BlockCard card = orderedCards.get(i);
            card.setLeftward(isLeftward(i, columns));
            card.autosize();
            card.relocate(xOf(i, columns), rowOf(i, columns) * ROW_STRIDE);
        }
        for (int i = 0; i < count; i++) {
            routeArrow(i, columns);
        }
    }

    private void routeArrow(int index, int columns) {
        LinkArrow arrow = orderedArrows.get(index);
        boolean leftward = isLeftward(index, columns);
        double dir = leftward ? -1 : 1;
        double x = xOf(index, columns);
        double y = rowOf(index, columns) * ROW_STRIDE + CENTER_Y;
        double exitX = leftward ? x : x + BlockCard.NODE_WIDTH;
        boolean last = index == orderedCards.size() - 1;

        if (last) {
            arrow.route(dir, exitX, y, exitX + dir * LINK_LENGTH, y);
            arrow.placeNullLabel(leftward
                    ? exitX - LINK_LENGTH - 6 - NULL_TEXT_WIDTH
                    : exitX + LINK_LENGTH + 6, y);
        } else if ((index + 1) % columns == 0) {
            double nextY = y + ROW_STRIDE;
            double entryX = leftward ? x : x + BlockCard.NODE_WIDTH;
            arrow.route(-dir, exitX, y, exitX + dir * TURN, y, exitX + dir * TURN, nextY, entryX, nextY);
        } else {
            double nextX = xOf(index + 1, columns);
            double entryX = leftward ? nextX + BlockCard.NODE_WIDTH : nextX;
            arrow.route(dir, exitX, y, entryX, y);
        }
    }

    private static int columnsFor(double width) {
        double usable = width - 2 * ROOM + GAP;
        return Math.max(1, (int) Math.floor(usable / (BlockCard.NODE_WIDTH + GAP)));
    }

    private int rowsFor(int columns) {
        return (blocks.size() + columns - 1) / columns;
    }

    private static int rowOf(int index, int columns) {
        return index / columns;
    }

    private static boolean isLeftward(int index, int columns) {
        return rowOf(index, columns) % 2 == 1;
    }

    private static double xOf(int index, int columns) {
        int column = index % columns;
        int visualColumn = isLeftward(index, columns) ? columns - 1 - column : column;
        return ROOM + visualColumn * (BlockCard.NODE_WIDTH + GAP);
    }

    public Optional<BlockCard> cardOf(int id) {
        return Optional.ofNullable(cards.get(id));
    }

    public void select(Integer id) {
        cards.forEach((cardId, card) -> card.setSelected(cardId.equals(id)));
    }

    public void highlight(Set<Integer> matchIds) {
        cards.forEach((id, card) -> {
            boolean match = matchIds != null && matchIds.contains(id);
            card.setMatch(match);
            card.setDimmed(matchIds != null && !match);
        });
    }

    public void playVerification(Integer brokenId, Runnable onDone) {
        for (int i = 0; i < blocks.size(); i++) {
            BlockInfo block = blocks.get(i);
            BlockCard card = cards.get(block.id());
            boolean broken = block.id() == (brokenId == null ? -1 : brokenId);
            PauseTransition wait = new PauseTransition(Duration.millis(i * WAVE_STEP_MS));
            wait.setOnFinished(event -> {
                if (broken) {
                    card.setBroken(true);
                } else {
                    card.setVerified(true);
                    PauseTransition hold = new PauseTransition(Duration.millis(WAVE_HOLD_MS));
                    hold.setOnFinished(done -> card.setVerified(false));
                    hold.play();
                }
            });
            wait.play();
            if (broken) {
                break;
            }
        }
        if (onDone != null) {
            PauseTransition end = new PauseTransition(Duration.millis(blocks.size() * WAVE_STEP_MS));
            end.setOnFinished(event -> onDone.run());
            end.play();
        }
    }

    private void showFresh(int id) {
        BlockCard card = cards.get(id);
        LinkArrow arrow = arrowsIn.get(id);
        card.setFresh(true);
        card.playEntrance();
        if (arrow != null) {
            arrow.setLinked(true);
        }
        PauseTransition hold = new PauseTransition(Duration.millis(FRESH_HOLD_MS));
        hold.setOnFinished(event -> {
            card.setFresh(false);
            if (arrow != null) {
                arrow.setLinked(false);
            }
        });
        hold.play();
    }

    public void setOnLoadSample(Runnable action) {
        sampleButton.setOnAction(event -> action.run());
    }

    private VBox buildEmptyState() {
        StackPane badge = new StackPane(Icons.large("layers"));
        badge.getStyleClass().add("placeholder-badge");
        Label title = new Label("Tu cadena está vacía");
        title.getStyleClass().add("empty-title");
        Label hint = new Label("Escribe los datos de una transacción arriba y agrega el primer bloque (head y tail "
                + "apuntarán a él). También puedes empezar con unos bloques de ejemplo.");
        hint.getStyleClass().add("placeholder");
        hint.setWrapText(true);
        hint.setMaxWidth(420);
        hint.setTextAlignment(TextAlignment.CENTER);

        Label head = new Label("head = null");
        head.getStyleClass().addAll("pill", "head-tag-pill");
        Label tail = new Label("tail = null");
        tail.getStyleClass().addAll("pill", "tail-tag-pill");
        HBox pills = new HBox(8, head, tail);
        pills.setAlignment(Pos.CENTER);

        sampleButton.setText("Cargar cadena de ejemplo");
        sampleButton.getStyleClass().addAll("btn", "btn-secondary");
        sampleButton.setGraphic(Icons.of("plus"));

        VBox box = new VBox(14, badge, title, hint, pills, sampleButton);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("empty-state");
        return box;
    }
}
