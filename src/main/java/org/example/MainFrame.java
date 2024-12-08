package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame implements TaskManagerObserver {
    private TaskManager taskManager;
    private TaskManagerHistory history;
    private JList<String> taskList;
    private DefaultListModel<String> listModel;
    private File currentFile;
    private TaskFactory factory;

    public MainFrame(TaskManager tm, TaskManagerHistory h, TaskFactory f) {
        this.taskManager = tm;
        this.history = h;
        this.factory = f;
        this.taskManager.addObserver(this);

        setTitle("TODO List");
        setSize(600,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);

        JPanel buttonsPanel = new JPanel();
        JButton addBtn = new JButton("Adicionar");
        JButton removeBtn = new JButton("Remover");
        JButton editBtn = new JButton("Editar");
        JButton doneBtn = new JButton("Marcar Concluído");
        JButton undoneBtn = new JButton("Marcar Não Concluído");
        JButton undoBtn = new JButton("Undo");

        addBtn.addActionListener(e -> addTask());
        removeBtn.addActionListener(e -> removeSelectedTask());
        editBtn.addActionListener(e -> editSelectedTask());
        doneBtn.addActionListener(e -> markSelectedTask(true));
        undoneBtn.addActionListener(e -> markSelectedTask(false));

        // Ação do botão Undo:
        undoBtn.addActionListener(e -> undo());

        buttonsPanel.add(addBtn);
        buttonsPanel.add(removeBtn);
        buttonsPanel.add(editBtn);
        buttonsPanel.add(doneBtn);
        buttonsPanel.add(undoneBtn);
        buttonsPanel.add(undoBtn); // Apenas o undo, sem redo

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem saveItem = new JMenuItem("Salvar");
        JMenuItem loadItem = new JMenuItem("Carregar");

        saveItem.addActionListener(e -> saveTasks());
        loadItem.addActionListener(e -> loadTasks());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void addTask() {
        String title = JOptionPane.showInputDialog(this, "Título da tarefa:");
        if(title != null && !title.isEmpty()) {
            String desc = JOptionPane.showInputDialog(this, "Descrição da tarefa:");
            // Antes de alterar o estado, salvamos o estado atual no history.
            history.saveState(taskManager.createMemento());
            Task t = factory.createTask(title, desc == null ? "" : desc);
            taskManager.addTask(t);
        }
    }

    private void removeSelectedTask() {
        int index = taskList.getSelectedIndex();
        if(index >= 0) {
            // Antes de remover, salvamos o estado
            history.saveState(taskManager.createMemento());
            Task t = taskManager.getTasks().get(index);
            taskManager.removeTask(t);
        }
    }

    private void editSelectedTask() {
        int index = taskList.getSelectedIndex();
        if(index >= 0) {
            Task oldTask = taskManager.getTasks().get(index);
            String newTitle = JOptionPane.showInputDialog(this, "Novo título:", oldTask.getTitle());
            if(newTitle != null && !newTitle.isEmpty()) {
                String newDesc = JOptionPane.showInputDialog(this, "Nova descrição:", oldTask.getDescription());
                // Salva estado antes da modificação
                history.saveState(taskManager.createMemento());
                oldTask.setTitle(newTitle);
                oldTask.setDescription(newDesc);
                taskManager.updateTask(taskManager.getTasks().get(index), oldTask);
            }
        }
    }

    private void markSelectedTask(boolean done) {
        int index = taskList.getSelectedIndex();
        if(index >= 0) {
            history.saveState(taskManager.createMemento());
            Task oldTask = taskManager.getTasks().get(index);
            oldTask.setDone(done);
            taskManager.updateTask(taskManager.getTasks().get(index), oldTask);
        }
    }

    private void undo() {
        // Realiza o undo usando o history e o taskManager
        Memento m = history.undo();
        if(m != null) {
            taskManager.restoreMemento(m);
        } else {
            JOptionPane.showMessageDialog(this, "Não há mais operações para desfazer.");
        }
    }

    private void saveTasks() {
        if(currentFile == null) {
            JFileChooser fc = new JFileChooser();
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = fc.getSelectedFile();
            }
        }
        if(currentFile != null) {
            try {
                TaskFileHandler.saveTasks(taskManager.getTasks(), currentFile);
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
            }
        }
    }

    private void loadTasks() {
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                // Salva estado atual antes de carregar
                history.saveState(taskManager.createMemento());

                // Carrega tarefas do arquivo
                java.util.List<Task> loaded = TaskFileHandler.loadTasks(f);

                // Remove todas as tarefas atuais
                for (Task t : new java.util.ArrayList<>(taskManager.getTasks())) {
                    taskManager.removeTask(t);
                }

                // Adiciona as tarefas carregadas
                for (Task t : loaded) {
                    taskManager.addTask(t);
                }

                currentFile = f;
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage());
            }
        }
    }

    @Override
    public void onTasksChanged() {
        listModel.clear();
        for(Task t : taskManager.getTasks()) {
            listModel.addElement((t.isDone() ? "[X] " : "[ ] ") + t.getTitle() + " - " + t.getDescription());
        }
    }
}

