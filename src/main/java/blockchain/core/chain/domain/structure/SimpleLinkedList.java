package blockchain.core.chain.domain.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public class SimpleLinkedList<T> implements Iterable<Node<T>> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public SimpleLinkedList() {
        clear();
    }

    private void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(Node<T> node) {
        Objects.requireNonNull(node, "node must not be null");
        node.setNext(head);
        head = node;
        if (tail == null) {
            tail = node;
        }
        size++;
    }

    public void addLast(Node<T> node) {
        Objects.requireNonNull(node, "node must not be null");
        node.setNext(null);
        if (isEmpty()) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
        size++;
    }

    public Node<T> removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("The list is empty");
        }
        Node<T> removed = head;
        head = head.getNext();
        removed.setNext(null);
        size--;
        if (head == null) {
            tail = null;
        }
        return removed;
    }

    public Node<T> getFirst() {
        return head;
    }

    public Node<T> getLast() {
        return tail;
    }

    public Node<T> find(Predicate<Node<T>> condition) {
        Node<T> current = head;
        while (current != null) {
            if (condition.test(current)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    @Override
    public Iterator<Node<T>> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Node<T> next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                Node<T> node = current;
                current = current.getNext();
                return node;
            }
        };
    }
}
