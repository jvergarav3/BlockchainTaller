package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.list_blocks.dtos.ListBlocksCommand;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksResponse;

public interface ListBlocksUseCase {

    ListBlocksResponse execute(ListBlocksCommand command);
}
