package edu.aitu.visitor.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.FileNode;

public interface Visitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}
