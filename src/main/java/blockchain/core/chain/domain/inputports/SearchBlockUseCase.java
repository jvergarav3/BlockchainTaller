package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.search_block.dtos.SearchBlockCommand;
import blockchain.core.chain.application.search_block.dtos.SearchBlockResponse;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public interface SearchBlockUseCase {

    SearchBlockResponse execute(SearchBlockCommand command);
}
