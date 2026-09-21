package blockchain.core.chain.presentation.javafx;

import javafx.scene.Node;
import javafx.scene.text.Font;

import java.util.List;

public final class Fonts {

    private static final List<String> UI = List.of(
            "Inter", "SF Pro Text", "Segoe UI", "Roboto", "Ubuntu Sans", "Noto Sans", "DejaVu Sans");
    private static final List<String> MONO = List.of(
            "JetBrains Mono", "Cascadia Code", "SF Mono", "Consolas", "Ubuntu Sans Mono", "Noto Sans Mono",
            "DejaVu Sans Mono", "Liberation Mono");

    private static String ui;
    private static String mono;

    private Fonts() {
    }

    public static String ui() {
        if (ui == null) {
            ui = firstAvailable(UI, "System");
        }
        return ui;
    }

    public static String mono() {
        if (mono == null) {
            mono = firstAvailable(MONO, "Monospaced");
        }
        return mono;
    }

    public static <T extends Node> T applyMono(T node) {
        node.setStyle(node.getStyle() + "-fx-font-family: '" + mono() + "';");
        return node;
    }

    private static String firstAvailable(List<String> candidates, String fallback) {
        List<String> installed = Font.getFamilies();
        return candidates.stream().filter(installed::contains).findFirst().orElse(fallback);
    }
}
