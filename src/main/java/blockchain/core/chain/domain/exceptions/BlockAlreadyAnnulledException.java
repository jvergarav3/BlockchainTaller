package blockchain.core.chain.domain.exceptions;

public class BlockAlreadyAnnulledException extends RuntimeException {

    public BlockAlreadyAnnulledException(String message) {
        super(message);
    }
}
