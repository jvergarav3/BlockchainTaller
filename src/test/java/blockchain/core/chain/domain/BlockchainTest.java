package blockchain.core.chain.domain;

import blockchain.core.chain.domain.entity.Blockchain;
import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.exceptions.BlockAlreadyAnnulledException;
import blockchain.core.chain.domain.exceptions.BlockNotFoundException;
import blockchain.core.chain.domain.exceptions.BlockchainExceptions;
import blockchain.core.chain.domain.exceptions.InvalidBlockDataException;
import blockchain.core.chain.domain.exceptions.InvalidChainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainTest {

    private Blockchain chain;

    @BeforeEach
    void setUp() {
        chain = new Blockchain();
    }

    @Test
    void newChainIsEmpty() {
        assertEquals(0, chain.size());
        assertNull(chain.getLast());
        assertTrue(chain.listBlockchain().isEmpty());
    }

    @Test
    void addBlockCreatesGenesisWithoutPreviousHash() {
        Bloque block = chain.addBlock("genesis");

        assertEquals(1, block.getId());
        assertEquals("genesis", block.getData());
        assertNull(block.getPreviousHash());
        assertEquals(BlockType.NORMAL, block.getType());
        assertNull(block.getReferencedBlockId());
        assertEquals(1, chain.size());
        assertSame(block, chain.getLast());
    }

    @Test
    void addBlockLinksToPreviousBlockHash() {
        Bloque first = chain.addBlock("A");
        Bloque second = chain.addBlock("B");
        Bloque third = chain.addBlock("C");

        assertEquals(first.getHash(), second.getPreviousHash());
        assertEquals(second.getHash(), third.getPreviousHash());
        assertEquals(List.of(1, 2, 3), List.of(first.getId(), second.getId(), third.getId()));
        assertSame(second, first.getNext());
        assertSame(third, second.getNext());
    }

    @Test
    void addBlockRejectsNullData() {
        assertThrows(InvalidBlockDataException.class, () -> chain.addBlock(null));
        assertEquals(0, chain.size());
    }

    @Test
    void addBlockRejectsBlankData() {
        InvalidBlockDataException ex =
                assertThrows(InvalidBlockDataException.class, () -> chain.addBlock("   "));

        assertEquals(BlockchainExceptions.EMPTY_DATA, ex.getMessage());
        assertEquals(0, chain.size());
    }

    @Test
    void listBlockchainReturnsBlocksInInsertionOrder() {
        chain.addBlock("A");
        chain.addBlock("B");
        chain.addBlock("C");

        List<String> data = chain.listBlockchain().stream().map(Bloque::getData).toList();

        assertEquals(List.of("A", "B", "C"), data);
    }

    @Test
    void listBlockchainIsUnmodifiable() {
        chain.addBlock("A");

        List<Bloque> list = chain.listBlockchain();

        assertThrows(UnsupportedOperationException.class, list::clear);
        assertEquals(1, chain.size());
    }

    @Test
    void searchBlockFindsBlockByHash() {
        chain.addBlock("A");
        Bloque second = chain.addBlock("B");

        Optional<Bloque> found = chain.searchBlock(second.getHash());

        assertTrue(found.isPresent());
        assertSame(second, found.get());
    }

    @Test
    void searchBlockReturnsEmptyForUnknownHash() {
        chain.addBlock("A");

        assertTrue(chain.searchBlock("does-not-exist").isEmpty());
    }

    @Test
    void searchBlockOnEmptyChainReturnsEmpty() {
        assertTrue(chain.searchBlock("anything").isEmpty());
    }

    @Test
    void findByIdReturnsMatchingBlock() {
        chain.addBlock("A");
        Bloque second = chain.addBlock("B");

        assertSame(second, chain.findById(2).orElseThrow());
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        chain.addBlock("A");

        assertTrue(chain.findById(99).isEmpty());
        assertTrue(chain.findById(0).isEmpty());
    }

    @Test
    void findByTextIsCaseInsensitiveAndMatchesSubstrings() {
        chain.addBlock("Alice pays Bob");
        chain.addBlock("Carol pays Dave");
        chain.addBlock("Eve sends ALICE a gift");

        List<Bloque> matches = chain.findByText("alice");

        assertEquals(List.of(1, 3), matches.stream().map(Bloque::getId).toList());
    }

    @Test
    void findByTextReturnsEmptyWhenNothingMatches() {
        chain.addBlock("A");

        assertTrue(chain.findByText("zzz").isEmpty());
    }

    @Test
    void findByTextReturnsEmptyForNullOrBlankText() {
        chain.addBlock("A");

        assertTrue(chain.findByText(null).isEmpty());
        assertTrue(chain.findByText("").isEmpty());
        assertTrue(chain.findByText("   ").isEmpty());
    }

    @Test
    void rectifyBlockAppendsCorrectionBlockReferencingOriginal() {
        chain.addBlock("Pay 100");
        Bloque original = chain.addBlock("Pay 200");

        Bloque correction = chain.rectifyBlock(2, "Pay 250");

        assertEquals(3, chain.size());
        assertEquals(3, correction.getId());
        assertEquals(BlockType.CORRECTION, correction.getType());
        assertEquals(2, correction.getReferencedBlockId());
        assertEquals("Pay 250", correction.getData());
        assertEquals(original.getHash(), correction.getPreviousHash());
        assertSame(correction, chain.getLast());
    }

    @Test
    void rectifyBlockDoesNotMutateOriginal() {
        Bloque original = chain.addBlock("Pay 100");
        String hashBefore = original.getHash();

        chain.rectifyBlock(1, "Pay 150");

        assertEquals("Pay 100", original.getData());
        assertEquals(hashBefore, original.getHash());
        assertEquals(BlockType.NORMAL, original.getType());
    }

    @Test
    void rectifyBlockThrowsWhenBlockDoesNotExist() {
        chain.addBlock("A");

        BlockNotFoundException ex =
                assertThrows(BlockNotFoundException.class, () -> chain.rectifyBlock(5, "B"));

        assertEquals(String.format(BlockchainExceptions.BLOCK_NOT_FOUND, 5), ex.getMessage());
        assertEquals(1, chain.size());
    }

    @Test
    void rectifyBlockRejectsBlankData() {
        chain.addBlock("A");

        assertThrows(InvalidBlockDataException.class, () -> chain.rectifyBlock(1, " "));
        assertThrows(InvalidBlockDataException.class, () -> chain.rectifyBlock(1, null));
        assertEquals(1, chain.size());
    }

    @Test
    void annulBlockAppendsAnnulmentBlockReferencingTarget() {
        chain.addBlock("A");
        Bloque target = chain.addBlock("B");

        Bloque annulment = chain.annulBlock(2);

        assertEquals(3, chain.size());
        assertEquals(3, annulment.getId());
        assertEquals(BlockType.ANNULMENT, annulment.getType());
        assertEquals(2, annulment.getReferencedBlockId());
        assertEquals("Annulment of block 2", annulment.getData());
        assertEquals(target.getHash(), annulment.getPreviousHash());
    }

    @Test
    void annulBlockDoesNotRemoveOrAlterTarget() {
        Bloque target = chain.addBlock("A");
        String hashBefore = target.getHash();

        chain.annulBlock(1);

        assertSame(target, chain.findById(1).orElseThrow());
        assertEquals(hashBefore, target.getHash());
        assertEquals(BlockType.NORMAL, target.getType());
    }

    @Test
    void annulBlockThrowsWhenBlockDoesNotExist() {
        chain.addBlock("A");

        assertThrows(BlockNotFoundException.class, () -> chain.annulBlock(9));
        assertEquals(1, chain.size());
    }

    @Test
    void annulBlockTwiceThrowsAlreadyAnnulled() {
        chain.addBlock("A");
        chain.annulBlock(1);

        BlockAlreadyAnnulledException ex =
                assertThrows(BlockAlreadyAnnulledException.class, () -> chain.annulBlock(1));

        assertEquals(String.format(BlockchainExceptions.BLOCK_ALREADY_ANNULLED, 1), ex.getMessage());
        assertEquals(2, chain.size());
    }

    @Test
    void annulBlockCannotAnnulAnAnnulmentBlock() {
        chain.addBlock("A");
        Bloque annulment = chain.annulBlock(1);

        assertThrows(BlockAlreadyAnnulledException.class, () -> chain.annulBlock(annulment.getId()));
    }

    @Test
    void annulBlockCanAnnulACorrectionBlock() {
        chain.addBlock("A");
        Bloque correction = chain.rectifyBlock(1, "A2");

        Bloque annulment = chain.annulBlock(correction.getId());

        assertEquals(BlockType.ANNULMENT, annulment.getType());
        assertEquals(correction.getId(), annulment.getReferencedBlockId());
    }

    @Test
    void validateAcceptsEmptyChain() {
        assertDoesNotThrow(chain::validate);
    }

    @Test
    void validateAcceptsChainBuiltThroughPublicOperations() {
        chain.addBlock("A");
        chain.addBlock("B");
        chain.rectifyBlock(2, "B2");
        chain.annulBlock(1);

        assertDoesNotThrow(chain::validate);
    }

    @Test
    void validateDetectsTamperedHash() throws ReflectiveOperationException {
        chain.addBlock("A");
        Bloque second = chain.addBlock("B");
        chain.addBlock("C");
        tamper(second, "hash", "deadbeef");

        InvalidChainException ex = assertThrows(InvalidChainException.class, chain::validate);

        assertEquals(2, ex.getInvalidBlockId());
        assertEquals(String.format(BlockchainExceptions.BROKEN_CHAIN, 2), ex.getMessage());
    }

    @Test
    void validateDetectsBrokenLinkToPreviousBlock() throws ReflectiveOperationException {
        chain.addBlock("A");
        chain.addBlock("B");
        Bloque third = chain.addBlock("C");
        tamper(third, "previousHash", "forged");

        InvalidChainException ex = assertThrows(InvalidChainException.class, chain::validate);

        assertEquals(3, ex.getInvalidBlockId());
    }

    @Test
    void validateDetectsGenesisWithPreviousHash() throws ReflectiveOperationException {
        Bloque genesis = chain.addBlock("A");
        tamper(genesis, "previousHash", "unexpected");

        InvalidChainException ex = assertThrows(InvalidChainException.class, chain::validate);

        assertEquals(1, ex.getInvalidBlockId());
    }

    @Test
    void validateReportsFirstBrokenBlock() throws ReflectiveOperationException {
        chain.addBlock("A");
        Bloque second = chain.addBlock("B");
        Bloque third = chain.addBlock("C");
        tamper(third, "hash", "bad-3");
        tamper(second, "hash", "bad-2");

        InvalidChainException ex = assertThrows(InvalidChainException.class, chain::validate);

        assertEquals(2, ex.getInvalidBlockId());
    }

    private static void tamper(Bloque block, String field, Object value) throws ReflectiveOperationException {
        Field f = Bloque.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(block, value);
    }
}
