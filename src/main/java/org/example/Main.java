package org.example;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Pede o nome do usuário
        String username = JOptionPane.showInputDialog(null, "Qual é o seu nome?", "Bem-vindo", JOptionPane.QUESTION_MESSAGE);

        if (username == null || username.trim().isEmpty()) {
            username = "usuario_padrao";
        }

        TaskManager taskManager = new TaskManager();
        TaskManagerHistory history = new TaskManagerHistory();
        TaskFactory factory = new SimpleTaskFactory();

        // Arquivo de tarefas específico do usuário
        File dbFile = new File(username + "_tasks.db");

        // Carrega as tasks do usuário, se o arquivo existir
        if (dbFile.exists()) {
            try {
                List<Task> loaded = TaskFileHandler.loadTasks(dbFile);
                for (Task t : loaded) {
                    taskManager.addTask(t);
                }
            } catch (IOException e) {
                System.err.println("Não foi possível carregar as tarefas do usuário " + username + ": " + e.getMessage());
            }
        }

        String finalUsername = username;

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(taskManager, history, factory, finalUsername);
            taskManager.refreshObservers();

            // Cria e inicia a AutoSaveThread após a janela ser exibida
            AutoSaveThread autoSaveThread = new AutoSaveThread(taskManager, dbFile);
            autoSaveThread.start();

            // Ao fechar a janela, salvamos as tasks e paramos a thread de auto-save
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    try {
                        TaskFileHandler.saveTasks(taskManager.getTasks(), dbFile);
                    } catch (IOException e) {
                        System.err.println("Erro ao salvar as tarefas de " + finalUsername + ": " + e.getMessage());
                    }
                    // Para a autoSaveThread
                    autoSaveThread.stopAutoSave();
                }
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
