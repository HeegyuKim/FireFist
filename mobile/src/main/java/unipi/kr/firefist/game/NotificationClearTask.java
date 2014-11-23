package unipi.kr.firefist.game;

import android.widget.TextView;

/**
 * Created by KimHeekue on 2014-11-04.
 */
public class NotificationClearTask implements  Runnable {

    TextView textView;
    boolean activation;
    boolean finished;

    public NotificationClearTask(TextView textView)
    {
        this.textView = textView;
        this.activation = true;
        this.finished = false;
    }

    public void SetActivation(boolean activation)
    {
        this.activation = activation;
    }

    public boolean isFinished()
    {
        return finished;
    }


    @Override
    public void run() {
        if(activation)
            textView.setText("");
        finished = true;
    }
}
