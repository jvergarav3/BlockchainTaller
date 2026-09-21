package blockchain.core.chain.domain;

import blockchain.core.chain.domain.structure.Node;
import blockchain.core.chain.domain.structure.SimpleLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleLinkedListTest {

    private SimpleLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new SimpleLinkedList<>();
    }

    @Test
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertNull(list.getFirst());
        assertNull(list.getLast());
    }

    @Test
    void addLastOnEmptyListSetsFirstAndLastToSameNode() {
        Node<String> node = new Node<>("A");

        list.addLast(node);

        assertSame(node, list.getFirst());
        assertSame(node, list.getLast());
        assertEquals(1, list.size());
    }

    @Test
    void addLastKeepsInsertionOrder() {
        Node<String> a = new Node<>("A");
        Node<String> b = new Node<>("B");
        Node<String> c = new Node<>("C");

        list.addLast(a);
        list.addLast(b);
        list.addLast(c);

        assertSame(a, list.getFirst());
        assertSame(c, list.getLast());
        assertSame(b, a.getNext());
        assertSame(c, b.getNext());
        assertNull(c.getNext());
        assertEquals(3, list.size());
    }

    @Test
    void addFirstOnEmptyListAlsoSetsLast() {
        Node<String> node = new Node<>("A");

        list.addFirst(node);

        assertSame(node, list.getFirst());
        assertSame(node, list.getLast());
        assertEquals(1, list.size());
    }

    @Test
    void removeFirstReturnsFirstNodeAndDecreasesSize() {
        Node<String> a = new Node<>("A");
        Node<String> b = new Node<>("B");
        list.addLast(a);
        list.addLast(b);

        Node<String> removed = list.removeFirst();

        assertSame(a, removed);
        assertEquals(1, list.size());
        assertSame(b, list.getFirst());
    }

    @Test
    void removeFirstOnSingleNodeListLeavesLastNull() {
        list.addLast(new Node<>("A"));

        list.removeFirst();

        assertTrue(list.isEmpty());
        assertNull(list.getFirst());
        assertNull(list.getLast());
    }

    @Test
    void removeFirstOnEmptyListThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
    }

    @Test
    void findReturnsFirstNodeThatMatches() {
        Node<String> a = new Node<>("A");
        Node<String> b = new Node<>("B");
        Node<String> c = new Node<>("C");
        list.addLast(a);
        list.addLast(b);
        list.addLast(c);

        Node<String> found = list.find(n -> n.getData().equals("B"));

        assertSame(b, found);
    }

    @Test
    void findReturnsNullWhenNothingMatches() {
        list.addLast(new Node<>("A"));
        list.addLast(new Node<>("B"));

        Node<String> found = list.find(n -> n.getData().equals("Z"));

        assertNull(found);
    }

    @Test
    void iteratorTraversesNodesInOrder() {
        list.addLast(new Node<>("A"));
        list.addLast(new Node<>("B"));
        list.addLast(new Node<>("C"));
        List<String> seen = new ArrayList<>();

        for (Node<String> node : list) {
            seen.add(node.getData());
        }

        assertEquals(List.of("A", "B", "C"), seen);
    }

    @Test
    void iteratorOnEmptyListHasNoElements() {
        List<String> seen = new ArrayList<>();

        for (Node<String> node : list) {
            seen.add(node.getData());
        }

        assertTrue(seen.isEmpty());
    }
}
