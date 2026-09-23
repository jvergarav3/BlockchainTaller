package blockchain.core.chain.application.validate_chain.service;

import blockchain.core.chain.application.validate_chain.dtos.ValidateChainCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainResponse;
import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.exceptions.InvalidChainException;
import blockchain.core.chain.domain.inputports.ValidateChainUseCase;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class ValidateChainService implements ValidateChainUseCase {
    private final BlockchainRepositoryPort blockchainRepositoryPort;

    public ValidateChainService(BlockchainRepositoryPort blockchainRepositoryPort) {
        this.blockchainRepositoryPort = blockchainRepositoryPort;
    }

    @Override
    public ValidateChainResponse execute(ValidateChainCommand command) {
        Blockchain blockchain = blockchainRepositoryPort.load();
        try {
            blockchain.validate();
            return new ValidateChainResponse(true, blockchain.size(), null);
        } catch (InvalidChainException e) {
            return new ValidateChainResponse(false, blockchain.size(), e.getInvalidBlockId());
        }
    }
}
