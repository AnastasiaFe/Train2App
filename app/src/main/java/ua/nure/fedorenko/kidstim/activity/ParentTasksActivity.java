package ua.nure.fedorenko.kidstim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import ua.nure.fedorenko.kidstim.AppConstants;
import ua.nure.fedorenko.kidstim.adapter.TaskAdapter;
import ua.nure.fedorenko.kidstim.entity.Task;
import ua.nure.fedorenko.kidstim.entity.TaskStatus;
import ua.nure.fedorenko.kidstim.service.TaskService;
import ua.nure.fedorenko.kidstim.utils.Constants;
import ua.nure.fedorenko.kidstim.R;

public class ParentTasksActivity extends BaseNavigableActivity implements TaskAdapter.TaskListener {

    private List<Task> allTasks;
    @BindView(R.id.tasksRecycleView)
    RecyclerView tasksRecycleView;
    private TaskAdapter taskAdapter;
    private String parentId;
    private TaskService taskService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_tasks);
        taskAdapter = new TaskAdapter();
        setTaskListener(taskAdapter);
        tasksRecycleView.setAdapter(taskAdapter);
        tasksRecycleView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        parentId = sharedPreferences.getString(Constants.ID, Constants.EMPTY_STRING);
        taskService = new TaskService();

    }

    private void getAllTasks() {
        Single<List<Task>> taskSingle = apiService.getTasksByParent(parentId);
        SingleSubscriber<List<Task>> subscriber = new SingleSubscriber<List<Task>>() {
            @Override
            public void onSuccess(List<Task> value) {
                allTasks = value;
                taskAdapter.setTasks(value);
            }

            @Override
            public void onError(Throwable error) {

            }
        };
        taskSingle.subscribe(subscriber);
    }

    private void setTaskListener(TaskAdapter adapter) {
        adapter.setTaskListener(this);
    }

    @OnClick(R.id.addTaskFloatingActionButton)
    void onAddTaskFloatingButtonClick() {
        startActivity(new Intent(this, SaveTaskActivity.class));
    }

    @Override
    public void onEditTask(String taskId) {
        Intent intent = new Intent(ParentTasksActivity.this, SaveTaskActivity.class);
        intent.putExtra(Constants.ID, taskId);
        startActivity(intent);
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

    @Override
    public void onDeleteTask(String taskId) {
        Completable completable = apiService.deleteTask(taskId);
        Completable.CompletableSubscriber subscriber = new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {
                Toast.makeText(getBaseContext(), "Ok", Toast.LENGTH_LONG).show();
                refreshActivity();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSubscribe(Subscription d) {

            }
        };
        completable.subscribe(subscriber);
    }

    private void refreshActivity() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onStatusChange(String taskId, boolean complete) {
        Completable completable = apiService.markTaskAsCompleted(taskId, complete);
        Completable.CompletableSubscriber subscriber = new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {
                refreshActivity();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSubscribe(Subscription d) {

            }
        };
        completable.subscribe(subscriber);
    }

    @Override
    public ImageView onGetImage(String photo) {
        ImageView imageView = new ImageView(getBaseContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(70, 70);
        imageView.setLayoutParams(layoutParams);
        String token = sharedPreferences.getString(Constants.AUTHORIZATION_HEADER, Constants.EMPTY_STRING);
        GlideUrl glideUrl = new GlideUrl(AppConstants.IMAGE_URL + "?name=" + photo + "&role=child", new LazyHeaders.Builder()
                .addHeader(Constants.AUTHORIZATION_HEADER, token)
                .build());
        Glide.with(this).load(glideUrl).into(imageView);
        return imageView;
    }


}
