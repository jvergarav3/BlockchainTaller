package blockchain.core.chain.application;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.service.AddBlockService;
import blockchain.core.chain.application.annul_block.dtos.AnnulBlockCommand;
import blockchain.core.chain.application.annul_block.service.AnnulBlockService;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksCommand;
import blockchain.core.chain.application.list_blocks.dtos.ListBlocksResponse;
import blockchain.core.chain.application.list_blocks.service.ListBlocksService;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListBlocksServiceTest {

    private InMemoryBlockchainRepositoryAdapter repository;
    private ListBlocksService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBlockchainRepositoryAdapter();
        service = new ListBlocksService(repository);
    }

    @Test
    void emptyChainReturnsEmptyListAndZeroTotal() {
        ListBlocksResponse response = service.execute(new ListBlocksCommand());

        assertNotNull(response);
        assertTrue(response.blocks().isEmpty());
        assertEquals(0, response.total());
    }

    @Test
    void returnsAllBlocksInChainOrderWithTotal() {
        AddBlockService add = new AddBlockService(repository);
        add.execute(new AddBlockCommand("A"));
        add.execute(new AddBlockCommand("B"));
        add.execute(new AddBlockCommand("C"));

        ListBlocksResponse response = service.execute(new ListBlocksCommand());

        assertEquals(3, response.total());
        assertEquals(List.of(1, 2, 3), response.blocks().stream().map(BlockInfo::id).toList());
        assertEquals(List.of("A", "B", "C"), response.blocks().stream().map(BlockInfo::data).toList());
    }

    @Test
    void blocksAreMappedWithHashesAndLinks() {
        AddBlockService add = new AddBlockService(repository);
        add.execute(new AddBlockCommand("A"));
        add.execute(new AddBlockCommand("B"));

        List<BlockInfo> blocks = service.execute(new ListBlocksCommand()).blocks();

        assertNull(blocks.get(0).previousHash());
        assertEquals(blocks.get(0).hash(), blocks.get(1).previousHash());
    }

    @Test
    void annulmentBlocksAreListedToo() {
        new AddBlockService(repository).execute(new AddBlockCommand("A"));
        new AnnulBlockService(repository).execute(new AnnulBlockCommand(1));

        ListBlocksResponse response = service.execute(new ListBlocksCommand());

        assertEquals(2, response.total());
        assertEquals(BlockType.ANNULMENT, response.blocks().get(1).type());
        assertEquals(1, response.blocks().get(1).referencedBlockId());
    }
}
