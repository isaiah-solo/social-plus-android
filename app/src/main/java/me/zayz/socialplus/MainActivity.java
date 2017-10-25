package me.zayz.socialplus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

import me.zayz.socialplus.instagram.Instagram;
import me.zayz.socialplus.instagram.InstagramUser;

public class MainActivity extends AppCompatActivity {
    private Instagram mInstagram;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        mInstagram = new Instagram(this);

        // Loads app if session is active
        if (mInstagram.sessionIsActive()) {
            setupApp();
        }

        // Loads login screen if session is not active
        else {
            setupLoginScreen();
        }
    }

    private void setupLoginScreen() {
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInstagram.authorize(new Instagram.InstagramAuthListener() {
                    @Override
                    public void onSuccess(InstagramUser user) {
                        setupApp();
                    }

                    @Override
                    public void onError(String error) {
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    private void setupApp() {
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
