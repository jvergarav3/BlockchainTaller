package blockchain.core.chain.application.validate_chain.dtos;

public record ValidateChainResponse(boolean valid, int blocksChecked, Integer firstInvalidBlockId) {
}
