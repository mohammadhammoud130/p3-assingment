package model;


import java.util.ArrayList;

public class ProductLine {
    private static int nextId = 1;
    private final int id ;
    private String name;
    private Status status;
    private ArrayList<Task> tasks;
    private ArrayList<String> notes;


    public ProductLine(String name, Status status, ArrayList<Task> tasks) {
        this.id = nextId++;
        this.name = name;
        this.status = status;
        this.tasks = tasks;
        this.notes = new ArrayList<String>();
    }

    public ProductLine(int id, String name, Status status, ArrayList<Task> tasks) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.tasks = (tasks != null) ? tasks : new ArrayList<>();
        if (id >= nextId) {
            nextId = id + 1;
        }
        this.notes = new ArrayList<String>();
    }

    public static void resetIdCounter(int newNextId) { nextId = newNextId; }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status Status) {
        this.status = status;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public void addNote(String note){
        this.notes.add(note);
    }
}
