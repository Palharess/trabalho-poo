package org.example;

import java.util.List;

public class Memento {
    private final List<Task> state;

    public Memento(List<Task> state) {
        // Cria uma cópia imutável do estado (poderíamos clonar tarefas se necessário)
        this.state = state;
    }

    public List<Task> getState() {
        return state;
    }
}
