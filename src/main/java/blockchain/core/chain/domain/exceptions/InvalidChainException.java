package blockchain.core.chain.domain.exceptions;

public class InvalidChainException extends RuntimeException {

    private final int invalidBlockId;

    public InvalidChainException(String message, int invalidBlockId) {
        super(message);
        this.invalidBlockId = invalidBlockId;
    }

    public int getInvalidBlockId() {
        return invalidBlockId;
    }
}
