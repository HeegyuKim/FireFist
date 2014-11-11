package unipi.kr.firefist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener
{

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.main_content:
                Intent it = new Intent(MainActivity.this, MeasuringActivity.class);
                startActivity(it);
                break;
            case R.id.main_exit:
                finish();
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                View content = stub.findViewById(R.id.main_content);
                content.setOnClickListener(MainActivity.this);

                View btn = stub.findViewById(R.id.main_exit);
                btn.setOnClickListener(MainActivity.this);
            }
        });

    }


}
