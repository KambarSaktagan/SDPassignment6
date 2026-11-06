# Assignment 06 — Visitor Pattern: Advanced File System

Java 17 Maven application implementing an in-memory file tree and the Visitor behavioral pattern.

## Run

Requirements: JDK 17+ and Apache Maven 3.9+.

From the **project root**:

```bash
mvn test
mvn compile exec:java
```

The demo data is created in `Main.java`; no user input or filesystem access is required.

Expected demo output:

```text
Visitor Pattern - Advanced File System
Total size: 5920 bytes
Search .java files (>= 2000 bytes): [src/App.java]
Statistics: files=5, directories=4, maxDepth=2
```

## Project layout

```text
pom.xml
src/
  main/java/edu/aitu/visitor/
    model/
      Element.java
      AbstractElement.java
      FileNode.java
      DirectoryNode.java
    visitor/
      Visitor.java
      AbstractTreeVisitor.java
      TotalSizeVisitor.java
      SearchVisitor.java
      StatisticsVisitor.java
    app/Main.java
  test/java/edu/aitu/visitor/
    VisitorDispatchTest.java
    TotalSizeVisitorTest.java
    SearchVisitorTest.java
    StatisticsVisitorTest.java
    TreeIntegrityTest.java
```

## How the Visitor pattern works

`Element` defines `accept(Visitor)`. `FileNode.accept(visitor)` calls `visitor.visit(this)` with a **FileNode** argument; `DirectoryNode.accept(visitor)` does the same with a **DirectoryNode** argument. This is double dispatch: the actual element receives `accept` first, and its concrete type determines which overloaded `visit` method is called second.

The three operations are independent:

- `TotalSizeVisitor.calculate(root)` totals file bytes, using `Math.addExact` to reject overflow.
- `SearchVisitor(filenamePredicate, minimumBytes).search(root)` returns filenames matching both conditions, with an inclusive size threshold.
- `StatisticsVisitor.analyze(root)` reports files, directories and maximum depth.

## Traversal, paths and visitor state

**Traversal owner:** `AbstractTreeVisitor` is the only component that recursively walks the tree. Its final `visit(DirectoryNode)` handles children; its final `visit(FileNode)` handles leaves. Each operation implements `onDirectory`/`onFile` hooks without traversing again. Children are stored in a `TreeMap` (sorted by name) and each reachable element is visited exactly once per operation.

**Paths:** for a directory root named `project`, a file under `src` has path `src/App.java` (the root directory's own name is excluded). A standalone file used as the root has path equal to its filename. Paths always use `/`. Search results are explicitly sorted lexicographically and returned as an immutable list.

**Depth:** root depth is zero. Each traversed parent-to-child edge increases depth by one. The maximum over all files and directories is recorded, including empty directories and the selected root.

**State:** visitors can be **reused sequentially**. Every public `calculate`, `search`, or `analyze` starts a fresh traversal, which clears prior results and traversal context. They are **not thread-safe** and must not be shared across concurrent calls. Output never depends on operation order. The result returned by `StatisticsVisitor` is an immutable record and search returns an immutable snapshot.

## Tree integrity

`FileNode` rejects negative sizes. `AbstractElement` validates element names, disallowing blank names, `.` and `..`, and path separators. `DirectoryNode.addChild` prevents duplicate sibling names, multiple parents and ancestor/self cycles. `removeChild` releases ownership so a detached child can be moved safely. Callers get an unmodifiable snapshot of sorted children, not a writable internal collection.

## Extensibility and trade-off

`StatisticsVisitor` is a separate class. Adding it **does not require changing `FileNode`, `DirectoryNode`, or their `accept` methods** because they depend only on the original `Visitor` interface. This is the main extension benefit: new operations can be introduced without changing the file-system model. In contrast, adding a **new element type** would require a new overload in `Visitor` and changes to its implementations or common dispatch base, which is a Visitor-pattern trade-off.

The code uses no `instanceof`, reflection or `getClass()` behavior selection. `DirectoryNode` does cast `Element` to its shared `AbstractElement` base for parent bookkeeping only; it does not decide behavior based on whether an object is a file or a directory.

## Five Clean Code choices

1. **Meaningful names:** `calculate`, `search`, `analyze`, `minimumBytes` and `maxDepth` express intent directly.
2. **Single Responsibility:** `FileNode` stores file metadata; `DirectoryNode` enforces tree ownership; each concrete visitor implements one independent operation.
3. **Small methods:** `SearchVisitor.onFile` only checks two conditions and records a matching path; traversal is delegated to `AbstractTreeVisitor`.
4. **Encapsulation:** `DirectoryNode.getChildren()` returns an unmodifiable copy; every structural mutation passes through checked methods.
5. **No type-checking chains:** overloaded `Visitor.visit(FileNode)` and `Visitor.visit(DirectoryNode)` select element behavior using double dispatch, not conditional type tests.

## Tests

Five JUnit 5 test classes contain **20 automated tests** covering:

| Check | Tests |
| --- | --- |
| T1 double dispatch | `VisitorDispatchTest` |
| T2 multi-level total sizes | `TotalSizeVisitorTest` |
| T3 combined search criterion and inclusive threshold | `SearchVisitorTest` |
| T4 deterministic root-relative paths | `SearchVisitorTest` |
| T5 counts and maximum depth | `StatisticsVisitorTest` |
| T6 empty trees, standalone files and no matches | size/search/statistics test classes |
| T7 repeat-run state reset | size/search/statistics test classes |
| T8 ownership, names, duplicates, cycles, valid reparenting | `TreeIntegrityTest` |

Run with `mvn test`. Maven downloads JUnit and test plugins automatically on first use when Internet access is available.

## For the assignment defense

Trace `FileNode.accept` -> `visitor.visit(this)` -> `AbstractTreeVisitor.visit(FileNode)` -> operation-specific `onFile`, and likewise for directories. Be ready to change the predicate/size threshold in `Main`, rerun the demo, run `mvn test`, and explain the Visitor trade-off.

**Academic integrity:** review and understand every class, customize the demo and tests yourself, and disclose any AI assistance as required by your course policy before submitting.
