package unipi.kr.firefist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import unipi.kr.firefist.api.GooglePlus;
import unipi.kr.firefist.utils.ActivityPlus;

public class SplashActivity extends ActivityPlus
		implements
		View.OnClickListener
{
	public static final String ACCOUNT_NONE = "none",
								ACCOUNT_GOOGLE_PLUS = "google_plus";

	GooglePlus plus;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		onClick(this,
				R.id.splash_button_googleplus_signin,
				R.id.btn_just_go
		);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(plus != null)
			plus.disconnect();
	}

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.btn_just_go:
			{
				log_d("Splash", "Just Go Clicked");
				goNext(ACCOUNT_NONE);
				break;
			}
			case R.id.splash_button_googleplus_signin:
			{
				log_d("Splash", "Google+ SignIn Clicked");
				signInGooglePlus();
				break;
			}
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == GooglePlus.REQUEST_RESOLVE_FAILED)
		{
			if(resultCode == RESULT_OK)
			{
				plus.connect();
			}
			else
			{
				toast(string(R.string.could_not_signin_google_plus), Toast.LENGTH_LONG);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	private void goNext(String account)
	{
		Intent it = new Intent(this, MainActivity.class);
		it.putExtra("account", account);
		startActivity(it);
		finish();
	}



	private void signInGooglePlus()
	{
		plus = new GooglePlus(this, null);
		plus.setResolveFailed(this, true);
		plus.client.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
			@Override
			public void onConnected(Bundle bundle) {
				goNext(ACCOUNT_GOOGLE_PLUS);
			}

			@Override
			public void onConnectionSuspended(int i) {

			}
		});
		plus.connect();
	}
}
