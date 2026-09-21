package blockchain.core.chain.application.rectify_block.dtos;

public record RectifyBlockCommand(int blockId, String newData) {
}
