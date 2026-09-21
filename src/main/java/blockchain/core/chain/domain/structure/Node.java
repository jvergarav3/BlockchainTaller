package blockchain.core.chain.domain.structure;

public class Node<T> {

    private final T data;
    private Node<T> next;

    public Node(T data) {
        this(data, null);
    }

    public Node(T data, Node<T> next) {
        this.data = data;
        this.next = next;
    }

    public T getData() { return data; }

    public Node<T> getNext() { return next; }

    void setNext(Node<T> next) { this.next = next; }
}
