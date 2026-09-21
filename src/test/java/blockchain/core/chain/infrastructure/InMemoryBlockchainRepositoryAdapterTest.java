package blockchain.core.chain.infrastructure;

import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBlockchainRepositoryAdapterTest {

    @Test
    void loadReturnsAnEmptyChainInitially() {
        Blockchain loaded = new InMemoryBlockchainRepositoryAdapter().load();

        assertNotNull(loaded);
        assertEquals(0, loaded.size());
    }

    @Test
    void loadReturnsTheSameInstanceOnEveryCall() {
        InMemoryBlockchainRepositoryAdapter repository = new InMemoryBlockchainRepositoryAdapter();

        assertSame(repository.load(), repository.load());
    }

    @Test
    void changesMadeToTheLoadedChainAreVisibleOnNextLoad() {
        InMemoryBlockchainRepositoryAdapter repository = new InMemoryBlockchainRepositoryAdapter();

        repository.load().addBlock("A");

        assertEquals(1, repository.load().size());
    }

    @Test
    void saveReplacesTheStoredChain() {
        InMemoryBlockchainRepositoryAdapter repository = new InMemoryBlockchainRepositoryAdapter();
        Blockchain replacement = new Blockchain();
        replacement.addBlock("X");
        replacement.addBlock("Y");

        repository.save(replacement);

        assertSame(replacement, repository.load());
        assertEquals(2, repository.load().size());
    }
}
