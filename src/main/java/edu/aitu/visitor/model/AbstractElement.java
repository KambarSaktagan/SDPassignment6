package edu.aitu.visitor.model;

import java.util.Objects;

abstract class AbstractElement implements Element {
    private final String name;
    private DirectoryNode parent;

    protected AbstractElement(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank() || name.equals(".") || name.equals("..")
                || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("Invalid element name: " + name);
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    final DirectoryNode getParent() {
        return parent;
    }

    final void setParent(DirectoryNode parent) {
        this.parent = parent;
    }
}
