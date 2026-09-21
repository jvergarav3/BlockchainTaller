package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.validate_chain.dtos.ValidateChainCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainResponse;

public interface ValidateChainUseCase {

    ValidateChainResponse execute(ValidateChainCommand command);
}
