package blockchain.core.chain.application.search_block.service;

import blockchain.core.chain.application.search_block.dtos.SearchBlockCommand;
import blockchain.core.chain.application.search_block.dtos.SearchBlockResponse;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.inputports.SearchBlockUseCase;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;

import java.util.List;

public class SearchBlockService implements SearchBlockUseCase {
    private final BlockchainRepositoryPort blockchainRepositoryPort;

    public SearchBlockService(BlockchainRepositoryPort blockchainRepositoryPort) {
        this.blockchainRepositoryPort = blockchainRepositoryPort;
    }

    @Override
    public SearchBlockResponse execute(SearchBlockCommand command) {
        Blockchain blockchain = blockchainRepositoryPort.load();
        List<Bloque> matches = switch (command.criteria()) {
            case HASH -> blockchain.searchBlock(command.value()).stream().toList();
            case ID -> findById(blockchain, command.value());
            case TEXT -> blockchain.findByText(command.value());
        };
        return new SearchBlockResponse(matches.stream().map(BlockInfo::from).toList());
    }

    private static List<Bloque> findById(Blockchain blockchain, String value) {
        try {
            return blockchain.findById(Integer.parseInt(value.trim())).stream().toList();
        } catch (NumberFormatException e) {
            return List.of();
        }
    }
}
