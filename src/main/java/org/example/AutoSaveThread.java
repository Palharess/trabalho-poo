package org.example;

import java.io.File;
import java.io.IOException;

public class AutoSaveThread extends Thread {
    private TaskManager taskManager;
    private File file;

    public AutoSaveThread(TaskManager tm, File f) {
        this.taskManager = tm;
        this.file = f;
    }

    public void run() {
        while(!isInterrupted()) {
            try {
                Thread.sleep(30000); // salva a cada 30s
                TaskFileHandler.saveTasks(taskManager.getTasks(), file);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch(IOException e) {
                // Logar o erro, não necessariamente parar a thread.
                System.err.println("Erro ao salvar automaticamente: " + e.getMessage());
            }
        }
    }
}
