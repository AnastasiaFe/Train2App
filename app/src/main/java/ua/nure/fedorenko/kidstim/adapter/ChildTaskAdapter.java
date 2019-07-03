package ua.nure.fedorenko.kidstim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ua.nure.fedorenko.kidstim.entity.Task;
import ua.nure.fedorenko.kidstim.R;

public class ChildTaskAdapter extends RecyclerView.Adapter<ChildTaskAdapter.ViewHolder> {

    private List<Task> tasks = new ArrayList<>();

    public void setTasks(List<Task> t) {
        tasks.clear();
        tasks.addAll(t);
        notifyDataSetChanged();
    }

    @Override
    public ChildTaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.child_task_recycle_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ChildTaskAdapter.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskDescTextView.setText(task.getDescription());
        holder.taskStatusTextView.setText(task.getStatus().toString());
        holder.taskPointsTextView.setText(String.valueOf(task.getPoints()));
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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
