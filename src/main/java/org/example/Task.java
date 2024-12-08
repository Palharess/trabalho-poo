package org.example;

public interface Task {
    String getTitle();
    void setTitle(String title);
    String getDescription();
    void setDescription(String description);
    boolean isDone();
    void setDone(boolean done);
}
