package blockchain.core.chain.domain.inputports;

import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockResponse;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public interface AnnulBlockUseCase {

    AnnulBlockResponse execute(AnnulBlockCommand command);
}
