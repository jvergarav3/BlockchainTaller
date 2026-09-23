package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockCommand;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockResponse;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public interface RectifyBlockUseCase {

    RectifyBlockResponse execute(RectifyBlockCommand command);
}
