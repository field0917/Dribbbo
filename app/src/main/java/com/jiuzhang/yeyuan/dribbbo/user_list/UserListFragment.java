package com.jiuzhang.yeyuan.dribbbo.user_list;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.EmptyRecyclerView;
import com.jiuzhang.yeyuan.dribbbo.base.EndlessRecyclerViewScrollListener;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.model.User;
import com.jiuzhang.yeyuan.dribbbo.utils.Utils;
import com.jiuzhang.yeyuan.dribbbo.utils.VerticalSpaceItemDecoration;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListFragment extends Fragment {

    private static final String KEY_QUERY = "query";
    private static final int VERTICAL_SPACE_HEIGHT = 20;

    private static final int NO_INTERNET_CONNECTION = 0;
    private static final int LIST_TYPE_SEARCH_USER = 1;

    @BindView(R.id.shot_list_recycler_view) EmptyRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_view) TextView emptyView;

    private UserListAdapter adapter;
    private List<User> users = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private int listType;

    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance(String query) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new UserListAdapter(users);
        if (!Utils.isConnectedToInternet(getContext())) {
            listType = NO_INTERNET_CONNECTION;
        } else {
            listType = LIST_TYPE_SEARCH_USER;
        }
        if (!isLoading) {
            SearchUserTask task = new SearchUserTask (false, currentPage);
            task.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setEnabled(false); // During the first loading, disable swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                swipeRefreshLayout.setRefreshing(true); // Enable the refresh icon
                SearchUserTask task = new SearchUserTask(true, currentPage);
                task.execute();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_SPACE_HEIGHT));

        recyclerView.setAdapter(adapter);

        setEmptyViewText(listType);

        recyclerView.setEmptyView(emptyView);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener() {
            @Override
            public void onLoadMore() {
                if (!isLoading) {
                    new SearchUserTask(false, currentPage).execute();
                }
            }
        });
    }

    private void setEmptyViewText(int listType) {
        switch (listType) {
            case LIST_TYPE_SEARCH_USER:
                emptyView.setText(R.string.no_search_result);
                break;
            case NO_INTERNET_CONNECTION:
                emptyView.setText(R.string.no_internet);
        }
    }

    private class SearchUserTask extends WendoTask<Void, Void, List<User>> {

        boolean refresh;
        int page;
        String query;

        private SearchUserTask (boolean refresh, int page) {
            this.refresh = refresh;
            this.page = page;
        }

        @Override
        protected void onPreExecute() {
            isLoading = !isLoading;
            adapter.setShowLoading(isLoading);
        }

        @Override
        public List<User> doOnNewThread(Void... voids) throws Exception {
            query = getArguments().getString(KEY_QUERY);
            return Wendo.getSearchedUsers(query, page);
        }

        @Override
        public void onSuccess(List<User> users) {
            if (users != null) {
                if (refresh) {
                    adapter.setData(users);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Users refreshed!", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.append(users);
                }
                swipeRefreshLayout.setEnabled(true);
                currentPage += 1;
                isLoading = !isLoading;
            } else {
                Snackbar.make(getView(), "Error!", Snackbar.LENGTH_LONG).show();
            }
            adapter.setShowLoading(isLoading);
        }

        @Override
        public void onFailed(Exception e) {
            Snackbar.make(getView(), "Something went wrong with the server", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Try again", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO:reload what did not load
                            Toast.makeText(getContext(), "reload!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
            adapter.setShowLoading(false);
        }
    }
}
