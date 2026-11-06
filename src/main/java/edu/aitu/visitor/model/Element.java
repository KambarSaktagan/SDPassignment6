package edu.aitu.visitor.model;

import edu.aitu.visitor.visitor.Visitor;

public interface Element {
    String getName();
    void accept(Visitor visitor);
}
