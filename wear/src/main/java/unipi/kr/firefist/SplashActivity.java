package unipi.kr.firefist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goNext();
            }
        }, 2000);
    }

    private void goNext()
    {
        //Intent it = new Intent(this, GameActivity.class);
        //startActivity(it);
        finish();
    }
}
