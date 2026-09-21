package blockchain.core.chain.application;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.service.AddBlockService;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockCommand;
import blockchain.core.chain.application.rectify_block.dtos.RectifyBlockResponse;
import blockchain.core.chain.application.rectify_block.service.RectifyBlockService;
import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.exceptions.BlockNotFoundException;
import blockchain.core.chain.domain.exceptions.InvalidBlockDataException;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RectifyBlockServiceTest {

    private InMemoryBlockchainRepositoryAdapter repository;
    private RectifyBlockService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBlockchainRepositoryAdapter();
        service = new RectifyBlockService(repository);
        AddBlockService add = new AddBlockService(repository);
        add.execute(new AddBlockCommand("Pay 100"));
        add.execute(new AddBlockCommand("Pay 200"));
    }

    @Test
    void executeAppendsCorrectionBlockReferencingTheOriginal() {
        RectifyBlockResponse response = service.execute(new RectifyBlockCommand(2, "Pay 250"));

        assertNotNull(response);
        assertEquals(3, response.correctionBlock().id());
        assertEquals("Pay 250", response.correctionBlock().data());
        assertEquals(BlockType.CORRECTION, response.correctionBlock().type());
        assertEquals(2, response.correctionBlock().referencedBlockId());
    }

    @Test
    void executePersistsTheCorrectionAndKeepsTheOriginalIntact() {
        Bloque original = repository.load().findById(2).orElseThrow();
        String originalHash = original.getHash();

        RectifyBlockResponse response = service.execute(new RectifyBlockCommand(2, "Pay 250"));

        assertEquals(3, repository.load().size());
        assertEquals(response.correctionBlock().hash(), repository.load().getLast().getHash());
        assertEquals("Pay 200", original.getData());
        assertEquals(originalHash, original.getHash());
        assertDoesNotThrow(repository.load()::validate);
    }

    @Test
    void unknownBlockThrowsBlockNotFound() {
        assertThrows(BlockNotFoundException.class, () -> service.execute(new RectifyBlockCommand(42, "X")));

        assertEquals(2, repository.load().size());
    }

    @Test
    void blankNewDataIsRejected() {
        assertThrows(InvalidBlockDataException.class, () -> service.execute(new RectifyBlockCommand(1, " ")));
        assertThrows(InvalidBlockDataException.class, () -> service.execute(new RectifyBlockCommand(1, null)));

        assertEquals(2, repository.load().size());
    }
}
