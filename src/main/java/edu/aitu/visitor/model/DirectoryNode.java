package edu.aitu.visitor.model;

import edu.aitu.visitor.visitor.Visitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public final class DirectoryNode extends AbstractElement {
    private final NavigableMap<String, Element> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void addChild(Element child) {
        Objects.requireNonNull(child, "child");
        // Ownership bookkeeping is shared by the two concrete elements;
        // this cast never selects file-versus-directory behavior.
        AbstractElement element = (AbstractElement) child;
        if (children.containsKey(child.getName())) {
            throw new IllegalArgumentException("Duplicate sibling name: " + child.getName());
        }
        if (element.getParent() != null) {
            throw new IllegalArgumentException("An element cannot have multiple parents");
        }
        for (DirectoryNode ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("A directory cannot contain itself or an ancestor");
            }
        }
        children.put(child.getName(), child);
        element.setParent(this);
    }

    public Element removeChild(String name) {
        Element removed = children.remove(Objects.requireNonNull(name, "name"));
        if (removed != null) {
            ((AbstractElement) removed).setParent(null);
        }
        return removed;
    }

    public List<Element> getChildren() {
        return Collections.unmodifiableList(new ArrayList<>(children.values()));
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "visitor").visit(this);
    }
}
