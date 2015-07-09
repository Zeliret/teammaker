package ru.zeliret.teammaker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFm();

        navigate(new ContactsFragment(), true);
    }

    private void setupFm() {
        fm = getSupportFragmentManager();
        updateHomeButton();
    }

    @Override
    protected void onStart() {
        super.onStart();

        fm.addOnBackStackChangedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        fm.removeOnBackStackChangedListener(this);
    }

    public void navigate(final Fragment fragment) {
        navigate(fragment, false);
    }

    private void navigate(final Fragment fragment, final boolean root) {
        if (!root || fm.getBackStackEntryCount() == 0) {
            Fragment currentFragment = fm.findFragmentById(R.id.content);
            if (null == currentFragment || !currentFragment.getClass().equals(fragment.getClass())) {
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }

    @Override
    public void onBackStackChanged() {
        updateHomeButton();

        int entryCount = fm.getBackStackEntryCount();
        if (entryCount == 0)
            finish();
    }

    private void updateHomeButton() {
        updateHomeButton(fm.getBackStackEntryCount() > 1);
    }

    private void updateHomeButton(final boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar)
            actionBar.setDisplayHomeAsUpEnabled(show);
    }
}
