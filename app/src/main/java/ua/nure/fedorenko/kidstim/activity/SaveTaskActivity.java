package ua.nure.fedorenko.kidstim.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Parent;
import ua.nure.fedorenko.kidstim.entity.Task;
import ua.nure.fedorenko.kidstim.entity.TaskStatus;
import ua.nure.fedorenko.kidstim.utils.Constants;
import ua.nure.fedorenko.kidstim.utils.Validator;
import ua.nure.fedorenko.kidstim.R;

public class SaveTaskActivity extends BaseNavigableActivity {

    @BindView(R.id.taskDescEditText)
    EditText taskDescEditText;
    @BindView(R.id.taskPointsEditText)
    EditText taskPointsEditText;
    @BindView(R.id.taskDescLayout)
    TextInputLayout taskDescLayout;
    @BindView(R.id.taskPointsLayout)
    TextInputLayout taskPointsLayout;
    @BindView(R.id.openCalendarButton)
    Button openCalendarButton;

    @BindView(R.id.expirationTextView)
    TextView expirationTextView;
    private Task task;
    private Calendar expirationDate;
    private Parent parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_task);
        expirationDate = Calendar.getInstance();
        String taskId = getIntent().getStringExtra(Constants.ID);
        setTask(taskId);
    }

    @OnClick(R.id.addTaskButton)
    void onAddTaskButtonClick() {
        clearErrors();
        if (isInputValid()) {
            execute();
        }
    }

    private void setTask(String id) {
        if (id != null) {
            Single<Task> single = apiService.getTaskById(id);
            SingleSubscriber<Task> subscriber = new SingleSubscriber<Task>() {
                @Override
                public void onSuccess(Task value) {
                    task = value;
                    taskDescEditText.setText(task.getDescription());
                    taskPointsEditText.setText(String.valueOf(task.getPoints()));
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date(task.getExpirationDate()));
                    expirationTextView.setText(
                            String.format(Locale.ENGLISH, "%d/%d/%d", c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)));
                }

                @Override
                public void onError(Throwable error) {
                    Toast.makeText(SaveTaskActivity.this, getString(R.string.error_task_retrieving), Toast.LENGTH_LONG).show();
                }
            };
            single.subscribe(subscriber);
        } else {
            task = new Task();
        }
    }

    private void execute() {
        String id = sharedPreferences.getString(Constants.USER, Constants.EMPTY_STRING);
        Single<Parent> parentSingle = apiService.getParentByEmail(id);
        SingleSubscriber<Parent> subscriber = new SingleSubscriber<Parent>() {
            @Override
            public void onSuccess(Parent value) {
                parent = value;
                showAlertDialog();
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(SaveTaskActivity.this, getString(R.string.error_parent_retrieving), Toast.LENGTH_LONG).show();
            }
        };
        parentSingle.subscribe(subscriber);
    }


    public void showAlertDialog() {
        final List<Child> selectedItems = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        for (Child child : parent.getChildren()) {
            names.add(child.getName());
        }
        boolean[] checked = new boolean[names.size()];
        if (task.getId() != null) {
            for (int i = 0; i < parent.getChildren().size(); i++) {
                if (task.getChildren().contains(parent.getChildren().get(i))) {
                    selectedItems.add(parent.getChildren().get(i));
                    checked[i] = true;
                }
            }
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.select_children)
                .setMultiChoiceItems(names.toArray(new CharSequence[names.size()]), checked, (dialog12, indexSelected, isChecked) -> {
                    Child child = parent.getChildren().get(indexSelected);
                    if (isChecked) {
                        selectedItems.add(child);
                    } else if (selectedItems.contains(child)) {
                        selectedItems.remove(child);
                    }
                }).setPositiveButton(R.string.ok, (dialog1, id) -> {
                    task.setChildren(selectedItems);
                    task.setDescription(taskDescEditText.getText().toString());
                    task.setParent(parent);
                    task.setPoints(Integer.parseInt(taskPointsEditText.getText().toString()));
                    task.setCreationDate(new Date().getTime());
                    task.setExpirationDate(expirationDate.getTime().getTime());
                    task.setStatus(TaskStatus.CREATED);
                    save(task);
                    Intent intent = new Intent(SaveTaskActivity.this, ParentTasksActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }).create();
        dialog.show();

    }

    @OnClick(R.id.openCalendarButton)
    void onOpenCalendarButtonClick() {
        new DatePickerDialog(this, date, expirationDate.get(Calendar.YEAR), expirationDate.get(Calendar.MONTH),
                expirationDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            expirationDate.set(year, monthOfYear, dayOfMonth);
            expirationTextView.setText(String.format(Locale.ENGLISH, "%d/%d/%d", dayOfMonth, monthOfYear, year));
        }

    };

    private boolean isInputValid() {
        String desc = taskDescEditText.getText().toString();
        String points = taskPointsEditText.getText().toString();
        if (Validator.isDescriptionValid(desc) && Validator.isPointsValid(points)) {
            return true;
        } else {
            if (!Validator.isDescriptionValid(desc)) {
                taskDescLayout.setError(getString(R.string.error_invalid_desc));
            }
            if (!Validator.isPointsValid(points)) {
                taskPointsLayout.setError(getString(R.string.error_invalid_points));
            }
            return false;
        }
    }

    private void clearErrors() {
        taskDescLayout.setError(null);
        taskPointsLayout.setError(null);
    }

    public SaveTaskActivity() {
    }


    // TODO: 10/23/2017 on completed implementation
    private void save(Task task) {
        Completable completable;
        if (task.getId() != null) {
            completable = apiService.updateTask(task);
        } else {
            completable = apiService.addTask(task);
        }
        Completable.CompletableSubscriber subscriber = new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(SaveTaskActivity.this, getString(R.string.error_task_saving), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSubscribe(Subscription d) {

            }
        };
        completable.subscribe(subscriber);
    }


}
