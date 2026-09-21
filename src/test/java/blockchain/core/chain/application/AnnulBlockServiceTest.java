package blockchain.core.chain.application;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.service.AddBlockService;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockResponse;
import blockchain.core.chain.application.annul_block.service.AnnulBlockService;
import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.exceptions.BlockAlreadyAnnulledException;
import blockchain.core.chain.domain.exceptions.BlockNotFoundException;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnulBlockServiceTest {

    private InMemoryBlockchainRepositoryAdapter repository;
    private AnnulBlockService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBlockchainRepositoryAdapter();
        service = new AnnulBlockService(repository);
        AddBlockService add = new AddBlockService(repository);
        add.execute(new AddBlockCommand("A"));
        add.execute(new AddBlockCommand("B"));
    }

    @Test
    void executeAppendsAnnulmentBlockReferencingTheTarget() {
        AnnulBlockResponse response = service.execute(new AnnulBlockCommand(2));

        assertNotNull(response);
        assertEquals(3, response.annulmentBlock().id());
        assertEquals(BlockType.ANNULMENT, response.annulmentBlock().type());
        assertEquals(2, response.annulmentBlock().referencedBlockId());
        assertEquals("Annulment of block 2", response.annulmentBlock().data());
    }

    @Test
    void executePersistsTheAnnulmentAndKeepsTheTargetInTheChain() {
        Bloque target = repository.load().findById(2).orElseThrow();
        String targetHash = target.getHash();

        service.execute(new AnnulBlockCommand(2));

        assertEquals(3, repository.load().size());
        assertSame(target, repository.load().findById(2).orElseThrow());
        assertEquals(targetHash, target.getHash());
        assertDoesNotThrow(repository.load()::validate);
    }

    @Test
    void unknownBlockThrowsBlockNotFound() {
        assertThrows(BlockNotFoundException.class, () -> service.execute(new AnnulBlockCommand(99)));

        assertEquals(2, repository.load().size());
    }

    @Test
    void annullingTwiceThrowsAlreadyAnnulled() {
        service.execute(new AnnulBlockCommand(1));

        assertThrows(BlockAlreadyAnnulledException.class, () -> service.execute(new AnnulBlockCommand(1)));

        assertEquals(3, repository.load().size());
    }

    @Test
    void annulmentBlockCannotBeAnnulled() {
        AnnulBlockResponse first = service.execute(new AnnulBlockCommand(1));

        assertThrows(BlockAlreadyAnnulledException.class,
                () -> service.execute(new AnnulBlockCommand(first.annulmentBlock().id())));
    }
}
