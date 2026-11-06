package edu.aitu.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.FileNode;
import edu.aitu.visitor.visitor.TotalSizeVisitor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TotalSizeVisitorTest {
    @Test
    void sumsFilesAtMultipleLevelsExactlyOnce() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode inner = new DirectoryNode("inner");
        DirectoryNode deep = new DirectoryNode("deep");
        deep.addChild(new FileNode("third", 303));
        inner.addChild(new FileNode("second", 202));
        inner.addChild(deep);
        root.addChild(new FileNode("first", 101));
        root.addChild(inner);
        assertEquals(606, new TotalSizeVisitor().calculate(root));
    }

    @Test
    void supportsEmptyDirectoryAndFileRoot() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        assertEquals(0, visitor.calculate(new DirectoryNode("empty")));
        assertEquals(44, visitor.calculate(new FileNode("alone", 44)));
    }

    @Test
    void repeatedRunsResetTotalsAndDoNotDependOnRunOrder() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("x", 35));
        assertEquals(35, visitor.calculate(root));
        assertEquals(35, visitor.calculate(root));
        assertEquals(2, visitor.calculate(new FileNode("other", 2)));
        assertEquals(35, visitor.calculate(root));
    }

    @Test
    void overflowIsRejectedRatherThanWrapping() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("one", Long.MAX_VALUE));
        root.addChild(new FileNode("two", 1));
        assertThrows(ArithmeticException.class, () -> new TotalSizeVisitor().calculate(root));
    }
}
