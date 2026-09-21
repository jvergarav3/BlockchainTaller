package blockchain.core.chain.application.search_block.dtos;

import blockchain.core.chain.application.dtos.BlockInfo;

import java.util.List;

public record SearchBlockResponse(List<BlockInfo> matches) {
}
