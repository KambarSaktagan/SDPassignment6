package edu.aitu.visitor.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.Element;
import edu.aitu.visitor.model.FileNode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Single traversal owner for every operation. Each concrete visitor only handles
 * the data it wants to collect; this class walks every child once.
 */
public abstract class AbstractTreeVisitor implements Visitor {
    private final Deque<String> segments = new ArrayDeque<>();
    private int depth;
    private boolean running;

    protected final void traverse(Element root) {
        Objects.requireNonNull(root, "root");
        if (running) {
            throw new IllegalStateException("Cannot run the same visitor recursively");
        }
        running = true;
        segments.clear();
        depth = 0;
        try {
            resetResults();
            root.accept(this);
        } finally {
            segments.clear();
            depth = 0;
            running = false;
        }
    }

    protected abstract void resetResults();

    protected void onFile(FileNode file, String relativePath, int nodeDepth) { }

    protected void onDirectory(DirectoryNode directory, String relativePath, int nodeDepth) { }

    @Override
    public final void visit(FileNode file) {
        // Root-file paths consist of the filename; directory-root paths exclude the root name.
        String path = segments.isEmpty() ? file.getName() : String.join("/", segments);
        onFile(file, path, depth);
    }

    @Override
    public final void visit(DirectoryNode directory) {
        onDirectory(directory, String.join("/", segments), depth);
        for (Element child : directory.getChildren()) {
            segments.addLast(child.getName());
            depth++;
            try {
                child.accept(this);
            } finally {
                depth--;
                segments.removeLast();
            }
        }
    }
}
