package unipi.kr.firefist.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
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



	public void log_d(String tag, String text)
	{
		Log.d(tag, text);
	}
	public void log_df(String tag, String format, Object ...args)
	{
		Log.d(tag, String.format(format, args));
	}


	public void alert(String message,
	                         AlertDialog.OnClickListener lis)
	{
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, lis)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}

	public void alert(String message, String title,
	                         AlertDialog.OnClickListener lis)
	{
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, lis)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}


	public String string(int id)
	{
		return getString(id);
	}
}
