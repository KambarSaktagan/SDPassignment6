package edu.aitu.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.FileNode;
import edu.aitu.visitor.visitor.SearchVisitor;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SearchVisitorTest {
    private DirectoryNode fixture() {
        DirectoryNode root = new DirectoryNode("workspace");
        DirectoryNode z = new DirectoryNode("z");
        z.addChild(new FileNode("big.log", 700));
        z.addChild(new FileNode("small.log", 99));
        DirectoryNode a = new DirectoryNode("a");
        a.addChild(new FileNode("exact.log", 100));
        a.addChild(new FileNode("other.txt", 900));
        root.addChild(z); // deliberately unsorted insertion
        root.addChild(a);
        root.addChild(new FileNode("top.log", 1_000));
        return root;
    }

    @Test
    void combinesFilenameAndInclusiveSizeThreshold() {
        assertEquals(List.of("a/exact.log", "top.log", "z/big.log"),
                new SearchVisitor(name -> name.endsWith(".log"), 100).search(fixture()));
    }

    @Test
    void returnsRootRelativeSlashSeparatedLexicalPaths() {
        assertEquals(List.of("a/exact.log", "top.log", "z/big.log", "z/small.log"),
                new SearchVisitor(name -> name.endsWith(".log"), 0).search(fixture()));
    }

    @Test
    void noMatchEmptyDirectoryAndFileRootWork() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".pdf"), 0);
        assertEquals(List.of(), visitor.search(fixture()));
        assertEquals(List.of(), visitor.search(new DirectoryNode("empty")));
        assertEquals(List.of("paper.pdf"), visitor.search(new FileNode("paper.pdf", 0)));
    }

    @Test
    void repeatSearchResetsResultsAndReturnsImmutableSnapshot() {
        SearchVisitor visitor = new SearchVisitor(name -> name.contains("log"), 100);
        List<String> first = visitor.search(fixture());
        assertEquals(first, visitor.search(fixture()));
        assertEquals(List.of(), visitor.search(new DirectoryNode("empty")));
        assertEquals(List.of("a/exact.log", "top.log", "z/big.log"), first);
        assertThrows(UnsupportedOperationException.class, () -> first.add("injected"));
    }

    @Test
    void rejectsInvalidSearchConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(s -> true, -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }
}
