package blockchain.core.chain.domain.structure;
/*
Joan Sebastian Vergara Valencia - 6902510055
Dylan Mayol Puertas Girón - 6902510052
Oscar David Tapias - 6902420043
*/
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
