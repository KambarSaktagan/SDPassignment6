package edu.aitu.visitor;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.FileNode;
import edu.aitu.visitor.visitor.StatisticsVisitor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsVisitorTest {
    @Test
    void countsRootDirectoriesFilesAndDeepestLevel() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode inner = new DirectoryNode("inner");
        DirectoryNode empty = new DirectoryNode("empty");
        inner.addChild(new FileNode("deep.txt", 0));
        root.addChild(inner);
        root.addChild(empty);
        root.addChild(new FileNode("top.txt", 8));
        assertEquals(new StatisticsVisitor.Statistics(2, 3, 2),
                new StatisticsVisitor().analyze(root));
    }

    @Test
    void rootDepthIsZeroForEmptyDirectoryAndStandaloneFile() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        assertEquals(new StatisticsVisitor.Statistics(0, 1, 0), visitor.analyze(new DirectoryNode("empty")));
        assertEquals(new StatisticsVisitor.Statistics(1, 0, 0), visitor.analyze(new FileNode("solo", 5)));
    }

    @Test
    void repeatedAnalysisDoesNotAccumulateCounts() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new DirectoryNode("sub"));
        assertEquals(new StatisticsVisitor.Statistics(0, 2, 1), visitor.analyze(root));
        assertEquals(new StatisticsVisitor.Statistics(0, 2, 1), visitor.analyze(root));
        assertEquals(new StatisticsVisitor.Statistics(0, 1, 0), visitor.analyze(new DirectoryNode("newRoot")));
    }
}
