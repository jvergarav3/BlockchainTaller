package blockchain.core.chain.application.list_blocks.dtos;

import blockchain.core.chain.application.dtos.BlockInfo;

import java.util.List;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public record ListBlocksResponse(List<BlockInfo> blocks, int total) {
}
