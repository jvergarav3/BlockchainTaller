package blockchain.core.chain.presentation.cli.controllers;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksCommand;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockCommand;
import blockchain.core.chain.application.search_block.dtos.SearchBlockCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainCommand;
import blockchain.core.chain.domain.enums.BlockSearchCriteria;
import blockchain.core.chain.domain.exceptions.BlockAlreadyAnnulledException;
import blockchain.core.chain.domain.exceptions.BlockNotFoundException;
import blockchain.core.chain.domain.exceptions.InvalidBlockDataException;
import blockchain.core.chain.infrastructure.config.BlockchainConfiguration;
import blockchain.core.chain.presentation.UserMessages;
import blockchain.core.chain.presentation.cli.demo.TallerDemo;
import blockchain.core.chain.presentation.cli.view.ConsoleFormatter;

import java.io.PrintStream;
import java.util.Scanner;

public class BlockchainConsoleController {

    private final BlockchainConfiguration config;
    private final Scanner in;
    private final PrintStream out;

    public BlockchainConsoleController(BlockchainConfiguration config, Scanner in, PrintStream out) {
        this.config = config;
        this.in = in;
        this.out = out;
    }

    public void run() {
        while (true) {
            out.println(ConsoleFormatter.menu());
            String option = prompt("Opción: ");
            if (option == null || option.equals("0")) {
                out.println("Hasta pronto.");
                return;
            }
            try {
                handle(option);
            } catch (InvalidBlockDataException | BlockNotFoundException | BlockAlreadyAnnulledException e) {
                out.println("Error: " + UserMessages.of(e));
            }
        }
    }

    private void handle(String option) {
        switch (option) {
            case "1" -> addBlock();
            case "2" -> listBlocks();
            case "3" -> searchBlock();
            case "4" -> rectifyBlock();
            case "5" -> annulBlock();
            case "6" -> validateChain();
            case "7" -> TallerDemo.run(config, out);
            default -> out.println("Opción no válida.");
        }
    }

    private void addBlock() {
        String data = prompt("Datos del bloque: ");
        var response = config.addBlockUseCase().execute(new AddBlockCommand(data));
        out.println("Bloque agregado:\n" + ConsoleFormatter.block(response.block()));
    }

    private void listBlocks() {
        var response = config.listBlocksUseCase().execute(new ListBlocksCommand());
        out.println(ConsoleFormatter.blocks(response.blocks()));
    }

    private void searchBlock() {
        String choice = prompt("Buscar por 1) Hash  2) ID  3) Texto: ");
        BlockSearchCriteria criteria = switch (choice == null ? "" : choice) {
            case "1" -> BlockSearchCriteria.HASH;
            case "2" -> BlockSearchCriteria.ID;
            case "3" -> BlockSearchCriteria.TEXT;
            default -> null;
        };
        if (criteria == null) {
            out.println("Criterio no válido.");
            return;
        }
        String value = prompt("Valor: ");
        var response = config.searchBlockUseCase().execute(new SearchBlockCommand(criteria, value == null ? "" : value));
        out.println(response.matches().isEmpty() ? "Sin coincidencias." : ConsoleFormatter.blocks(response.matches()));
    }

    private void rectifyBlock() {
        Integer id = promptId();
        if (id == null) {
            return;
        }
        String data = prompt("Nuevos datos: ");
        var response = config.rectifyBlockUseCase().execute(new RectifyBlockCommand(id, data));
        out.println("Bloque de corrección agregado:\n" + ConsoleFormatter.block(response.correctionBlock()));
    }

    private void annulBlock() {
        Integer id = promptId();
        if (id == null) {
            return;
        }
        var response = config.annulBlockUseCase().execute(new AnnulBlockCommand(id));
        out.println("Bloque de anulación agregado:\n" + ConsoleFormatter.block(response.annulmentBlock()));
    }

    private void validateChain() {
        var response = config.validateChainUseCase().execute(new ValidateChainCommand());
        out.println(ConsoleFormatter.validation(response));
    }

    private Integer promptId() {
        String text = prompt("ID del bloque: ");
        try {
            return Integer.parseInt(text == null ? "" : text.trim());
        } catch (NumberFormatException e) {
            out.println("El ID debe ser un número.");
            return null;
        }
    }

    private String prompt(String message) {
        out.print(message);
        out.flush();
        return in.hasNextLine() ? in.nextLine() : null;
    }
}
