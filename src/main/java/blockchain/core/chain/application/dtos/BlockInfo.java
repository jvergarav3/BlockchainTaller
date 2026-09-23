package blockchain.core.chain.application.dtos;

import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.enums.BlockType;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
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
