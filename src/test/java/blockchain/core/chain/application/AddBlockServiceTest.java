package blockchain.core.chain.application;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.dtos.AddBlockResponse;
import blockchain.core.chain.application.add_block.service.AddBlockService;
import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.exceptions.InvalidBlockDataException;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddBlockServiceTest {

    private InMemoryBlockchainRepositoryAdapter repository;
    private AddBlockService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBlockchainRepositoryAdapter();
        service = new AddBlockService(repository);
    }

    @Test
    void executeReturnsInfoOfTheNewBlock() {
        AddBlockResponse response = service.execute(new AddBlockCommand("Alice pays Bob"));

        assertNotNull(response);
        assertEquals(1, response.block().id());
        assertEquals("Alice pays Bob", response.block().data());
        assertNull(response.block().previousHash());
        assertNotNull(response.block().hash());
        assertEquals(BlockType.NORMAL, response.block().type());
        assertNull(response.block().referencedBlockId());
    }

    @Test
    void executePersistsTheBlockInTheRepository() {
        service.execute(new AddBlockCommand("A"));

        Blockchain stored = repository.load();
        assertEquals(1, stored.size());
        assertEquals("A", stored.getLast().getData());
    }

    @Test
    void consecutiveBlocksAreLinkedByHash() {
        AddBlockResponse first = service.execute(new AddBlockCommand("A"));
        AddBlockResponse second = service.execute(new AddBlockCommand("B"));

        assertEquals(2, second.block().id());
        assertEquals(first.block().hash(), second.block().previousHash());
        assertEquals(2, repository.load().size());
    }

    @Test
    void blankDataIsRejectedAndNothingIsStored() {
        assertThrows(InvalidBlockDataException.class, () -> service.execute(new AddBlockCommand("  ")));
        assertThrows(InvalidBlockDataException.class, () -> service.execute(new AddBlockCommand(null)));

        assertEquals(0, repository.load().size());
    }
}
