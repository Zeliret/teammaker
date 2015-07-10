package ru.zeliret.teammaker;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TeamsFragment extends BaseFragment {
    public static final String KEY_PLAYERS = "players";

    public static TeamsFragment newInstance(final ArrayList<Player> players) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_PLAYERS, players);

        TeamsFragment fragment = new TeamsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public TeamsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void setupActionBar(final ActionBar actionBar) {
        if (null != actionBar)
            actionBar.setTitle(R.string.title_teams);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
