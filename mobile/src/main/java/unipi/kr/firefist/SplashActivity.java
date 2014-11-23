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

import unipi.kr.firefist.utils.ActivityPlus;

public class SplashActivity extends ActivityPlus
		implements GoogleApiClient.OnConnectionFailedListener,
		GoogleApiClient.ConnectionCallbacks,
		View.OnClickListener
{
	private static final int REQUEST_PLUS_SIGNIN = 10101,
			REQUEST_PLUS_CONNECTION_ERROR = 10102,
			REQUEST_GAME_SIGNIN_ERROR = 12312;


	GoogleApiClient client;
	ConnectionResult result;
	ProgressDialog dialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		onClick(R.id.splash_button_googleplus_signin, this);

		client = new GoogleApiClient.Builder(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.addScope(Plus.SCOPE_PLUS_PROFILE)
				//.addApi(Games.API)
				//.addScope(Games.SCOPE_GAMES)
				.addOnConnectionFailedListener(this)
				.addConnectionCallbacks(this)
				.build();
		dialog = ProgressDialog.show(this, "", getString(R.string.connecting));
	}

	@Override
	protected void onStart() {
		super.onStart();
		client.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(client != null && client.isConnected())
		{
			client.disconnect();
		}
	}

	int errorCode;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
			case REQUEST_PLUS_CONNECTION_ERROR:
			{
				if(resultCode == RESULT_OK)
				{
					Log.d("Google+", "재연결");
					client.connect();
				}
				else
				{
					Log.d("Google+", "재연결 실패...");
				}
				break;
			}
			case REQUEST_GAME_SIGNIN_ERROR:
			{
				if(resultCode == RESULT_OK)
				{
					Log.d("Google Play Game", "재연결 성공");
				}
				else
				{
					Log.d("Google Play Game", "재연결 실패, 에러 코드 " + errorCode );
					BaseGameUtils.showActivityResultError(
							this,
							requestCode,
							R.string.sign_in_failed,
							errorCode,
							R.string.signin_other_error
					);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("Google+", "연결 실패;");

		dialog.dismiss();
		dialog = null;
		view_(R.id.splash_button_googleplus_signin).setVisibility(View.VISIBLE);


		if(connectionResult.hasResolution())
		{
			try {
				Log.d("Google+", "연결 재시도");
				connectionResult.startResolutionForResult(
						this,
						REQUEST_PLUS_CONNECTION_ERROR
				);
			}
			catch(IntentSender.SendIntentException e) {
				client.connect();
			}
		}
		else
		{
			toast("Google+ 연결이 안되네요...", Toast.LENGTH_LONG);
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("Google+", "연결됨!");
		if(dialog != null)
			dialog.dismiss();
		goNext();
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("Google+", "연결 중단됨!");
	}


	@Override
	public void onClick(View view) {

		if(dialog == null)
		{
			dialog = ProgressDialog.show(this, "", getString(R.string.connecting));
		}
		client.connect();
	}


	private void goNext()
	{
		Intent it = new Intent(this, MainActivity.class);
		startActivity(it);
		finish();
	}
}
