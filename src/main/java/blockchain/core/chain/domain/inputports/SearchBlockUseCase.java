package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.search_block.dtos.SearchBlockCommand;
import blockchain.core.chain.application.search_block.dtos.SearchBlockResponse;

public interface SearchBlockUseCase {

    SearchBlockResponse execute(SearchBlockCommand command);
}
