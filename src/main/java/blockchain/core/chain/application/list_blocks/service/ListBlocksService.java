package blockchain.core.chain.application.list_blocks.service;

import blockchain.core.chain.application.list_blocks.dtos.ListBlocksCommand;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksResponse;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.domain.inputports.ListBlocksUseCase;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;

import java.util.List;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class ListBlocksService implements ListBlocksUseCase {
    private final BlockchainRepositoryPort blockchainRepositoryPort;

    public ListBlocksService(BlockchainRepositoryPort blockchainRepositoryPort) {
        this.blockchainRepositoryPort = blockchainRepositoryPort;
    }

    @Override
    public ListBlocksResponse execute(ListBlocksCommand command) {
        List<BlockInfo> blocks = blockchainRepositoryPort.load().listBlockchain().stream()
                .map(BlockInfo::from)
                .toList();
        return new ListBlocksResponse(blocks, blocks.size());
    }
}
