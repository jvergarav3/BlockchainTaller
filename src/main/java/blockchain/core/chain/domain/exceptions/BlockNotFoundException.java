package blockchain.core.chain.domain.exceptions;

public class BlockNotFoundException extends RuntimeException {

    public BlockNotFoundException(String message) {
        super(message);
    }
}
