package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskFileHandler {
    public static void saveTasks(List<Task> tasks, File file) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for(Task t : tasks) {
                bw.write(t.getTitle() + ";" + t.getDescription() + ";" + t.isDone());
                bw.newLine();
            }
        }
    }

    public static List<Task> loadTasks(File file) throws IOException {
        List<Task> tasks = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if(parts.length == 3) {
                    String title = parts[0];
                    String desc = parts[1];
                    boolean done = Boolean.parseBoolean(parts[2]);
                    Task t = new SimpleTask(title, desc);
                    t.setDone(done);
                    tasks.add(t);
                }
            }
        }
        return tasks;
    }
}
