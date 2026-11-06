package edu.aitu.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.FileNode;
import edu.aitu.visitor.model.Element;
import edu.aitu.visitor.visitor.TotalSizeVisitor;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TreeIntegrityTest {
    @Test
    void rejectsNegativeFileSizes() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad", -1));
        assertEquals(0, new FileNode("zero", 0).getSizeBytes());
    }

    @Test
    void rejectsDuplicateSiblingNamesWithoutChangingTree() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("same", 10));
        assertThrows(IllegalArgumentException.class, () -> root.addChild(new DirectoryNode("same")));
        assertEquals(1, root.getChildren().size());
    }

    @Test
    void rejectsMultipleParentOwnership() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode shared = new FileNode("shared", 8);
        first.addChild(shared);
        assertThrows(IllegalArgumentException.class, () -> second.addChild(shared));
        assertEquals(8, new TotalSizeVisitor().calculate(first));
    }

    @Test
    void rejectsDirectAndIndirectCycles() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode middle = new DirectoryNode("middle");
        DirectoryNode leaf = new DirectoryNode("leaf");
        root.addChild(middle);
        middle.addChild(leaf);
        assertThrows(IllegalArgumentException.class, () -> root.addChild(root));
        assertThrows(IllegalArgumentException.class, () -> leaf.addChild(root));
    }

    @Test
    void allowsValidArbitraryNestingAndReparentAfterRemoval() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        DirectoryNode nested = new DirectoryNode("nested");
        nested.addChild(new FileNode("ok", 30));
        first.addChild(nested);
        Element removed = first.removeChild("nested");
        assertSame(nested, removed);
        second.addChild(removed);
        assertEquals(30, new TotalSizeVisitor().calculate(second));
        assertEquals(0, new TotalSizeVisitor().calculate(first));
    }

    @Test
    void childrenAreUnmodifiableAndSorted() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("z", 1));
        root.addChild(new FileNode("a", 2));
        List<Element> children = root.getChildren();
        assertEquals(List.of("a", "z"), children.stream().map(Element::getName).toList());
        assertThrows(UnsupportedOperationException.class, () -> children.clear());
    }

    @Test
    void rejectsAmbiguousNamesAndNullChild() {
        for (String bad : List.of("", "   ", ".", "..", "a/b", "a\\b")) {
            assertThrows(IllegalArgumentException.class, () -> new FileNode(bad, 1));
        }
        assertThrows(NullPointerException.class, () -> new DirectoryNode("root").addChild(null));
    }
}
