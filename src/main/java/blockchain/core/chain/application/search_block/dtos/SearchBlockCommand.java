package blockchain.core.chain.application.search_block.dtos;

import blockchain.core.chain.domain.enums.BlockSearchCriteria;

public record SearchBlockCommand(BlockSearchCriteria criteria, String value) {
}
