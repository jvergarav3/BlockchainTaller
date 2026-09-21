package blockchain.core.chain.infrastructure.adapter.repository;

import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;

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
