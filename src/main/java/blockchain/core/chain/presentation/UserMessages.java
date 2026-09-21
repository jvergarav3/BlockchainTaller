package blockchain.core.chain.presentation;

import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.exceptions.BlockAlreadyAnnulledException;
import blockchain.core.chain.domain.exceptions.BlockNotFoundException;
import blockchain.core.chain.domain.exceptions.InvalidBlockDataException;

public final class UserMessages {

    private UserMessages() {
    }

    public static String of(RuntimeException exception) {
        if (exception instanceof InvalidBlockDataException) {
            return "Los datos del bloque no pueden estar vacíos.";
        }
        if (exception instanceof BlockNotFoundException) {
            return "No existe el bloque indicado.";
        }
        if (exception instanceof BlockAlreadyAnnulledException) {
            return "Ese bloque ya está anulado.";
        }
        return exception.getMessage();
    }

    public static String typeLabel(BlockType type) {
        return switch (type) {
            case NORMAL -> "Normal";
            case CORRECTION -> "Corrección";
            case ANNULMENT -> "Anulación";
        };
    }
}
