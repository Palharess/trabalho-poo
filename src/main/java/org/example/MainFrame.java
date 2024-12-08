package org.example;

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
    private String username;

    public MainFrame(TaskManager tm, TaskManagerHistory h, TaskFactory f, String username) {
        this.taskManager = tm;
        this.history = h;
        this.factory = f;
        this.username = username;
        this.taskManager.addObserver(this);

        // Define tamanho da janela
        setSize(800, 600);
        setTitle("To Do List Application - Usuário: " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Define cor de fundo da janela
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Painel de topo com título estilizado
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("TO DO LIST", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);

        // Aumenta a fonte das tasks e a altura da célula
        taskList.setFont(new Font("Arial", Font.PLAIN, 18));
        taskList.setFixedCellHeight(30);

        JScrollPane scrollPane = new JScrollPane(taskList);

        // Painel de botões inferior
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 3, 10, 10));
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton addBtn = new JButton("Adicionar");
        JButton removeBtn = new JButton("Remover");
        JButton editBtn = new JButton("Editar");
        JButton doneBtn = new JButton("Concluído");
        JButton undoneBtn = new JButton("Não Concluído");
        JButton undoBtn = new JButton("Undo");

        // Ações dos botões
        addBtn.addActionListener(e -> addTask());
        removeBtn.addActionListener(e -> removeSelectedTask());
        editBtn.addActionListener(e -> editSelectedTask());
        doneBtn.addActionListener(e -> markSelectedTask(true));
        undoneBtn.addActionListener(e -> markSelectedTask(false));
        undoBtn.addActionListener(e -> undo());

        // Adiciona os botões ao painel
        buttonsPanel.add(addBtn);
        buttonsPanel.add(removeBtn);
        buttonsPanel.add(editBtn);
        buttonsPanel.add(doneBtn);
        buttonsPanel.add(undoneBtn);
        buttonsPanel.add(undoBtn);

        // Barra de menu
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

        // Adiciona os componentes ao frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void addTask() {
        String title = JOptionPane.showInputDialog(this, "Título da tarefa:");
        if(title != null && !title.isEmpty()) {
            String desc = JOptionPane.showInputDialog(this, "Descrição da tarefa:");
            history.saveState(taskManager.createMemento());
            Task t = factory.createTask(title, desc == null ? "" : desc);
            taskManager.addTask(t);
        }
    }

    private void removeSelectedTask() {
        int index = taskList.getSelectedIndex();
        if(index >= 0) {
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
        Memento m = history.undo();
        if(m != null) {
            taskManager.restoreMemento(m);
        } else {
            JOptionPane.showMessageDialog(this, "Não há mais operações para desfazer.");
        }
    }

    private void saveTasks() {
        if(currentFile == null) {
            currentFile = new File(username + "_tasks.db");
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
                history.saveState(taskManager.createMemento());
                java.util.List<Task> loaded = TaskFileHandler.loadTasks(f);

                for (Task t : new java.util.ArrayList<>(taskManager.getTasks())) {
                    taskManager.removeTask(t);
                }

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
