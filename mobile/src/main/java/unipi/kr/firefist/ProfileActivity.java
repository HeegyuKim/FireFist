package unipi.kr.firefist;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.io.IOException;
import java.sql.Connection;

import unipi.kr.firefist.game.FireFist;
import unipi.kr.firefist.game.GamesAgent;
import unipi.kr.firefist.game.PlayerData;
import unipi.kr.firefist.game.PlayerListener;
import unipi.kr.firefist.game.PlusAdapter;
import unipi.kr.firefist.game.PlusAgent;
import unipi.kr.firefist.utils.ActivityPlus;
import unipi.kr.firefist.utils.VolleySingleton;

public class ProfileActivity
extends ActivityPlus
implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		PlayerListener
{
	private static final int REQ_CONNECT_GOOGLE_API = 1011;

	FireFist firefist;
	GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		firefist = new FireFist(this, this);
		client = new GoogleApiClient.Builder(this)
				.addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN)
				.addScope(Plus.SCOPE_PLUS_PROFILE)
				//.addApi(Games.API)
				//.addScope(Games.SCOPE_GAMES)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onPlayerDataChanged(final PlayerData playerData) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				changePlayerUI(playerData);
			}
		});
	}

	private void changePlayerUI(PlayerData data)
	{
		text_f(R.id.profile_text_best_score, "%.1f %s", data.bestScore, getString(R.string.score_unit));
		text_f(R.id.profile_text_camul_score, "%.1f %s", data.camulScore, getString(R.string.score_unit));
		text_f(R.id.profile_text_exp, "%.1f %s", data.exp, getString(R.string.exp_unit));
		text_f(R.id.profile_text_coin, "%d", data.coins);
		text_(R.id.profile_text_level, data.level);
	}



	@Override
	public void onConnected(Bundle bundle) {
		log_d("Google API", "연결됨");

		Person me = Plus.PeopleApi.getCurrentPerson(client);
		setProfile(me);

		// loadLeaderBoards();
	}


	private void loadLeaderBoards()
	{
		getLeaderBoard(
				R.string.leaderboard_best_score,
				R.id.profile_text_best_score,
				R.string.score_unit
		);
		getLeaderBoard(
				R.string.leaderboard_camul_score,
				R.id.profile_text_camul_score,
				R.string.score_unit
		);
		getLeaderBoard(
				R.string.leaderboard_exp,
				R.id.profile_text_exp,
				R.string.exp_unit
		);

	}

	public void getLeaderBoard(
			int id,
			final int textViewId,
			final int unitStringId
	)
	{
		Games.Leaderboards.loadCurrentPlayerLeaderboardScore (
				client,
				getString(id),
				LeaderboardVariant.TIME_SPAN_ALL_TIME,
				LeaderboardVariant.COLLECTION_PUBLIC
		)
		.setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
		@Override
		public void onResult(Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult)
		{
			if(!loadPlayerScoreResult.getStatus().isSuccess())
			{
				text_(textViewId, "알 수 없음");
				return;
			}


			LeaderboardScore score = loadPlayerScoreResult.getScore();
			text_f(textViewId, "%s %s", score.getDisplayScore(), getString(unitStringId));
		}
		});
	}


	@Override
	public void onConnectionSuspended(int i) {
		log_d("Google API", "연결 중단됨, 재시도");
		client.connect();
	}


	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		log_d("Google API", "연결 실패");
		if(connectionResult.hasResolution())
		{
			log_d("Google API", "오류 해결 시도");
			try
			{
				connectionResult.startResolutionForResult(this, REQ_CONNECT_GOOGLE_API);
			}
			catch(IntentSender.SendIntentException e)
			{
				log_d("Google API", "SendIntentException");
				client.connect();
			}
		}
		else
			toast("Google API에 연결하지 못했습니다.", Toast.LENGTH_LONG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
			case REQ_CONNECT_GOOGLE_API:
			{
				if(resultCode == RESULT_OK)
				{
					log_d("Google API", "오류 해결 성공, 다시 연결");
					client.connect();
				}
				else
				{
					log_d("Google API", "오류 해결 실패...");
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	private void setProfile(Person person)
	{
		NetworkImageView imageView = (NetworkImageView)view_(R.id.profile_image);

		if(person == null)
		{
			toast("내 정보를 얻지 못했습니다", Toast.LENGTH_LONG);
			text_(R.id.profile_text_name, "알 수 없음");
			imageView.setImageResource(R.drawable.firefist_mobile_profile);
			return;
		}

		String url = person.getImage().getUrl();
		url = url.substring(0, url.indexOf('?')) + "?sz=" + imageView.getWidth();
		log_df("Profile Image URL", "URL: %s", url);

		imageView.setImageUrl(
				url,
				VolleySingleton.getInstance(this).getImageLoader()
		);


		text_(R.id.profile_text_name, person.getDisplayName());
	}






	@Override
	protected void onStart() {
		super.onStart();
		client.connect();
		firefist.connect();
	}


	@Override
	protected void onStop() {
		super.onStop();
		client.disconnect();
		firefist.disconnect();
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		// 로그아웃
		if(id == R.id.action_logout)
		{
			Plus.AccountApi.clearDefaultAccount(client);

			Intent it = new Intent(this, SplashActivity.class);
			it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);
			finish();
		}
		else if(id == android.R.id.home)
		{
			onBackPressed();
		}

		return super.onOptionsItemSelected(item);
	}
}
