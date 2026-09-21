package blockchain.core.chain.application.dtos;

import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.enums.BlockType;

public record BlockInfo(
        int id,
        String data,
        String previousHash,
        String hash,
        BlockType type,
        Integer referencedBlockId
) {

    public static BlockInfo from(Bloque block) {
        return new BlockInfo(
                block.getId(),
                block.getData(),
                block.getPreviousHash(),
                block.getHash(),
                block.getType(),
                block.getReferencedBlockId()
        );
    }
}
