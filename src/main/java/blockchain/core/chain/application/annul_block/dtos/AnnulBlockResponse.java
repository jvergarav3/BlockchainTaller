package blockchain.core.chain.application.annul_block.dtos;

import blockchain.core.chain.application.dtos.BlockInfo;

public record AnnulBlockResponse(BlockInfo annulmentBlock) {
}
