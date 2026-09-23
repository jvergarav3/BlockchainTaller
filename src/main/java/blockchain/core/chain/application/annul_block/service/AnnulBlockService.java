package blockchain.core.chain.application.annul_block.service;

import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockResponse;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.inputports.AnnulBlockUseCase;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class AnnulBlockService implements AnnulBlockUseCase {
    private final BlockchainRepositoryPort blockchainRepositoryPort;

    public AnnulBlockService(BlockchainRepositoryPort blockchainRepositoryPort) {
        this.blockchainRepositoryPort = blockchainRepositoryPort;
    }

    @Override
    public AnnulBlockResponse execute(AnnulBlockCommand command) {
        Blockchain blockchain = blockchainRepositoryPort.load();
        Bloque annulment = blockchain.annulBlock(command.blockId());
        blockchainRepositoryPort.save(blockchain);
        return new AnnulBlockResponse(BlockInfo.from(annulment));
    }
}
