package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.list_blocks.dtos.ListBlocksCommand;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksResponse;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public interface ListBlocksUseCase {

    ListBlocksResponse execute(ListBlocksCommand command);
}
