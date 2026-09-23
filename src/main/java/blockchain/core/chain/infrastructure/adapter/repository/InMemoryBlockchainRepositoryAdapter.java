package blockchain.core.chain.infrastructure.adapter.repository;

import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class InMemoryBlockchainRepositoryAdapter implements BlockchainRepositoryPort {

    private Blockchain blockchain = new Blockchain();

    @Override
    public Blockchain load() {
        return blockchain;
    }

    @Override
    public void save(Blockchain blockchain) {
        this.blockchain = blockchain;
    }
}
