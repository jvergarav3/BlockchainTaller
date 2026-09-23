package blockchain.core.chain.application.add_block.service;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.dtos.AddBlockResponse;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.inputports.AddBlockUseCase;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class AddBlockService implements AddBlockUseCase {
    private final BlockchainRepositoryPort blockchainRepositoryPort;

    public AddBlockService(BlockchainRepositoryPort blockchainRepositoryPort) {
        this.blockchainRepositoryPort = blockchainRepositoryPort;
    }

    @Override
    public AddBlockResponse execute(AddBlockCommand command) {
        Blockchain blockchain = blockchainRepositoryPort.load();
        Bloque block = blockchain.addBlock(command.data());
        blockchainRepositoryPort.save(blockchain);
        return new AddBlockResponse(BlockInfo.from(block));
    }
}
