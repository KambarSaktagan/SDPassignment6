package edu.aitu.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.FileNode;
import edu.aitu.visitor.visitor.Visitor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VisitorDispatchTest {
    @Test
    void eachElementSelectsItsOwnOverloadedVisitMethod() {
        class CountingVisitor implements Visitor {
            int fileVisits;
            int directoryVisits;
            public void visit(FileNode file) { fileVisits++; }
            public void visit(DirectoryNode directory) { directoryVisits++; }
        }
        CountingVisitor visitor = new CountingVisitor();
        new FileNode("hello.txt", 10).accept(visitor);
        new DirectoryNode("folder").accept(visitor);
        assertEquals(1, visitor.fileVisits);
        assertEquals(1, visitor.directoryVisits);
    }
}
