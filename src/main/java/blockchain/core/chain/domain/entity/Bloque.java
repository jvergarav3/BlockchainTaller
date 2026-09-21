package blockchain.core.chain.domain.entity;

import blockchain.core.chain.domain.enums.BlockType;
import blockchain.core.chain.domain.structure.Node;

import java.util.Objects;

public class Bloque extends Node<String> {

    private final int id;
    private final String previousHash;
    private final String hash;
    private final BlockType type;
    private final Integer referencedBlockId;

    public Bloque(int id, String data, String previousHash, BlockType type, Integer referencedBlockId) {
        super(data);
        this.id = id;
        this.previousHash = previousHash;
        this.type = type;
        this.referencedBlockId = referencedBlockId;
        this.hash = hashOf(id, data, previousHash, type, referencedBlockId);
    }

    public String calculateHash() {
        return hashOf(id, getData(), previousHash, type, referencedBlockId);
    }

    private static String hashOf(int id, String data, String previousHash, BlockType type, Integer referencedBlockId) {
        return Integer.toHexString(Objects.hash(id, data, previousHash, type.name(), referencedBlockId));
    }

    public int getId() {
        return id;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public BlockType getType() {
        return type;
    }

    public Integer getReferencedBlockId() {
        return referencedBlockId;
    }
}
