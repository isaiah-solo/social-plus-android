package me.zayz.socialplus.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

import me.zayz.socialplus.R;
import me.zayz.socialplus.instagram.Instagram;
import me.zayz.socialplus.interfaces.ActivityCallback;
import me.zayz.socialplus.models.InstagramStats;
import me.zayz.socialplus.models.InstagramUser;
import me.zayz.socialplus.models.SocialPlusUser;
import me.zayz.socialplus.views.EngagementFragment;
import me.zayz.socialplus.views.HomeFragment;

/**
 * Created by zayz on 10/10/17.
 * <p>
 * Main activity that houses entire app.
 */
public class MainActivity extends AppCompatActivity implements ActivityCallback {

    private Instagram mInstagram;
    private List<Fragment> mFragments;
    private List<Integer> mSelectedMenuItemHistory;

    private boolean needLogin;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int itemId = item.getItemId();
            int lastSelected = mSelectedMenuItemHistory.size() > 0
                    ? mSelectedMenuItemHistory.get(mSelectedMenuItemHistory.size() - 1)
                    : R.id.navigation_home;

            if (lastSelected == itemId) {
                return false;
            }

            switch (itemId) {
                case R.id.navigation_home:
                    setFragment(new HomeFragment(),
                            android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                            R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.navigation_engagement:
                    setFragment(new EngagementFragment(),
                            R.anim.slide_in_right, R.anim.slide_out_left,
                            android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    break;
                default:
                    return false;
            }

            mSelectedMenuItemHistory.add(itemId);

            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        needLogin = true;

        mInstagram = new Instagram(this, new Instagram.InstagramAuthListener() {

            @Override
            public void onSuccess() {

                mInstagram.getStats(new InstagramStats.Callback() {

                    @Override
                    public void onFinish() {

                        needLogin = false;
                        invalidateOptionsMenu();
                        setupApp();
                    }

                    @Override
                    public void onError() {
                    }
                });
            }

            @Override
            public void onNeedLogin() {

                setupLogin();
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem logoutItem = menu.getItem(0);
        logoutItem.setVisible(!needLogin);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_logout: {
                needLogin = true;
                invalidateOptionsMenu();
                mInstagram.resetSession();
                setupLogin();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (needLogin) {
            super.onBackPressed();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();

        Fragment lastFragment = mFragments.get(mFragments.size() - 1);
        FragmentManager childFm = lastFragment.getChildFragmentManager();

        if (childFm.getBackStackEntryCount() > 0) {
            childFm.popBackStackImmediate();
            return;
        }

        if (fm.getBackStackEntryCount() == 0 && mFragments.size() < 2) {
            super.onBackPressed();
            return;
        }

        fm.popBackStackImmediate();

        if (mSelectedMenuItemHistory.size() > 0) {
            mSelectedMenuItemHistory.remove(mSelectedMenuItemHistory.size() - 1);
        }
    }

    /**
     * Sets tab fragment providing sliding animations
     *
     * @param fragment     Fragment to set
     * @param createEnter  Create entering animation
     * @param createExit   Create exiting animation
     * @param destroyEnter Destroy entering animation
     * @param destroyExit  Destroy exiting animation
     */
    private void setFragment(Fragment fragment, int createEnter, int createExit,
                             int destroyEnter, int destroyExit) {

        String lastFragmentName = mFragments.get(mFragments.size() - 1).getClass().getSimpleName();
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(createEnter, createExit, destroyEnter, destroyExit);
        ft.addToBackStack(lastFragmentName);
        ft.replace(R.id.content, fragment);
        ft.commit();
    }

    /**
     * Sets up app view.
     */
    private void setupApp() {

        setContentView(R.layout.activity_main);

        mFragments = new ArrayList<>();
        mSelectedMenuItemHistory = new ArrayList<>();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setItemIconTintList(getResources().getColorStateList(R.color.navigation_item));
        navigation.setItemTextColor(getResources().getColorStateList(R.color.navigation_item));
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fm = getSupportFragmentManager();

        fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {

                super.onFragmentAttached(fm, f, context);

                mFragments.add(f);
            }

            @Override
            public void onFragmentDetached(FragmentManager fm, Fragment f) {

                super.onFragmentDetached(fm, f);

                mFragments.remove(f);
            }
        }, false);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, new HomeFragment());
        ft.commit();
    }

    /**
     * Sets up login view.
     */
    private void setupLogin() {
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInstagram.authorize();
            }
        });
    }

    @Override
    public Instagram getInstagram() {
        return mInstagram;
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void setNavigation(int menuItem) {
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        MenuItem menuItemView = navigationView.getMenu().findItem(menuItem);
        menuItemView.setChecked(true);
    }
}
