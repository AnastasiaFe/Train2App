package ua.nure.fedorenko.kidstim.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.nure.fedorenko.kidstim.R;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> tasks = new ArrayList<>();

    @Nullable
    private TaskListener taskListener;

    public void setTaskListener(@Nullable TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public void setTasks(List<Task> t) {
        tasks.clear();
        tasks.addAll(t);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.task_recycle_view_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskDescTextView.setText(task.getDescription());
        holder.taskStatusTextView.setText(task.getStatus().toString());
        holder.taskPointsTextView.setText(String.valueOf(task.getPoints()));
        List<Child> children = task.getChildren();
        if (children == null) {
            children = Collections.emptyList();
        }
        holder.childrenLayout.removeAllViews();
        for (Child child : children) {
            holder.childrenLayout.addView(taskListener.onGetImage(child.getEmail()));
        }
        switch (task.getStatus()) {
            case COMPLETED:
                holder.markCompletedCheckbox.setChecked(true);
                break;
            default:
                holder.markCompletedCheckbox.setChecked(false);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.taskDescTextView)
        TextView taskDescTextView;

        @BindView(R.id.taskStatusTextView)
        TextView taskStatusTextView;

        @BindView(R.id.taskPointsTextView)
        TextView taskPointsTextView;

        @BindView(R.id.markCompletedCheckbox)
        CheckBox markCompletedCheckbox;

        @BindView(R.id.childrenPicturesLayout)
        LinearLayout childrenLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.deleteTaskButton)
        void onDeleteTaskButtonClick() {
            AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.task_deleting)
                    .setMessage(R.string.task_delete_confirmation)
                    .setPositiveButton(R.string.delete, (dialog1, whichButton) -> {
                        if (taskListener != null) {
                            taskListener.onDeleteTask(tasks.get(getAdapterPosition()).getId());
                        }
                        dialog1.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog12, which) -> dialog12.dismiss())
                    .create();
            dialog.show();
        }

        @OnClick(R.id.showEditTaskButton)
        void onShowEditTaskButtonClick() {
            if (taskListener != null) {
                taskListener.onEditTask(tasks.get(getAdapterPosition()).getId());
            }
        }

        @OnClick(R.id.markCompletedCheckbox)
        void onMarkCompletedCheckChanged() {
            AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.task_completion)
                    .setMessage(R.string.completion_confirmation)
                    .setPositiveButton(R.string.yes, (dialog1, whichButton) -> {
                        if (taskListener != null) {
                            taskListener.onStatusChange(tasks.get(getAdapterPosition()).getId(), markCompletedCheckbox.isChecked());
                        }
                        dialog1.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog12, which) -> {
                        dialog12.dismiss();
                        markCompletedCheckbox.setChecked(!markCompletedCheckbox.isChecked());
                    })
                    .create();
            dialog.show();

        }
    }

    public interface TaskListener {
        void onEditTask(String taskId);

        void onDeleteTask(String taskId);

        void onStatusChange(String taskId, boolean complete);


        ImageView onGetImage(String photo);
    }
}

