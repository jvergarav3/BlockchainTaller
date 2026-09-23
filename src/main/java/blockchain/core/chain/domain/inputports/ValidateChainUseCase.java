package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.validate_chain.dtos.ValidateChainCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainResponse;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public interface ValidateChainUseCase {

    ValidateChainResponse execute(ValidateChainCommand command);
}
