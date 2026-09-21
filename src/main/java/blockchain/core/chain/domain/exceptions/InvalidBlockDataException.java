package blockchain.core.chain.domain.exceptions;

public class InvalidBlockDataException extends RuntimeException {

    public InvalidBlockDataException(String message) {
        super(message);
    }
}
