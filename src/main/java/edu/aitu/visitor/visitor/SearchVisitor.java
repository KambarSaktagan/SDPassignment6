package edu.aitu.visitor.visitor;

import edu.aitu.visitor.model.Element;
import edu.aitu.visitor.model.FileNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor extends AbstractTreeVisitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumBytes;
    private final List<String> matches = new ArrayList<>();

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumBytes) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "filenameCriterion");
        if (minimumBytes < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        this.minimumBytes = minimumBytes;
    }

    public List<String> search(Element root) {
        traverse(root);
        matches.sort(String::compareTo);
        return List.copyOf(matches);
    }

    @Override
    protected void resetResults() {
        matches.clear();
    }

    @Override
    protected void onFile(FileNode file, String path, int depth) {
        if (file.getSizeBytes() >= minimumBytes && filenameCriterion.test(file.getName())) {
            matches.add(path);
        }
    }
}
