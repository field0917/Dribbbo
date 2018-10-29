package com.jiuzhang.yeyuan.dribbbo.user_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.ImageUtils;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<User> users;
    private boolean showLoading = false;

    public UserListAdapter (List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_USER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                return new UserListViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item_activity, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_USER) {
            UserListViewHolder userListViewHolder = (UserListViewHolder) holder;
            User user = users.get(position);
            final Context context = holder.itemView.getContext();
            ImageUtils.loadCircleUserImage(context, user.getProfileImageURL(), userListViewHolder.userImage);
            userListViewHolder.name.setText(user.name);
            userListViewHolder.username.setText("@" + user.username);
            userListViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Clicked!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? users.size() + 1 : users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < users.size() ? VIEW_TYPE_USER : VIEW_TYPE_LOADING;
    }

    public void setShowLoading (boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public void append(@NonNull List<User> moreUsers) {
        users.addAll(moreUsers);
        notifyDataSetChanged();
    }

    public void setData(@NonNull List<User> data) {
        users.clear();
        users.addAll(data);
        notifyDataSetChanged();
    }

    public List<User> getData() {
        return users;
    }
}
