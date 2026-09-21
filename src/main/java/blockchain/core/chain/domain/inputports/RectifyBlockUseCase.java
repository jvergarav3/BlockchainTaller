package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockCommand;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockResponse;

public interface RectifyBlockUseCase {

    RectifyBlockResponse execute(RectifyBlockCommand command);
}
