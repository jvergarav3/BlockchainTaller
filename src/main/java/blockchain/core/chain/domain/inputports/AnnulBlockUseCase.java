package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockResponse;

public interface AnnulBlockUseCase {

    AnnulBlockResponse execute(AnnulBlockCommand command);
}
