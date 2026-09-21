package blockchain.core.chain.domain;

import blockchain.core.chain.domain.entity.Bloque;
import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.structure.SimpleLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BloqueTest {

    @Test
    void constructorStoresAllAttributes() {
        Bloque block = new Bloque(2, "data", "prev", BlockType.CORRECTION, 1);

        assertEquals(2, block.getId());
        assertEquals("data", block.getData());
        assertEquals("prev", block.getPreviousHash());
        assertEquals(BlockType.CORRECTION, block.getType());
        assertEquals(1, block.getReferencedBlockId());
        assertNull(block.getNext());
    }

    @Test
    void genesisBlockHasNoPreviousHashNorReference() {
        Bloque block = new Bloque(1, "genesis", null, BlockType.NORMAL, null);

        assertNull(block.getPreviousHash());
        assertNull(block.getReferencedBlockId());
    }

    @Test
    void hashIsComputedAtCreationAndMatchesCalculateHash() {
        Bloque block = new Bloque(1, "data", null, BlockType.NORMAL, null);

        assertNotNull(block.getHash());
        assertFalse(block.getHash().isBlank());
        assertEquals(block.getHash(), block.calculateHash());
    }

    @Test
    void hashIsDeterministic() {
        Bloque a = new Bloque(1, "data", "prev", BlockType.NORMAL, null);
        Bloque b = new Bloque(1, "data", "prev", BlockType.NORMAL, null);

        assertEquals(a.getHash(), b.getHash());
    }

    @Test
    void hashChangesWhenIdChanges() {
        Bloque a = new Bloque(1, "data", "prev", BlockType.NORMAL, null);
        Bloque b = new Bloque(2, "data", "prev", BlockType.NORMAL, null);

        assertNotEquals(a.getHash(), b.getHash());
    }

    @Test
    void hashChangesWhenDataChanges() {
        Bloque a = new Bloque(1, "data", "prev", BlockType.NORMAL, null);
        Bloque b = new Bloque(1, "other", "prev", BlockType.NORMAL, null);

        assertNotEquals(a.getHash(), b.getHash());
    }

    @Test
    void hashChangesWhenPreviousHashChanges() {
        Bloque a = new Bloque(1, "data", "prev-1", BlockType.NORMAL, null);
        Bloque b = new Bloque(1, "data", "prev-2", BlockType.NORMAL, null);

        assertNotEquals(a.getHash(), b.getHash());
    }

    @Test
    void hashChangesWhenTypeChanges() {
        Bloque a = new Bloque(1, "data", "prev", BlockType.NORMAL, null);
        Bloque b = new Bloque(1, "data", "prev", BlockType.ANNULMENT, null);

        assertNotEquals(a.getHash(), b.getHash());
    }

    @Test
    void hashChangesWhenReferencedBlockChanges() {
        Bloque a = new Bloque(3, "data", "prev", BlockType.CORRECTION, 1);
        Bloque b = new Bloque(3, "data", "prev", BlockType.CORRECTION, 2);

        assertNotEquals(a.getHash(), b.getHash());
    }

    @Test
    void hashDoesNotDependOnNextPointer() {
        Bloque first = new Bloque(1, "A", null, BlockType.NORMAL, null);
        String before = first.getHash();

        SimpleLinkedList<String> list = new SimpleLinkedList<>();
        list.addLast(first);
        list.addLast(new Bloque(2, "B", before, BlockType.NORMAL, null));

        assertNotNull(first.getNext());
        assertEquals(before, first.getHash());
        assertEquals(before, first.calculateHash());
    }
}
