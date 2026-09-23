package blockchain.core.chain.presentation.javafx.controllers;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksCommand;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksResponse;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockCommand;
import blockchain.core.chain.application.search_block.dtos.SearchBlockCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainResponse;
import blockchain.core.chain.domain.enums.BlockSearchCriteria;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.exceptions.BlockAlreadyAnnulledException;
import blockchain.core.chain.domain.exceptions.BlockNotFoundException;
import blockchain.core.chain.domain.exceptions.InvalidBlockDataException;
import blockchain.core.chain.infrastructure.config.BlockchainConfiguration;
import blockchain.core.chain.presentation.UserMessages;
import blockchain.core.chain.presentation.javafx.components.BlockCard;
import blockchain.core.chain.presentation.javafx.components.BlockDetailsPanel;
import blockchain.core.chain.presentation.javafx.components.BlockDetailsPanel.Link;
import blockchain.core.chain.presentation.javafx.components.BlockDetailsPanel.StateKind;
import blockchain.core.chain.presentation.javafx.components.BlockDialogs;
import blockchain.core.chain.presentation.javafx.components.ChainView;
import blockchain.core.chain.presentation.javafx.components.ToastHost;
import blockchain.core.chain.presentation.javafx.components.ToastHost.Kind;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class ChainController {

    private enum Integrity { UNKNOWN, VALID, BROKEN }

    private static final List<String> SAMPLE_DATA = List.of(
            "Alice paga 10 a Bob", "Bob paga 5 a Carol", "Carol paga 2 a Dave");

    private final BlockchainConfiguration config;
    private final ChainView chainView = new ChainView();
    private final BlockDetailsPanel detailsPanel = new BlockDetailsPanel();

    @FXML private TextField dataField;
    @FXML private TextField searchField;
    @FXML private Button searchClearButton;
    @FXML private Label searchInfo;
    @FXML private ToggleGroup criteriaGroup;
    @FXML private ToggleButton criteriaText;
    @FXML private ToggleButton criteriaId;
    @FXML private ToggleButton criteriaHash;
    @FXML private Label totalValue;
    @FXML private Label normalValue;
    @FXML private Label correctionValue;
    @FXML private Label annulmentValue;
    @FXML private Label integrityValue;
    @FXML private Label integritySub;
    @FXML private Region integrityDot;
    @FXML private Label sizeLabel;
    @FXML private Button validateButton;
    @FXML private VBox canvasCard;
    @FXML private ScrollPane chainScroll;
    @FXML private StackPane chainHolder;
    @FXML private StackPane detailsHolder;
    @FXML private ToastHost toastHost;

    private List<BlockInfo> blocks = List.of();
    private final Map<Integer, Integer> annulledBy = new HashMap<>();
    private final Map<Integer, List<Integer>> correctedBy = new HashMap<>();
    private Integer selectedId;
    private Set<Integer> matchIds;
    private Integrity integrity = Integrity.UNKNOWN;
    private ValidateChainResponse lastValidation;

    public ChainController(BlockchainConfiguration config) {
        this.config = config;
    }

    @FXML
    private void initialize() {
        chainHolder.getChildren().add(chainView);
        chainHolder.setOnMouseClicked(event -> deselect());
        chainScroll.viewportBoundsProperty().addListener(
                (observable, oldBounds, bounds) -> chainHolder.setMinHeight(bounds.getHeight()));
        detailsHolder.getChildren().add(detailsPanel);
        chainView.setOnLoadSample(this::loadSample);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(28);
        clip.setArcHeight(28);
        clip.widthProperty().bind(canvasCard.widthProperty());
        clip.heightProperty().bind(canvasCard.heightProperty());
        canvasCard.setClip(clip);

        criteriaText.setUserData(BlockSearchCriteria.TEXT);
        criteriaId.setUserData(BlockSearchCriteria.ID);
        criteriaHash.setUserData(BlockSearchCriteria.HASH);
        criteriaGroup.selectedToggleProperty().addListener((observable, previous, current) -> {
            if (current == null) {
                previous.setSelected(true);
            } else {
                applySearch();
            }
        });
        searchField.textProperty().addListener((observable, previous, current) -> applySearch());
        searchClearButton.visibleProperty().bind(searchField.textProperty().isNotEmpty());
        searchClearButton.managedProperty().bind(searchField.textProperty().isNotEmpty());

        chainScroll.sceneProperty().addListener((observable, previous, scene) -> {
            if (scene != null) {
                installShortcuts(scene);
            }
        });

        refresh(null);
    }

    @FXML
    private void onAddBlock() {
        try {
            var response = config.addBlockUseCase().execute(new AddBlockCommand(dataField.getText()));
            dataField.clear();
            integrity = Integrity.UNKNOWN;
            BlockInfo block = response.block();
            refresh(block.id());
            scrollToEnd();
            toast("Bloque #" + block.id() + " agregado a la cadena.", Kind.SUCCESS);
            dataField.requestFocus();
        } catch (InvalidBlockDataException e) {
            toast(UserMessages.of(e), Kind.ERROR);
            shake(dataField);
        }
    }

    @FXML
    private void onSearch() {
        applySearch();
        if (matchIds != null && !matchIds.isEmpty()) {
            BlockInfo first = blocks.stream().filter(block -> matchIds.contains(block.id())).findFirst().orElseThrow();
            select(first);
            chainView.cardOf(first.id()).ifPresent(this::scrollTo);
        }
    }

    @FXML
    private void onClearSearch() {
        searchField.clear();
        searchField.requestFocus();
    }

    @FXML
    private void onValidate() {
        ValidateChainResponse response = config.validateChainUseCase().execute(new ValidateChainCommand());
        if (blocks.isEmpty()) {
            toast("La cadena está vacía; no hay nada que validar.", Kind.INFO);
            return;
        }
        lastValidation = response;
        integrity = response.valid() ? Integrity.VALID : Integrity.BROKEN;
        updateIntegrity();
        validateButton.setDisable(true);
        chainView.playVerification(response.valid() ? null : response.firstInvalidBlockId(),
                () -> validateButton.setDisable(false));
        if (response.valid()) {
            toast("Cadena íntegra: " + response.blocksChecked() + " bloques verificados (hash y enlace).", Kind.SUCCESS);
        } else {
            toast("Cadena rota en el bloque #" + response.firstInvalidBlockId() + ".", Kind.ERROR);
        }
    }

    private void onRectify() {
        BlockInfo block = selectedBlock();
        if (block == null) {
            return;
        }
        BlockDialogs.rectify(chainScroll.getScene().getWindow(), block).ifPresent(newData -> {
            try {
                var response = config.rectifyBlockUseCase().execute(new RectifyBlockCommand(block.id(), newData));
                integrity = Integrity.UNKNOWN;
                refresh(response.correctionBlock().id());
                scrollToEnd();
                toast("Corrección #" + response.correctionBlock().id() + " registrada para el bloque #"
                        + block.id() + ".", Kind.SUCCESS);
            } catch (InvalidBlockDataException | BlockNotFoundException e) {
                toast(UserMessages.of(e), Kind.ERROR);
            }
        });
    }

    private void onAnnul() {
        BlockInfo block = selectedBlock();
        if (block == null || !BlockDialogs.confirmAnnul(chainScroll.getScene().getWindow(), block)) {
            return;
        }
        try {
            var response = config.annulBlockUseCase().execute(new AnnulBlockCommand(block.id()));
            integrity = Integrity.UNKNOWN;
            refresh(response.annulmentBlock().id());
            scrollToEnd();
            toast("Bloque #" + response.annulmentBlock().id() + " agregado: anula el #" + block.id() + ".",
                    Kind.SUCCESS);
        } catch (BlockNotFoundException | BlockAlreadyAnnulledException e) {
            toast(UserMessages.of(e), Kind.ERROR);
        }
    }

    private void loadSample() {
        Integer last = null;
        for (String data : SAMPLE_DATA) {
            last = config.addBlockUseCase().execute(new AddBlockCommand(data)).block().id();
        }
        integrity = Integrity.UNKNOWN;
        refresh(last);
        toast("Cadena de ejemplo cargada.", Kind.INFO);
    }

    private void refresh(Integer freshId) {
        ListBlocksResponse response = config.listBlocksUseCase().execute(new ListBlocksCommand());
        blocks = response.blocks();
        indexReferences();
        chainView.render(blocks, annulledBy.keySet(), correctedBy.keySet(), this::onBlockClicked, freshId);
        updateStats(response.total());
        applySearch();
        if (selectedBlock() != null) {
            chainView.select(selectedId);
            showDetails(selectedBlock());
        } else {
            selectedId = null;
            detailsPanel.showPlaceholder();
        }
    }

    private void updateStats(int total) {
        sizeLabel.setText("size = " + total);
        totalValue.setText(String.valueOf(total));
        normalValue.setText(String.valueOf(count(BlockType.NORMAL)));
        correctionValue.setText(String.valueOf(count(BlockType.CORRECTION)));
        annulmentValue.setText(String.valueOf(count(BlockType.ANNULMENT)));
        updateIntegrity();
    }

    private long count(BlockType type) {
        return blocks.stream().filter(block -> block.type() == type).count();
    }

    private void updateIntegrity() {
        integrityDot.getStyleClass().removeAll("dot-ok", "dot-bad", "dot-idle");
        if (blocks.isEmpty()) {
            integrityValue.setText("Vacía");
            integritySub.setText("sin bloques");
            integrityDot.getStyleClass().add("dot-idle");
            return;
        }
        switch (integrity) {
            case VALID -> {
                integrityValue.setText("Íntegra");
                integritySub.setText(lastValidation.blocksChecked() + " bloques verificados");
                integrityDot.getStyleClass().add("dot-ok");
            }
            case BROKEN -> {
                integrityValue.setText("Rota");
                integritySub.setText("falla en el bloque #" + lastValidation.firstInvalidBlockId());
                integrityDot.getStyleClass().add("dot-bad");
            }
            default -> {
                integrityValue.setText("Sin validar");
                integritySub.setText("pulsa «Validar cadena»");
                integrityDot.getStyleClass().add("dot-idle");
            }
        }
    }

    private void applySearch() {
        String value = searchField == null ? null : searchField.getText();
        searchInfo.getStyleClass().removeAll("search-ok", "search-none");
        if (value == null || value.isBlank()) {
            matchIds = null;
            searchInfo.setText("");
            chainView.highlight(null);
            return;
        }
        BlockSearchCriteria criteria = (BlockSearchCriteria) criteriaGroup.getSelectedToggle().getUserData();
        var response = config.searchBlockUseCase().execute(new SearchBlockCommand(criteria, value));
        matchIds = response.matches().stream().map(BlockInfo::id).collect(Collectors.toSet());
        chainView.highlight(matchIds);
        int found = matchIds.size();
        searchInfo.setText(found == 0 ? "Sin resultados" : found + (found == 1 ? " resultado" : " resultados"));
        searchInfo.getStyleClass().add(found == 0 ? "search-none" : "search-ok");
    }

    private void indexReferences() {
        annulledBy.clear();
        correctedBy.clear();
        for (BlockInfo block : blocks) {
            if (block.type() == BlockType.ANNULMENT) {
                annulledBy.put(block.referencedBlockId(), block.id());
            } else if (block.type() == BlockType.CORRECTION) {
                correctedBy.computeIfAbsent(block.referencedBlockId(), key -> new ArrayList<>()).add(block.id());
            }
        }
    }

    private void onBlockClicked(BlockInfo block) {
        if (selectedId != null && block.id() == selectedId) {
            deselect();
        } else {
            select(block);
        }
    }

    private void select(BlockInfo block) {
        selectedId = block.id();
        chainView.select(selectedId);
        showDetails(block);
    }

    private void deselect() {
        selectedId = null;
        chainView.select(null);
        detailsPanel.showPlaceholder();
    }

    private void goTo(int blockId) {
        blocks.stream().filter(block -> block.id() == blockId).findFirst().ifPresent(block -> {
            select(block);
            chainView.cardOf(blockId).ifPresent(this::ensureVisible);
        });
    }

    private void step(int delta) {
        if (blocks.isEmpty()) {
            return;
        }
        int index = selectedId == null ? (delta > 0 ? -1 : blocks.size()) : indexOf(selectedId);
        int target = Math.max(0, Math.min(blocks.size() - 1, index + delta));
        goTo(blocks.get(target).id());
    }

    private int indexOf(int blockId) {
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).id() == blockId) {
                return i;
            }
        }
        return -1;
    }

    private void showDetails(BlockInfo block) {
        detailsPanel.show(viewOf(block), new BlockDetailsPanel.Actions() {
            @Override
            public void rectify() {
                onRectify();
            }

            @Override
            public void annul() {
                onAnnul();
            }

            @Override
            public void close() {
                deselect();
            }

            @Override
            public void goTo(int blockId) {
                ChainController.this.goTo(blockId);
            }

            @Override
            public void previous() {
                step(-1);
            }

            @Override
            public void next() {
                step(1);
            }

            @Override
            public void copied(String what) {
                toast(what + " copiado al portapapeles.", Kind.INFO);
            }
        });
    }

    private BlockDetailsPanel.View viewOf(BlockInfo block) {
        boolean annulment = block.type() == BlockType.ANNULMENT;
        boolean annulled = annulledBy.containsKey(block.id());
        List<Integer> corrections = correctedBy.getOrDefault(block.id(), List.of());

        String state;
        StateKind kind;
        if (annulment) {
            state = "Bloque de anulación";
            kind = StateKind.ANNULMENT;
        } else if (annulled) {
            state = "Anulado";
            kind = StateKind.ANNULLED;
        } else if (!corrections.isEmpty()) {
            state = "Rectificado";
            kind = StateKind.CORRECTED;
        } else {
            state = "Vigente";
            kind = StateKind.ACTIVE;
        }

        List<Link> links = new ArrayList<>();
        if (block.referencedBlockId() != null) {
            String verb = annulment ? "Anula el bloque #" : "Corrige el bloque #";
            links.add(new Link(verb + block.referencedBlockId(), block.referencedBlockId()));
        }
        if (annulled) {
            links.add(new Link("Anulado por #" + annulledBy.get(block.id()), annulledBy.get(block.id())));
        }
        for (Integer correction : corrections) {
            links.add(new Link("Rectificado por #" + correction, correction));
        }

        return new BlockDetailsPanel.View(block, indexOf(block.id()) + 1, blocks.size(), state, kind,
                !annulment && !annulled, links);
    }

    private BlockInfo selectedBlock() {
        if (selectedId == null) {
            return null;
        }
        return blocks.stream().filter(block -> block.id() == selectedId).findFirst().orElse(null);
    }

    private void installShortcuts(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            boolean typing = scene.getFocusOwner() instanceof TextInputControl;
            if (event.getCode() == KeyCode.ESCAPE) {
                if (typing) {
                    chainScroll.requestFocus();
                } else {
                    deselect();
                }
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.F) {
                searchField.requestFocus();
                searchField.selectAll();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.N) {
                dataField.requestFocus();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.L) {
                if (!validateButton.isDisabled()) {
                    onValidate();
                }
                event.consume();
            } else if (!typing && (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.UP)) {
                step(-1);
                event.consume();
            } else if (!typing && (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.DOWN)) {
                step(1);
                event.consume();
            }
        });
    }

    private void scrollToEnd() {
        Platform.runLater(() -> {
            chainScroll.applyCss();
            chainScroll.layout();
            animateScroll(1.0);
        });
    }

    private void scrollTo(BlockCard card) {
        Platform.runLater(() -> {
            chainScroll.applyCss();
            chainScroll.layout();
            double target = targetFor(card);
            if (target >= 0) {
                animateScroll(target);
            }
        });
    }

    private void ensureVisible(BlockCard card) {
        Platform.runLater(() -> {
            chainScroll.applyCss();
            chainScroll.layout();
            double contentHeight = chainHolder.getHeight();
            double viewHeight = chainScroll.getViewportBounds().getHeight();
            if (contentHeight <= viewHeight) {
                return;
            }
            Bounds bounds = chainHolder.sceneToLocal(card.localToScene(card.getBoundsInLocal()));
            double top = chainScroll.getVvalue() * (contentHeight - viewHeight);
            if (bounds.getMinY() < top + 16 || bounds.getMaxY() > top + viewHeight - 16) {
                animateScroll(targetFor(card));
            }
        });
    }

    private double targetFor(BlockCard card) {
        double contentHeight = chainHolder.getHeight();
        double viewHeight = chainScroll.getViewportBounds().getHeight();
        if (contentHeight <= viewHeight) {
            return -1;
        }
        Bounds bounds = chainHolder.sceneToLocal(card.localToScene(card.getBoundsInLocal()));
        double target = (bounds.getCenterY() - viewHeight / 2) / (contentHeight - viewHeight);
        return Math.max(0, Math.min(1, target));
    }

    private void animateScroll(double target) {
        new Timeline(new KeyFrame(Duration.millis(320),
                new KeyValue(chainScroll.vvalueProperty(), target, Interpolator.EASE_BOTH))).play();
    }

    private void shake(Node node) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(60), new KeyValue(node.translateXProperty(), -7)),
                new KeyFrame(Duration.millis(120), new KeyValue(node.translateXProperty(), 7)),
                new KeyFrame(Duration.millis(180), new KeyValue(node.translateXProperty(), -4)),
                new KeyFrame(Duration.millis(240), new KeyValue(node.translateXProperty(), 0)));
        timeline.play();
    }

    private void toast(String message, Kind kind) {
        toastHost.show(message, kind);
    }
}
