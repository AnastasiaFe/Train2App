package ua.nure.fedorenko.kidstim.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import rx.Single;
import rx.SingleSubscriber;
import ua.nure.fedorenko.kidstim.adapter.ChildTaskAdapter;
import ua.nure.fedorenko.kidstim.entity.Task;
import ua.nure.fedorenko.kidstim.entity.TaskStatus;
import ua.nure.fedorenko.kidstim.service.TaskService;
import ua.nure.fedorenko.kidstim.utils.Constants;
import ua.nure.fedorenko.kidstim.R;

public class ChildTasksActivity extends BaseNavigableActivity {

    private List<Task> allTasks;
    @BindView(R.id.tasksRecycleView)
    RecyclerView tasksRecycleView;
    private ChildTaskAdapter taskAdapter;
    String childId;
    private TaskService taskService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_tasks);
        taskAdapter = new ChildTaskAdapter();
        tasksRecycleView.setAdapter(taskAdapter);
        tasksRecycleView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        childId = sharedPreferences.getString(Constants.ID, Constants.EMPTY_STRING);
        taskService = new TaskService();
        getAllTasks();

    }

    private void getAllTasks() {
        Single<List<Task>> taskSingle = apiService.getTasksByChild(childId);
        SingleSubscriber<List<Task>> subscriber = new SingleSubscriber<List<Task>>() {
            @Override
            public void onSuccess(List<Task> value) {
                allTasks = value;
                taskAdapter.setTasks(value);
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(ChildTasksActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        taskSingle.subscribe(subscriber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(this,
                R.array.task_statuses, android.R.layout.simple_spinner_item);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                switch (pos) {
                    case 1:
                        taskAdapter.setTasks(taskService.getTasksByStatus(TaskStatus.CREATED, allTasks));
                        break;
                    case 2:
                        taskAdapter.setTasks(taskService.getTasksByStatus(TaskStatus.COMPLETED, allTasks));
                        break;
                    default:
                        if (allTasks == null) {
                            getAllTasks();
                        } else {
                            taskAdapter.setTasks(allTasks);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return true;
    }


}
