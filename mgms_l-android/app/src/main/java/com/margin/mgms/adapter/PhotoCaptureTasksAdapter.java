package com.margin.mgms.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.margin.mgms.R;
import com.margin.mgms.listener.OnTaskClickListener;
import com.margin.mgms.model.ShipmentLocation;
import com.margin.mgms.model.Task;
import com.margin.mgms.model.TaskHeader;

import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on May 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureTasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 1;
    private static final int TASK = 2;

    private List<Object> mTasks;
    private OnTaskClickListener mOnTaskClickListener;

    public PhotoCaptureTasksAdapter(@NonNull List<Object> items, OnTaskClickListener listener) {
        mTasks = items;
        mOnTaskClickListener = listener;
    }

    /**
     * Add task items to the adapter
     */
    public void addTaskItems(Collection<Object> tasksItems) {
        if (tasksItems != null && !tasksItems.isEmpty()) {
            int count = mTasks.size();
            mTasks.addAll(tasksItems);
            notifyItemRangeInserted(count, mTasks.size());
        }
    }

    /**
     * Clears all data from the adapter
     */
    public void clearData() {
        if (mTasks != null && !mTasks.isEmpty()) {
            int count = mTasks.size();
            mTasks.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    /**
     * Updates {@link Task} view
     */
    public void updateTaskItem(Task task) {
        int position = findTaskPosition(task);
        if (position >= 0 && position < mTasks.size()) {
            int headerPosition;
            int assignedHeaderPosition = findHeaderPosition(true);
            int notAssignedHeaderPosition = findHeaderPosition(false);
            TaskHeader header;
            if (position < notAssignedHeaderPosition || notAssignedHeaderPosition == -1) {
                headerPosition = assignedHeaderPosition;
                header = (TaskHeader) mTasks.get(assignedHeaderPosition);
            } else {
                headerPosition = notAssignedHeaderPosition;
                header = (TaskHeader) mTasks.get(notAssignedHeaderPosition);
            }
            switch (task.getStatus()) {
                case Completed:
                    header.setTaskCount(header.getTaskCount() - 1);
                    if (header.getTaskCount() > 0) notifyItemChanged(headerPosition);
                    else if (headerPosition >= 0 && headerPosition < mTasks.size()) {
                        //remove orphaned header
                        mTasks.remove(headerPosition);
                        notifyItemRemoved(headerPosition);
                        position--;
                    }
                    if (position >= 0 && position < mTasks.size()) {
                        //remove task item
                        mTasks.remove(position);
                        notifyItemRemoved(position);
                    }
                    break;
                case InProgress:
                    if (!header.isAssigned()) {
                        header.setTaskCount(header.getTaskCount() - 1);
                        if (header.getTaskCount() > 0) notifyItemChanged(headerPosition);
                        else if (headerPosition >= 0 && headerPosition < mTasks.size()) {
                            //remove orphaned not assigned header
                            mTasks.remove(headerPosition);
                            notifyItemRemoved(headerPosition);
                            position--;
                        }
                        if (assignedHeaderPosition >= 0) {
                            //update existing assigned header
                            TaskHeader assignedHeader = (TaskHeader) mTasks.get(assignedHeaderPosition);
                            assignedHeader.setTaskCount(assignedHeader.getTaskCount() + 1);
                            notifyItemChanged(assignedHeaderPosition);
                        } else {
                            //create new assigned header
                            assignedHeaderPosition = 0;
                            TaskHeader assignedHeader = new TaskHeader(true, 1);
                            mTasks.add(assignedHeaderPosition, assignedHeader);
                            notifyItemInserted(assignedHeaderPosition);
                            position++;
                        }
                    }
                    int newTaskPosition = assignedHeaderPosition + 1;
                    if (position == newTaskPosition) {
                        //just update the task
                        notifyItemChanged(position);
                    } else {
                        //move task to a new position
                        mTasks.remove(position);
                        mTasks.add(newTaskPosition, task);
                        notifyItemMoved(position, newTaskPosition);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Finds {@link Task} position in the list by {@link Task} object
     */
    private int findTaskPosition(Task task) {
        for (int i = 0; i < mTasks.size(); i++) {
            Object taskItem = mTasks.get(i);
            if (taskItem instanceof Task) {
                if (((Task) taskItem).task_id().equals(task.task_id())) return i;
            }
        }
        return -1;
    }

    /**
     * Finds {@link TaskHeader} position in the list
     */
    private int findHeaderPosition(boolean isAssigned) {
        for (int i = 0; i < mTasks.size(); i++) {
            Object taskItem = mTasks.get(i);
            if (taskItem instanceof TaskHeader
                    && ((TaskHeader) taskItem).isAssigned() == isAssigned) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.task_management_header_item, parent, false));
            case TASK:
                return new TaskViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.task_list_item, parent, false), mOnTaskClickListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof TaskViewHolder) {
            TaskViewHolder holder = (TaskViewHolder) viewHolder;
            Context context = holder.icon.getContext();
            Object taskItem = mTasks.get(position);
            if (taskItem instanceof Task) {
                Task task = (Task) taskItem;
                switch (task.getStatus()) {
                    case Completed:
                        holder.icon.setImageDrawable(context.getDrawable(R.drawable.complete_icon));
                        holder.time.setText(task.getFormattedEndDate());
                        break;
                    case InProgress:
                        holder.icon.setImageDrawable(context.getDrawable(R.drawable.progress_icon));
                        holder.time.setText(task.getFormattedStartDate());
                        break;
                    case NotStarted:
                    case NotAssigned:
                        holder.icon.setImageDrawable(context.getDrawable(R.drawable.not_started_icon));
                        holder.time.setText(task.getFormattedDate());
                        break;
                }
                holder.title.setText(task.reference());
                holder.pieces.setText(context.getString(R.string.value_pieces_weight,
                        task.total_pieces(), task.total_weight(), task.weight_uom()));
                holder.locationsContainer.removeAllViews();
                if (task.getLocations() != null && !task.getLocations().isEmpty()) {
                    for (ShipmentLocation location : task.getLocations()) {
                        addLocationView(context, location, holder.locationsContainer);
                    }
                }
                holder.iconsContainer.removeAllViews();
                List<Integer> specialIcons = task.getSpecialIcons();
                if (specialIcons != null && !specialIcons.isEmpty()) {
                    for (Integer drawableId : specialIcons) {
                        addSpecialHandlingView(context, drawableId, holder.iconsContainer);
                    }
                }
            }
        } else if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            Object taskItem = mTasks.get(position);
            if (taskItem instanceof TaskHeader) {
                TaskHeader taskHeader = (TaskHeader) taskItem;
                Context context = holder.assigned.getContext();
                if (taskHeader.isAssigned()) {
                    holder.assigned.setText(context.getString(R.string.header_assigned_to_me));
                } else {
                    holder.assigned.setText(context.getString(R.string.header_not_assigned_to_me));
                }
                String quantityString = context.getResources().getQuantityString(R.plurals.tasks_plurals,
                        taskHeader.getTaskCount(), taskHeader.getTaskCount());
                holder.tasks.setText(quantityString);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mTasks.get(position) instanceof TaskHeader) return HEADER;
        else if (mTasks.get(position) instanceof Task) return TASK;
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    /**
     * Adds locationView into the location container
     *
     * @param context  needed for getting resources and view inflating
     * @param location shipment location model
     * @param parent   location container
     */
    private void addLocationView(Context context, ShipmentLocation location, ViewGroup parent) {
        int margin = (int) context.getResources().getDimension(R.dimen.spacing_tiny);
        int tinyMargin = (int) context.getResources().getDimension(R.dimen.spacing_tiniest) / 2;
        View locationView = View.inflate(context, R.layout.task_locaton_layout, null);
        ((TextView) locationView.findViewById(R.id.title)).setText(context.getString(R.string
                .title_tasks_location, location.location(), location.pieces()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(parent.getChildCount() > 0 ? margin : tinyMargin, margin, margin, margin);
        locationView.setLayoutParams(params);
        parent.addView(locationView);
    }

    /**
     * Adds specialHandlingView into the special handling container
     *
     * @param context    needed for getting resources and view inflating
     * @param drawableId drawable resource id for image
     * @param parent     special handling container
     */
    private void addSpecialHandlingView(Context context, @DrawableRes int drawableId,
                                        ViewGroup parent) {
        int margin = (int) context.getResources().getDimension(R.dimen.spacing_tiny);
        int imageSize = (int) context.getResources().getDimension(R.dimen.spacing_large);
        ImageView icon = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        params.setMargins(parent.getChildCount() > 0 ? margin : 0, margin, margin, margin);
        icon.setLayoutParams(params);
        icon.setImageDrawable(context.getDrawable(drawableId));
        parent.addView(icon);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.assigned)
        public TextView assigned;
        @Bind(R.id.tasks)
        public TextView tasks;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.pieces)
        TextView pieces;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.locations_container)
        ViewGroup locationsContainer;
        @Bind(R.id.icons_container)
        ViewGroup iconsContainer;
        private OnTaskClickListener mOnTaskClickListener;

        public TaskViewHolder(View view, OnTaskClickListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            mOnTaskClickListener = listener;
        }

        @OnClick({R.id.item_layout})
        public void OnClick(View v) {
            if (mOnTaskClickListener != null) {
                Object taskItem = mTasks.get(getAdapterPosition());
                if (taskItem instanceof Task) {
                    mOnTaskClickListener.onTaskItemClicked((Task) taskItem);
                }
            }
        }
    }
}
