package edu.aitu.visitor.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.Element;
import edu.aitu.visitor.model.FileNode;

public final class StatisticsVisitor extends AbstractTreeVisitor {
    public record Statistics(int fileCount, int directoryCount, int maxDepth) { }

    private int files;
    private int directories;
    private int maxDepth;

    public Statistics analyze(Element root) {
        traverse(root);
        return new Statistics(files, directories, maxDepth);
    }

    @Override
    protected void resetResults() {
        files = 0;
        directories = 0;
        maxDepth = 0;
    }

    @Override
    protected void onFile(FileNode file, String path, int depth) {
        files++;
        maxDepth = Math.max(maxDepth, depth);
    }

    @Override
    protected void onDirectory(DirectoryNode directory, String path, int depth) {
        directories++;
        maxDepth = Math.max(maxDepth, depth);
    }
}
