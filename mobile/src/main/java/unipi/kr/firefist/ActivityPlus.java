package unipi.kr.firefist;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by KimHeekue on 2014-11-12.
 */
public abstract class ActivityPlus
        extends Activity{

    public View view_(int id)
    {
        return findViewById(id);
    }

    public void onClick(int id, View.OnClickListener lis)
    {
        view_(id).setOnClickListener(lis);
    }

    public void toast(String text, int duration)
    {
        Toast.makeText(this, text, duration).show();
    }





    public TextView textView_(int id)
    {
        return (TextView)findViewById(id);
    }

    public String text_(int id)
    {
        return textView_(id).getText().toString();
    }

    public void text_(int id, Object text)
    {
        textView_(id).setText(text.toString());
    }

    public void text_f(int id, String format, Object ...args)
    {
        text_(id, String.format(format, args));
    }

}
