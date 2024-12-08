package org.example;

import java.io.File;
import java.io.IOException;

public class AutoSaveThread extends Thread {
    private final TaskManager taskManager;
    private final File file;
    private volatile boolean running = true;

    public AutoSaveThread(TaskManager tm, File f) {
        this.taskManager = tm;
        this.file = f;
    }

    @Override
    public void run() {
        while(running) {
            try {
                Thread.sleep(15000); // Espera 15 segundos
                TaskFileHandler.saveTasks(taskManager.getTasks(), file);
                System.out.println("AutoSave realizado com sucesso em " + file.getName());
            } catch (InterruptedException e) {
                running = false; // Interrompe o loop se a thread for interrompida
            } catch (IOException e) {
                System.err.println("Erro ao realizar auto-save: " + e.getMessage());
            }
        }
    }

    public void stopAutoSave() {
        running = false;
        this.interrupt();
    }
}
