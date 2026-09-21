# BlockchainTaller

Blockchain simulation built on a **singly linked list** (Data Structures workshop, Universidad de Cartagena).
Each node of the list is a block that stores an id, the transaction data, the hash of the previous block and
a pointer to the next block. The program offers two front ends over the same core, chosen from the terminal
at startup:

1. **Terminal** (plain Java, `Scanner`)
2. **Desktop** (JavaFX)

## Requirements

- JDK 21+
- Maven 3.9+
- JavaFX is downloaded by Maven (`org.openjfx`, version in `javafx.version`); no manual install needed.

> The project is configured for JDK 21 (`maven.compiler.release` = `21`) and JavaFX 21.0.5
> (`javafx.version`) in `pom.xml`. To use a newer JavaFX (e.g. 25, which requires JDK 23+), raise both
> properties.

## Build and test

```bash
mvn clean test
mvn clean package
```

## Run

`blockchain.BlockchainApplication` accepts an optional mode:

| Mode | What it does |
|---|---|
| `gui` | JavaFX desktop UI only |
| `cli` | terminal only |
| `menu` (default) | asks which front end to open |

- **IDE (recommended):** run `blockchain.BlockchainApplication` with the mode as program argument.
  Terminal mode reads from standard input, so it needs a real console.
- **Maven:** `mvn javafx:run -Djavafx.args=gui` (or `cli`, `menu`). If terminal input does not reach the
  program through Maven, use the IDE.

The desktop UI draws the chain as linked nodes (`head`, `tail`, `size`) that wrap into a zigzag, with a
stats strip, live search (text / ID / hash), toast notifications and modal dialogs. Click a block to see its
data, hashes and relations, copy hashes, jump to the previous/next block and rectify or annul it.
Shortcuts: `←`/`→` navigate, `Esc` close, `Ctrl+F` search, `Ctrl+N` new block, `Ctrl+L` validate.

## Architecture

Layered / hexagonal structure under `core/chain`. One folder per layer, one folder per use case.

```
blockchain/
├── BlockchainApplication.java      entry point: mode menu (terminal / desktop) + wiring
└── core/chain/
    ├── domain/
    │   ├── entity/                 Bloque, Blockchain
    │   ├── enums/                  BlockType, BlockSearchCriteria
    │   ├── exceptions/             BlockchainExceptions + specific exceptions
    │   ├── inputports/             *UseCase interfaces
    │   ├── outputports/            BlockchainRepositoryPort
    │   └── structure/              Node, SimpleLinkedList (adapted from the professor's implementation)
    ├── application/
    │   ├── <use_case>/dtos/        *Command, *Response
    │   ├── <use_case>/service/     *Service (implements the use case)
    │   └── dtos/                   BlockInfo
    ├── infrastructure/
    │   ├── adapter/repository/     InMemoryBlockchainRepositoryAdapter
    │   └── config/                 BlockchainConfiguration (manual wiring)
    └── presentation/
        ├── cli/                    console controller, formatter, workshop demo
        └── javafx/                 FxApp, controllers, components
```

Dependency rule: `presentation -> application -> domain`; `infrastructure` implements the output ports of
`domain`. Nothing outside `presentation/javafx` imports `javafx.*`.

Naming: everything is in English except what the workshop requires in Spanish (the `Bloque` class and the
console output labels: `Bloque N:`, `Datos:`, `Hash anterior:`, `Hash actual:`, `None`).

## Workshop requirements

| Workshop asks for | In the code |
|---|---|
| Clase Bloque | `Bloque` (extends `Node<String>`) |
| Clase Blockchain | `Blockchain` |
| Block attributes | `id`, `data`, `previousHash`, `hash`, `next` |
| Simulated hash | `Bloque.calculateHash()` (`Objects.hash`, `next` excluded) |
| `add_block(datos)` | `Blockchain.addBlock(data)` |
| `list_blockchain()` | `Blockchain.listBlockchain()` |
| `search_block(hash)` | `Blockchain.searchBlock(hash)` |
| Main with at least 3 blocks | `TallerDemo` |
| CRUD-S: Create / Read / Search | `AddBlock`, `ListBlocks`, `SearchBlock` use cases |
| CRUD-S: Update / Delete (immutability) | `RectifyBlock` / `AnnulBlock` append a correction / annulment block |
| Integrity check | `ValidateChain` use case |

## Changes over the original linked list

Base: `DataStructures/LinkedList` (`Nodo<T>`, `Lista<T>`, package `co.edu.unicartagena.list`).

| Original | Problem | Change |
|---|---|---|
| `Nodo` with `dato`, `sig` | Package-private fields, no getter for `sig` | `Node<T>` with private `data`, `next`; public `getNext()`, list-only `setNext()` |
| `adicionarFinal` | Walks the whole list on every append | `addLast` with a `tail` pointer (O(1)) |
| `adicionarEntreNodos` | `Integer.parseInt(toString())`, numbers only; inserting in the middle would break hashes | Removed |
| `eliminar` | Does not decrement `tamaño`, NPE on empty list, debug `println` | `removeFirst` fixed (not exposed by `Blockchain`) |
| `imprimir` | The data structure prints to the console | Removed; `SimpleLinkedList` is `Iterable<Node<T>>`, `presentation` prints |
| private `estaVacia`, `tamaño` | No public way to read the state | Public `isEmpty()`, `size()` |
| (missing) | `searchBlock` needs a lookup | `find(Predicate<Node<T>>)`, `getLast()` |

## Status

- [ ] Phase 1: adapt `Node` / `SimpleLinkedList`, `Bloque`, `Blockchain`, `TallerDemo`
- [ ] Phase 2: CRUD-S use cases, `ValidateChain`, unit tests
- [ ] Phase 3: terminal front end
- [ ] Phase 4: JavaFX front end
- [ ] Phase 5: extras (persistence, tamper demo) and final documentation
