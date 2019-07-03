package ua.nure.fedorenko.kidstim.service;

import java.util.ArrayList;
import java.util.List;

import ua.nure.fedorenko.kidstim.entity.Task;
import ua.nure.fedorenko.kidstim.entity.TaskStatus;

public class TaskService {


    public List<Task> getTasksByStatus(TaskStatus taskStatus, List<Task> allTasks) {
        List<Task> filtered = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getStatus().equals(taskStatus)) {
                filtered.add(task);
            }
        }
        return filtered;
    }
}
