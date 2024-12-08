package org.example;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private List<TaskManagerObserver> observers = new ArrayList<>();

    public void addTask(Task t) {
        tasks.add(t);
        notifyObservers();
    }

    public void removeTask(Task t) {
        tasks.remove(t);
        notifyObservers();
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void updateTask(Task oldTask, Task newTask) {
        int index = tasks.indexOf(oldTask);
        if (index != -1) {
            tasks.set(index, newTask);
            notifyObservers();
        }
    }

    public void addObserver(TaskManagerObserver obs) {
        observers.add(obs);
    }

    public void removeObserver(TaskManagerObserver obs) {
        observers.remove(obs);
    }

    // Torna notifyObservers privado, mas fornece um método público para atualizar manualmente
    private void notifyObservers() {
        for(TaskManagerObserver obs : observers) {
            obs.onTasksChanged();
        }
    }

    public void refreshObservers() {
        // Método público que chama internamente notifyObservers()
        notifyObservers();
    }

    // Memento (não alterado)
    public Memento createMemento() {
        return new Memento(new ArrayList<>(tasks));
    }

    public void restoreMemento(Memento m) {
        this.tasks = new ArrayList<>(m.getState());
        notifyObservers();
    }
}
