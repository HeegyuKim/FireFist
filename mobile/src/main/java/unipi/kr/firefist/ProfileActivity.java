package unipi.kr.firefist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import unipi.kr.firefist.api.Container;
import unipi.kr.firefist.api.GooglePlus;
import unipi.kr.firefist.api.Watch;
import unipi.kr.firefist.api.IScoreBoard;
import unipi.kr.firefist.gaming.LevelTable;
import unipi.kr.firefist.gaming.LocalScoreBoard;
import unipi.kr.firefist.gaming.PlayerAttributes;
import unipi.kr.firefist.gaming.PlayerHandler;
import unipi.kr.firefist.utils.ActivityPlus;
import unipi.kr.firefist.utils.VolleySingleton;

public class ProfileActivity
extends ActivityPlus
implements PlayerHandler,
		Watch.Handler,
		GoogleApiClient.ConnectionCallbacks
{
	private static final int REQ_CONNECT_GOOGLE_API = 1011;

	Container container;

	IScoreBoard board;
	GooglePlus plus;
	Watch watch;

	PlayerAttributes attributes;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		initFeatures();
	}

	private void initFeatures()
	{
		container = new Container();

		plus = new GooglePlus(this, container);
		plus.setResolveFailed(this, true);
		plus.client.registerConnectionCallbacks(this);
		board = plus.leaderBoard;

		watch = new Watch(this, container);
		watch.setHandler(this);

		attributes = new PlayerAttributes();
		attributes.setHandler(this);
	}




	@Override
	protected void onStart() {
		super.onStart();
		container.setEnable(true);
	}


	@Override
	protected void onStop() {
		super.onStop();
		container.setEnable(false);
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
			if(plus.isConnected())
				Plus.AccountApi.clearDefaultAccount(plus.client);
			watch.clearData();
			board.clear();


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







	// 시계에서 정보가 왔을 때,
	@Override
	public void onWatchMeasured(PlayerAttributes watchAttr, Watch watch)
	{
		if(attributes.bestScore < watchAttr.bestScore)
			attributes.bestScore = watchAttr.bestScore;
		attributes.camulScore += watchAttr.camulScore;
		attributes.exp += watchAttr.exp;
		attributes.coins += watchAttr.coins;
		attributes.notifyChanged();
		watch.clearData();
	}

	@Override
	public void onPlayerChanged(PlayerAttributes attr) {
		log_d("Profile", "Player Attributes has changed");
		showAttributes(attr);
		board.saveAttributesFrom(attr);
	}

	private void showAttributes(PlayerAttributes attr)
	{
		text_f(R.id.profile_text_best_score,
				"%.1f %s",
				attr.bestScore,
				string(R.string.score_unit)
				);
		text_f(R.id.profile_text_camul_score,
				"%.1f %s",
				attr.camulScore,
				string(R.string.score_unit)
				);
		text_f(R.id.profile_text_exp,
				"%.1f %s",
				attr.exp,
				string(R.string.exp_unit)
				);
		text_f(R.id.profile_text_coin,
				"%d %s",
				attr.coins,
				string(R.string.coin_unit)
				);

		LevelTable table = new LevelTable(this);
		text_(R.id.profile_text_level, table.findLevel(attr.exp));
	}






	@Override
	public void onConnected(Bundle bundle) {
		log_d("Google API", "연결됨");

		Person me = Plus.PeopleApi.getCurrentPerson(plus.client);
		setProfile(me);

		board.loadAttributesTo(this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		log_d("Google API", "연결 중단됨, 재시도");
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
			case GooglePlus.REQUEST_RESOLVE_FAILED:
			{
				if(resultCode == RESULT_OK)
				{
					log_d("Google API", "오류 해결 성공, 다시 연결");
					plus.connect();
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

		log_df("Plus ID", person.getId());
		String url = person.getImage().getUrl();
		url = url.substring(0, url.indexOf('?')) + "?sz=" + imageView.getWidth();
		log_df("Profile Image URL", "URL: %s", url);

		imageView.setImageUrl(
				url,
				VolleySingleton.getInstance(this).getImageLoader()
		);


		text_(R.id.profile_text_name, person.getDisplayName());
	}




}
