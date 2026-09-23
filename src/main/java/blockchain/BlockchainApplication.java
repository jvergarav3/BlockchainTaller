package blockchain;

import blockchain.core.chain.infrastructure.config.BlockchainConfiguration;
import blockchain.core.chain.presentation.cli.controllers.BlockchainConsoleController;
import blockchain.core.chain.presentation.javafx.FxApp;

import java.util.Locale;
import java.util.Scanner;

/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class BlockchainApplication {

    private static final String USAGE = """
            Uso: BlockchainApplication [gui | cli | menu]
              gui   abre solo la interfaz gráfica (JavaFX)
              cli   abre solo la terminal
              menu  pregunta qué interfaz usar (por defecto)""";

    public static void main(String[] args) {
        BlockchainConfiguration configuration = new BlockchainConfiguration();
        String mode = args.length == 0 ? "menu" : args[0].toLowerCase(Locale.ROOT).replaceFirst("^-+", "");

        switch (mode) {
            case "gui", "fx", "javafx" -> FxApp.launchApp(configuration);
            case "cli", "terminal" -> runTerminal(configuration, new Scanner(System.in));
            case "menu" -> runMenu(configuration);
            default -> {
                System.err.println(USAGE);
                System.exit(1);
            }
        }
    }

    private static void runMenu(BlockchainConfiguration configuration) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("""

                    ===== Blockchain =====
                    1. Terminal
                    2. Interfaz grafica (JavaFX)
                    0. Salir""");
            System.out.print("Opcion: ");
            if (!scanner.hasNextLine()) {
                return;
            }
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    runTerminal(configuration, scanner);
                    return;
                }
                case "2" -> {
                    FxApp.launchApp(configuration);
                    return;
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opción no valida.");
            }
        }
    }

    private static void runTerminal(BlockchainConfiguration configuration, Scanner scanner) {
        new BlockchainConsoleController(configuration, scanner, System.out).run();
    }
}
