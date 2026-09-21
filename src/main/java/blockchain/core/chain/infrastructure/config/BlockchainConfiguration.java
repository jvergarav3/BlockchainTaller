package blockchain.core.chain.infrastructure.config;

import blockchain.core.chain.application.add_block.service.AddBlockService;
import blockchain.core.chain.application.annul_block.service.AnnulBlockService;
import blockchain.core.chain.application.list_blocks.service.ListBlocksService;
import blockchain.core.chain.application.rectify_block.service.RectifyBlockService;
import blockchain.core.chain.application.search_block.service.SearchBlockService;
import blockchain.core.chain.application.validate_chain.service.ValidateChainService;
import blockchain.core.chain.domain.inputports.AddBlockUseCase;
import blockchain.core.chain.domain.inputports.AnnulBlockUseCase;
import blockchain.core.chain.domain.inputports.ListBlocksUseCase;
import blockchain.core.chain.domain.inputports.RectifyBlockUseCase;
import blockchain.core.chain.domain.inputports.SearchBlockUseCase;
import blockchain.core.chain.domain.inputports.ValidateChainUseCase;
import blockchain.core.chain.domain.outputports.BlockchainRepositoryPort;
import blockchain.core.chain.infrastructure.adapter.repository.InMemoryBlockchainRepositoryAdapter;

public class BlockchainConfiguration {

    private final AddBlockUseCase addBlockUseCase;
    private final ListBlocksUseCase listBlocksUseCase;
    private final SearchBlockUseCase searchBlockUseCase;
    private final RectifyBlockUseCase rectifyBlockUseCase;
    private final AnnulBlockUseCase annulBlockUseCase;
    private final ValidateChainUseCase validateChainUseCase;

    public BlockchainConfiguration() {
        BlockchainRepositoryPort repository = new InMemoryBlockchainRepositoryAdapter();
        this.addBlockUseCase = new AddBlockService(repository);
        this.listBlocksUseCase = new ListBlocksService(repository);
        this.searchBlockUseCase = new SearchBlockService(repository);
        this.rectifyBlockUseCase = new RectifyBlockService(repository);
        this.annulBlockUseCase = new AnnulBlockService(repository);
        this.validateChainUseCase = new ValidateChainService(repository);
    }

    public AddBlockUseCase addBlockUseCase() {
        return addBlockUseCase;
    }

    public ListBlocksUseCase listBlocksUseCase() {
        return listBlocksUseCase;
    }

    public SearchBlockUseCase searchBlockUseCase() {
        return searchBlockUseCase;
    }

    public RectifyBlockUseCase rectifyBlockUseCase() {
        return rectifyBlockUseCase;
    }

    public AnnulBlockUseCase annulBlockUseCase() {
        return annulBlockUseCase;
    }

    public ValidateChainUseCase validateChainUseCase() {
        return validateChainUseCase;
    }
}
