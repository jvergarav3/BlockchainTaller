package blockchain.core.chain.application.rectify_block.dtos;

import blockchain.core.chain.application.dtos.BlockInfo;

public record RectifyBlockResponse(BlockInfo correctionBlock) {
}
