package ua.nure.fedorenko.kidstim.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ua.nure.fedorenko.kidstim.R;
import ua.nure.fedorenko.kidstim.entity.Child;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private List<Child> children = new ArrayList<>();
    @Nullable
    private ChildListener childListener;

    public void setChildren(List<Child> c) {
        children.clear();
        children.addAll(c);
        notifyDataSetChanged();
    }

    public void setChildListener(@Nullable ChildListener childListener) {
        this.childListener = childListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.child_recycle_view_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Child child = children.get(position);
        holder.childNameTextView.setText(child.getName());
        holder.childPointsTextView.setText(String.valueOf(child.getPoints()));
        if (childListener != null) {
            childListener.onGetImage(child.getEmail(), holder.childImageView);
        }
    }

    @Override
    public int getItemCount() {
        return children.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.childImageView)
        ImageView childImageView;

        @BindView(R.id.childNameTextView)
        TextView childNameTextView;

        @BindView(R.id.childPointsTextView)
        TextView childPointsTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ChildListener {
        void onChildSelected(@NonNull Child child);

        void onGetImage(@NonNull String photo, ImageView imageView);
    }
}
