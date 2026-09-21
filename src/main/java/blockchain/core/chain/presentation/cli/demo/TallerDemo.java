package blockchain.core.chain.presentation.cli.demo;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksCommand;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockCommand;
import blockchain.core.chain.application.search_block.dtos.SearchBlockCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainCommand;
import blockchain.core.chain.domain.enums.BlockSearchCriteria;
import blockchain.core.chain.infrastructure.config.BlockchainConfiguration;
import blockchain.core.chain.presentation.cli.view.ConsoleFormatter;

import java.io.PrintStream;

public final class TallerDemo {

    private TallerDemo() {
    }

    public static void run(BlockchainConfiguration config, PrintStream out) {
        out.println("--- Agregando 3 bloques ---");
        config.addBlockUseCase().execute(new AddBlockCommand("Alice paga 10 a Bob"));
        BlockInfo second = config.addBlockUseCase().execute(new AddBlockCommand("Bob paga 5 a Carol")).block();
        config.addBlockUseCase().execute(new AddBlockCommand("Carol paga 2 a Dave"));
        out.println(ConsoleFormatter.blocks(config.listBlocksUseCase().execute(new ListBlocksCommand()).blocks()));

        out.println("\n--- Buscando por hash el bloque 2 ---");
        var found = config.searchBlockUseCase()
                .execute(new SearchBlockCommand(BlockSearchCriteria.HASH, second.hash()));
        out.println(ConsoleFormatter.blocks(found.matches()));

        out.println("\n--- Rectificando el bloque 2 ---");
        out.println(ConsoleFormatter.block(config.rectifyBlockUseCase()
                .execute(new RectifyBlockCommand(2, "Bob paga 50 a Carol")).correctionBlock()));

        out.println("\n--- Anulando el bloque 1 ---");
        out.println(ConsoleFormatter.block(config.annulBlockUseCase()
                .execute(new AnnulBlockCommand(1)).annulmentBlock()));

        out.println("\n--- Validando la cadena ---");
        out.println(ConsoleFormatter.validation(config.validateChainUseCase().execute(new ValidateChainCommand())));
    }
}
