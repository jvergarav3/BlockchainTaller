package blockchain.core.chain.domain.exceptions;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
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
