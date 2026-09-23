package blockchain.core.chain.presentation.javafx;

import blockchain.core.chain.infrastructure.config.BlockchainConfiguration;
import blockchain.core.chain.presentation.javafx.controllers.ChainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class FxApp extends Application {

    private static BlockchainConfiguration configuration;

    public static void launchApp(BlockchainConfiguration config, String... args) {
        configuration = config;
        Application.launch(FxApp.class, args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(FxApp.class.getResource("/fxml/chain-view.fxml")));
        loader.setControllerFactory(type -> new ChainController(configuration));
        Parent root = loader.load();
        root.setStyle("-fx-font-family: '" + Fonts.ui() + "';");

        Scene scene = new Scene(root, 1320, 800);
        scene.getStylesheets().add(Objects.requireNonNull(FxApp.class.getResource("/css/styles.css")).toExternalForm());

        stage.getIcons().add(appIcon());
        stage.setTitle("Blockchain · lista enlazada");
        stage.setMinWidth(1040);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    private static Image appIcon() {
        double size = 128;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#6d8dff")), new Stop(1, Color.web("#8b5cf6"))));
        g.fillRoundRect(4, 4, size - 8, size - 8, 30, 30);
        g.setStroke(Color.web("#ffffff", 0.9));
        g.setLineWidth(5);
        g.strokeLine(44, 64, 84, 64);
        g.setFill(Color.WHITE);
        g.fillRoundRect(20, 46, 34, 36, 9, 9);
        g.fillRoundRect(74, 46, 34, 36, 9, 9);
        g.setFill(Color.web("#4b63d8"));
        g.fillRoundRect(29, 58, 16, 12, 4, 4);
        g.fillRoundRect(83, 58, 16, 12, 4, 4);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        return canvas.snapshot(parameters, null);
    }
}
