package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.dtos.AddBlockResponse;

public interface AddBlockUseCase {

    AddBlockResponse execute(AddBlockCommand command);
}
