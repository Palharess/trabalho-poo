package org.example;

public class SimpleTaskFactory extends TaskFactory {
    public Task createTask(String title, String description) {
        return new SimpleTask(title, description);
    }
}
