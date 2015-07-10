package ru.zeliret.teammaker;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private FragmentManager fm;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFm();

        setupRoot();
    }

    private void setupRoot() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(new ContactsFragment(), true);
            }
        }, 100);
    }

    private void setupFm() {
        fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);
        updateHomeButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fm.removeOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        onBackPressed(false);
    }

    private void onBackPressed(final boolean force){
        BaseFragment fragment = (BaseFragment) fm.findFragmentById(R.id.content);
        if( force || null == fragment || fragment.onBackPressed() ){
            super.onBackPressed();
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

    public void goBack() {

    }
}
