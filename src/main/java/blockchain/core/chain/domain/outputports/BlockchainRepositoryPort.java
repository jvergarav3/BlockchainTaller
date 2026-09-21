package blockchain.core.chain.domain.outputports;

import blockchain.core.chain.domain.entity.Blockchain;

public interface BlockchainRepositoryPort {

    Blockchain load();

    void save(Blockchain blockchain);
}
