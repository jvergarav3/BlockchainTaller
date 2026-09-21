package blockchain.core.chain.application;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.service.AddBlockService;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainCommand;
import blockchain.core.chain.application.validate_chain.dtos.ValidateChainResponse;
import blockchain.core.chain.application.validate_chain.service.ValidateChainService;
import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.exceptions.BlockchainExceptions;
import blockchain.core.chain.domain.exceptions.InvalidChainException;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateChainServiceTest {

    private InMemoryBlockchainRepositoryAdapter repository;
    private ValidateChainService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBlockchainRepositoryAdapter();
        service = new ValidateChainService(repository);
    }

    @Test
    void emptyChainIsValid() {
        ValidateChainResponse response = service.execute(new ValidateChainCommand());

        assertNotNull(response);
        assertTrue(response.valid());
        assertEquals(0, response.blocksChecked());
        assertNull(response.firstInvalidBlockId());
    }

    @Test
    void intactChainIsValidAndReportsCheckedBlocks() {
        AddBlockService add = new AddBlockService(repository);
        add.execute(new AddBlockCommand("A"));
        add.execute(new AddBlockCommand("B"));
        add.execute(new AddBlockCommand("C"));

        ValidateChainResponse response = service.execute(new ValidateChainCommand());

        assertTrue(response.valid());
        assertEquals(3, response.blocksChecked());
        assertNull(response.firstInvalidBlockId());
    }

    @Test
    void brokenChainIsReportedInTheResponseInsteadOfThrowing() {
        repository.save(new BrokenAtBlock(2));
        AddBlockService add = new AddBlockService(repository);
        add.execute(new AddBlockCommand("A"));
        add.execute(new AddBlockCommand("B"));
        add.execute(new AddBlockCommand("C"));

        ValidateChainResponse response = assertDoesNotThrow(() -> service.execute(new ValidateChainCommand()));

        assertFalse(response.valid());
        assertEquals(2, response.firstInvalidBlockId());
    }

    private static class BrokenAtBlock extends Blockchain {
        private final int blockId;

        BrokenAtBlock(int blockId) {
            this.blockId = blockId;
        }

        @Override
        public void validate() {
            throw BlockchainExceptions.brokenChain(blockId);
        }
    }
}
