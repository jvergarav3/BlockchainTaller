package blockchain.core.chain.domain.exceptions;

public final class BlockchainExceptions {

    public static final String BLOCK_NOT_FOUND = "Block not found: id=%d";
    public static final String EMPTY_DATA = "Block data must not be blank";
    public static final String BLOCK_ALREADY_ANNULLED = "Block %d is already annulled";
    public static final String BROKEN_CHAIN = "Chain is broken at block %d";

    private BlockchainExceptions() {
    }

    public static BlockNotFoundException blockNotFound(int id) {
        return new BlockNotFoundException(String.format(BLOCK_NOT_FOUND, id));
    }

    public static InvalidBlockDataException emptyData() {
        return new InvalidBlockDataException(EMPTY_DATA);
    }

    public static BlockAlreadyAnnulledException blockAlreadyAnnulled(int id) {
        return new BlockAlreadyAnnulledException(String.format(BLOCK_ALREADY_ANNULLED, id));
    }

    public static InvalidChainException brokenChain(int id) {
        return new InvalidChainException(String.format(BROKEN_CHAIN, id), id);
    }
}
