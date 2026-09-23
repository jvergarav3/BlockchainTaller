package blockchain.core.chain.domain.exceptions;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class BlockAlreadyAnnulledException extends RuntimeException {

    public BlockAlreadyAnnulledException(String message) {
        super(message);
    }
}
