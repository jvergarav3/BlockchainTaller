package blockchain.core.chain.application.rectify_block.service;

import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockCommand;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockResponse;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.inputports.RectifyBlockUseCase;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class RectifyBlockService implements RectifyBlockUseCase {
    private final BlockchainRepositoryPort blockchainRepositoryPort;

    public RectifyBlockService(BlockchainRepositoryPort blockchainRepositoryPort) {
        this.blockchainRepositoryPort = blockchainRepositoryPort;
    }

    @Override
    public RectifyBlockResponse execute(RectifyBlockCommand command) {
        Blockchain blockchain = blockchainRepositoryPort.load();
        Bloque correction = blockchain.rectifyBlock(command.blockId(), command.newData());
        blockchainRepositoryPort.save(blockchain);
        return new RectifyBlockResponse(BlockInfo.from(correction));
    }
}
