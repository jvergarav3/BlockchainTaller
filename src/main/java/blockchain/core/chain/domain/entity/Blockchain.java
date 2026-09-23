package blockchain.core.chain.domain.entity;

import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.exceptions.BlockchainExceptions;
import blockchain.core.chain.domain.structure.Node;
import blockchain.core.chain.domain.structure.SimpleLinkedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
public class Blockchain {

    private static final String ANNULMENT_DATA = "Annulment of block %d";

    private final SimpleLinkedList<String> blocks = new SimpleLinkedList<>();

    public Bloque addBlock(String data) {
        requireData(data);
        return append(data, BlockType.NORMAL, null);
    }

    public List<Bloque> listBlockchain() {
        List<Bloque> result = new ArrayList<>();
        for (Node<String> node : blocks) {
            result.add(cast(node));
        }
        return Collections.unmodifiableList(result);
    }

    public Optional<Bloque> searchBlock(String hash) {
        Node<String> found = blocks.find(node -> cast(node).getHash().equals(hash));
        return Optional.ofNullable(found).map(Blockchain::cast);
    }

    public Optional<Bloque> findById(int id) {
        Node<String> found = blocks.find(node -> cast(node).getId() == id);
        return Optional.ofNullable(found).map(Blockchain::cast);
    }

    public List<Bloque> findByText(String text) {
        List<Bloque> matches = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return Collections.unmodifiableList(matches);
        }
        String needle = text.toLowerCase(Locale.ROOT);
        for (Node<String> node : blocks) {
            Bloque block = cast(node);
            if (block.getData().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(block);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public Bloque rectifyBlock(int id, String newData) {
        requireData(newData);
        findById(id).orElseThrow(() -> BlockchainExceptions.blockNotFound(id));
        return append(newData, BlockType.CORRECTION, id);
    }

    public Bloque annulBlock(int id) {
        Bloque target = findById(id).orElseThrow(() -> BlockchainExceptions.blockNotFound(id));
        if (target.getType() == BlockType.ANNULMENT || isAnnulled(id)) {
            throw BlockchainExceptions.blockAlreadyAnnulled(id);
        }
        return append(String.format(ANNULMENT_DATA, id), BlockType.ANNULMENT, id);
    }

    public void validate() {
        Bloque previous = null;
        for (Node<String> node : blocks) {
            Bloque block = cast(node);
            String expectedPreviousHash = previous == null ? null : previous.getHash();
            boolean hashMatches = block.getHash().equals(block.calculateHash());
            boolean linkMatches = Objects.equals(block.getPreviousHash(), expectedPreviousHash);
            if (!hashMatches || !linkMatches) {
                throw BlockchainExceptions.brokenChain(block.getId());
            }
            previous = block;
        }
    }

    public int size() {
        return blocks.size();
    }

    public Bloque getLast() {
        Node<String> last = blocks.getLast();
        return last == null ? null : cast(last);
    }

    private Bloque append(String data, BlockType type, Integer referencedBlockId) {
        Bloque last = getLast();
        String previousHash = last == null ? null : last.getHash();
        Bloque block = new Bloque(blocks.size() + 1, data, previousHash, type, referencedBlockId);
        blocks.addLast(block);
        return block;
    }

    private boolean isAnnulled(int id) {
        Node<String> found = blocks.find(node -> {
            Bloque block = cast(node);
            return block.getType() == BlockType.ANNULMENT && Objects.equals(block.getReferencedBlockId(), id);
        });
        return found != null;
    }

    private void requireData(String data) {
        if (data == null || data.isBlank()) {
            throw BlockchainExceptions.emptyData();
        }
    }

    private static Bloque cast(Node<String> node) {
        return (Bloque) node;
    }
}
