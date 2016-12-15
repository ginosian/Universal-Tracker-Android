package com.margin.mgms.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.margin.mgms.R;
import com.margin.mgms.model.Action;
import com.margin.mgms.mvp.task_manager.TaskManagerContract;

import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ActionHolder> {

    private List<Action> mActions;
    private TaskManagerContract.Presenter mPresenter;
    private String secondaryTextPlaceHolderString;

    public TasksAdapter(List<Action> actions, TaskManagerContract.Presenter presenter) {
        mActions = actions;
        mPresenter = presenter;
    }

    @Override
    public ActionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tasks_list_item, parent, false);
        secondaryTextPlaceHolderString = parent.getContext().getString(R.string
                .value_completed);
        return new ActionHolder(v);
    }

    @Override
    public void onBindViewHolder(ActionHolder holder, int position) {
        final Action action = mActions.get(position);
        holder.mainText.setText(action.getLabel());
        String secondaryText = String.format(secondaryTextPlaceHolderString,
                action.getCompletedTaskCount(), action.getTaskCount());
        holder.secondaryText.setText(secondaryText);
        holder.icon.setImageResource(action.getImage() != null ? R.drawable.unlock_icon :
                R.drawable.lock_icon);
    }

    @Override
    public int getItemCount() {
        return mActions.size();
    }

    /**
     * Inserts actions into adapter
     */
    public void addActions(Collection<Action> actions) {
        int startPosition = mActions.size();
        mActions.addAll(actions);
        notifyItemRangeInserted(startPosition, actions.size());
    }

    public class ActionHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.main_text)
        public TextView mainText;
        @Bind(R.id.secondary_text)
        public TextView secondaryText;
        @Bind(R.id.icon)
        public ImageView icon;

        public ActionHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.item_layout})
        public void onItemClicked(View v) {
            if (mPresenter != null) {
                mPresenter.onItemClicked(v.getContext(), mActions.get(getAdapterPosition()),
                        getAdapterPosition());
            }
        }
    }
}
