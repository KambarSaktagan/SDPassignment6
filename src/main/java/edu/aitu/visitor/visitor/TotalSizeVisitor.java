package edu.aitu.visitor.visitor;

import edu.aitu.visitor.model.Element;
import edu.aitu.visitor.model.FileNode;

public final class TotalSizeVisitor extends AbstractTreeVisitor {
    private long totalBytes;

    public long calculate(Element root) {
        traverse(root);
        return totalBytes;
    }

    @Override
    protected void resetResults() {
        totalBytes = 0;
    }

    @Override
    protected void onFile(FileNode file, String relativePath, int depth) {
        totalBytes = Math.addExact(totalBytes, file.getSizeBytes());
    }
}
