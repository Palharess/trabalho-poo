package org.example;

import java.util.Stack;

public class TaskManagerHistory {
    private Stack<Memento> undoStack = new Stack<>();
    private Stack<Memento> redoStack = new Stack<>();

    public void saveState(Memento m) {
        undoStack.push(m);
        redoStack.clear();
    }

    public Memento undo() {
        if(!undoStack.isEmpty()) {
            Memento m = undoStack.pop();
            redoStack.push(m);
            return m;
        }
        return null;
    }

    public Memento redo() {
        if(!redoStack.isEmpty()) {
            Memento m = redoStack.pop();
            undoStack.push(m);
            return m;
        }
        return null;
    }
}
