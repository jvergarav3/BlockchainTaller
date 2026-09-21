package blockchain.core.chain.application.list_blocks.dtos;

import blockchain.core.chain.application.dtos.BlockInfo;

import java.util.List;

public record ListBlocksResponse(List<BlockInfo> blocks, int total) {
}
