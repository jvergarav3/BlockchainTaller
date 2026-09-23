package blockchain.core.chain.presentation.cli.view;

import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainResponse;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.presentation.UserMessages;

import java.util.List;
import java.util.stream.Collectors;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public final class ConsoleFormatter {

    private static final String NONE = "None";

    private ConsoleFormatter() {
    }

    public static String menu() {
        return """

                ===== Blockchain =====
                1. Agregar bloque
                2. Listar cadena
                3. Buscar bloque
                4. Rectificar bloque
                5. Anular bloque
                6. Validar cadena
                7. Ejecutar demo del taller
                0. Salir""";
    }

    public static String block(BlockInfo block) {
        StringBuilder text = new StringBuilder();
        text.append("Bloque ").append(block.id()).append(":\n");
        text.append("  Datos: ").append(block.data()).append('\n');
        text.append("  Hash anterior: ").append(block.previousHash() == null ? NONE : block.previousHash()).append('\n');
        text.append("  Hash actual: ").append(block.hash());
        if (block.type() != BlockType.NORMAL) {
            text.append("\n  Tipo: ").append(UserMessages.typeLabel(block.type()));
            text.append("\n  Referencia: bloque ").append(block.referencedBlockId());
        }
        return text.toString();
    }

    public static String blocks(List<BlockInfo> blocks) {
        if (blocks.isEmpty()) {
            return "La cadena está vacía.";
        }
        return blocks.stream().map(ConsoleFormatter::block).collect(Collectors.joining("\n\n"));
    }

    public static String validation(ValidateChainResponse response) {
        if (response.valid()) {
            return "Cadena válida (" + response.blocksChecked() + " bloques verificados).";
        }
        return "Cadena INVÁLIDA: se rompe en el bloque " + response.firstInvalidBlockId() + ".";
    }
}
