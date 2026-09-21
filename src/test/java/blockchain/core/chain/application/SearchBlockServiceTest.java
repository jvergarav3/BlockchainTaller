package blockchain.core.chain.application;

import blockchain.core.chain.application.add_block.dtos.AddBlockCommand;
import blockchain.core.chain.application.add_block.dtos.AddBlockResponse;
import blockchain.core.chain.application.add_block.service.AddBlockService;
import blockchain.core.chain.application.dtos.BlockInfo;
import blockchain.core.chain.application.search_block.dtos.SearchBlockCommand;
import blockchain.core.chain.application.search_block.dtos.SearchBlockResponse;
import blockchain.core.chain.application.search_block.service.SearchBlockService;
import blockchain.core.chain.domain.enums.BlockSearchCriteria;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchBlockServiceTest {

    private SearchBlockService service;
    private AddBlockResponse first;
    private AddBlockResponse second;

    @BeforeEach
    void setUp() {
        InMemoryBlockchainRepositoryAdapter repository = new InMemoryBlockchainRepositoryAdapter();
        service = new SearchBlockService(repository);
        AddBlockService add = new AddBlockService(repository);
        first = add.execute(new AddBlockCommand("Alice pays Bob"));
        second = add.execute(new AddBlockCommand("Carol pays Dave"));
        add.execute(new AddBlockCommand("Eve sends ALICE a gift"));
    }

    @Test
    void searchByHashReturnsTheMatchingBlock() {
        SearchBlockResponse response =
                service.execute(new SearchBlockCommand(BlockSearchCriteria.HASH, second.block().hash()));

        assertNotNull(response);
        assertEquals(List.of(second.block()), response.matches());
    }

    @Test
    void searchByUnknownHashReturnsNoMatches() {
        SearchBlockResponse response =
                service.execute(new SearchBlockCommand(BlockSearchCriteria.HASH, "does-not-exist"));

        assertTrue(response.matches().isEmpty());
    }

    @Test
    void searchByIdReturnsTheMatchingBlock() {
        SearchBlockResponse response = service.execute(new SearchBlockCommand(BlockSearchCriteria.ID, "1"));

        assertEquals(List.of(first.block()), response.matches());
    }

    @Test
    void searchByUnknownIdReturnsNoMatches() {
        SearchBlockResponse response = service.execute(new SearchBlockCommand(BlockSearchCriteria.ID, "99"));

        assertTrue(response.matches().isEmpty());
    }

    @Test
    void searchByTextReturnsEveryCaseInsensitiveMatchInChainOrder() {
        SearchBlockResponse response =
                service.execute(new SearchBlockCommand(BlockSearchCriteria.TEXT, "alice"));

        assertEquals(List.of(1, 3), response.matches().stream().map(BlockInfo::id).toList());
    }

    @Test
    void searchByTextWithoutMatchesReturnsEmptyList() {
        SearchBlockResponse response =
                service.execute(new SearchBlockCommand(BlockSearchCriteria.TEXT, "zzz"));

        assertNotNull(response.matches());
        assertTrue(response.matches().isEmpty());
    }
}
