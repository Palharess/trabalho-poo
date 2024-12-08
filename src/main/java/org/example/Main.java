package org.example;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        TaskManagerHistory history = new TaskManagerHistory();
        TaskFactory factory = new SimpleTaskFactory();

        File dbFile = new File("tasks.db");

        if (dbFile.exists()) {
            try {
                List<Task> loaded = TaskFileHandler.loadTasks(dbFile);
                for (Task t : loaded) {
                    taskManager.addTask(t);
                }
            } catch (IOException e) {
                System.err.println("Não foi possível carregar as tarefas: " + e.getMessage());
            }
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(taskManager, history, factory);

            // Ao invés de chamar taskManager.notifyObservers(), agora chamamos:
            taskManager.refreshObservers();

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    try {
                        TaskFileHandler.saveTasks(taskManager.getTasks(), dbFile);
                    } catch (IOException e) {
                        System.err.println("Erro ao salvar as tarefas: " + e.getMessage());
                    }
                }
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
