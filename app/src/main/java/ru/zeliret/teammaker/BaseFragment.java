package ru.zeliret.teammaker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

public abstract class BaseFragment extends Fragment {
    /**
     * @return True if we can go back
     */
    public boolean onBackPressed() {
        return true;
    }

    protected MainActivity getHostActivity() {
        return (MainActivity) getActivity();
    }

    protected void setupActionBar(final ActionBar actionBar) {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupActionBar(getHostActivity().getSupportActionBar());
    }

    protected void finish(){
        getHostActivity().goBack();
    }

    protected ActionBar getActionBar(){
        return getHostActivity().getSupportActionBar();
    }
}
