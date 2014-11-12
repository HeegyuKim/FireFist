package unipi.kr.firefist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import unipi.kr.firefist.game.FireFist;
import unipi.kr.firefist.game.PlayerData;
import unipi.kr.firefist.game.PlayerListener;


public class MainActivity
        extends ActivityPlus
    implements PlayerListener
{

    FireFist firefist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firefist = new FireFist(this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firefist.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        firefist.disconnect();
    }

    @Override
    public void onPlayerDataChanged(final PlayerData playerData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("메인", "텍스트뷰 값들 바뀌었음");
                text_f(R.id.main_text_exp, "%.2f xp", playerData.exp);
                text_f(R.id.main_text_best_score, "%.1f N", playerData.bestScore);
                text_f(R.id.main_text_camul_score, "%.1f N", playerData.camulScore);
                text_f(R.id.main_text_coin, "%d", playerData.coins);
                text_(R.id.main_text_name, playerData.name);
                text_(R.id.main_text_level, playerData.level);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent it = new Intent(this, SettingsActivity.class);
            startActivity(it);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
