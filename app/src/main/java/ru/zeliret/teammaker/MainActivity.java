package ru.zeliret.teammaker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (processFragmentStack())
                    return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean processFragmentStack() {
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return true;
        }

        return false;
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
                transaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                transaction.addToBackStack(null);
                transaction.replace(R.id.content, fragment);
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
