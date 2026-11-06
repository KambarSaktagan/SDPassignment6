package edu.aitu.visitor.app;

import edu.aitu.visitor.model.DirectoryNode;
import edu.aitu.visitor.model.FileNode;
import edu.aitu.visitor.visitor.SearchVisitor;
import edu.aitu.visitor.visitor.StatisticsVisitor;
import edu.aitu.visitor.visitor.TotalSizeVisitor;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        DirectoryNode project = new DirectoryNode("project");
        project.addChild(new FileNode("README.md", 700));
        project.addChild(new FileNode("notes.txt", 120));

        DirectoryNode src = new DirectoryNode("src");
        src.addChild(new FileNode("App.java", 2_400));
        src.addChild(new FileNode("Utils.java", 1_800));
        project.addChild(src);

        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("design.txt", 900));
        docs.addChild(new DirectoryNode("empty"));
        project.addChild(docs);

        System.out.println("Visitor Pattern - Advanced File System");
        System.out.println("Total size: " + new TotalSizeVisitor().calculate(project) + " bytes");
        System.out.println("Search .java files (>= 2000 bytes): "
                + new SearchVisitor(name -> name.endsWith(".java"), 2_000).search(project));
        StatisticsVisitor.Statistics statistics = new StatisticsVisitor().analyze(project);
        System.out.println("Statistics: files=" + statistics.fileCount()
                + ", directories=" + statistics.directoryCount()
                + ", maxDepth=" + statistics.maxDepth());
    }
}
