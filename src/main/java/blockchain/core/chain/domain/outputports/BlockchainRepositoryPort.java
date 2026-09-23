package blockchain.core.chain.domain.outputports;

import blockchain.core.chain.domain.entity.Blockchain;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public interface BlockchainRepositoryPort {

    Blockchain load();

    void save(Blockchain blockchain);
}
