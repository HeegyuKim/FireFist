package unipi.kr.firefist.api;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import javax.xml.transform.Result;

import unipi.kr.firefist.R;
import unipi.kr.firefist.gaming.PlayerAttributes;
import unipi.kr.firefist.gaming.PlayerHandler;

/**
 * Created by KimHeeKue on 2014-12-06.
 */
public class LeaderBoard
	implements IScoreBoard
{
	private interface Handler
	{
		public void onScore(long score, boolean ok);
	}



	Context context;
	GoogleApiClient client;
	PlayerAttributes attr;
	int loadCount;


	public LeaderBoard(Context context, GoogleApiClient client) {
		this.context = context;
		this.client = client;
		this.attr = new PlayerAttributes();
	}

	public void connect() {

	}

	public void disconnect() {

	}

	private void load(
			int leaderboardStringId,
			final Handler handler
	) {
		Games.Leaderboards.loadCurrentPlayerLeaderboardScore(
				client,
				context.getString(leaderboardStringId),
				LeaderboardVariant.TIME_SPAN_ALL_TIME,
				LeaderboardVariant.COLLECTION_PUBLIC
		)
		.setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
		@Override
		public void onResult(Leaderboards.LoadPlayerScoreResult result) {
			if(result.getStatus().isSuccess() && result.getScore() != null)
				handler.onScore(result.getScore().getRawScore(), true);
			else
				handler.onScore(0, false);
		}});
	}

	private void loadAsync()
	{
		loadCount = 0;

		load(R.string.leaderboard_best_score , new Handler() {
			@Override
			public void onScore(long score, boolean ok) {
				attr.bestScore = score / 100.0;
				if(!ok)
					Log.d("LeaderBoard-Failed", "best score");
				if(++loadCount >= 4)
					onLoadFinish();
			}
		});
		load(R.string.leaderboard_camulated_score , new Handler() {
			@Override
			public void onScore(long score, boolean ok) {
				attr.camulScore = score / 100.0;
				if(!ok)
					Log.d("LeaderBoard-Failed", "camulated score");
				if(++loadCount >= 4)
					onLoadFinish();
			}
		});
		load(R.string.leaderboard_experience , new Handler() {
			@Override
			public void onScore(long score, boolean ok) {
				attr.exp = score / 100.0;
				if(!ok)
					Log.d("LeaderBoard-Failed", "exp");
				if(++loadCount >= 4)
					onLoadFinish();
			}
		});
		load(R.string.leaderboard_coins , new Handler() {
			@Override
			public void onScore(long score, boolean ok) {
				attr.coins = (int)score;
				if(!ok)
					Log.d("LeaderBoard-Failed", "coins");
				if(++loadCount >= 4)
					onLoadFinish();
			}
		});
	}

	private void onLoadFinish()
	{
		Log.d("Leaderboard", "onLoadFinish()!");
		attr.notifyChanged();
	}




	public void loadAttributesTo(PlayerHandler handler)
	{
		if(!client.isConnected()) return;

		attr.setHandler(handler);
		loadAsync();
	}



	private void submit(int leaderboardStringId, long score)
	{
		if(!client.isConnected()) return;

		Games.Leaderboards.submitScore(
				client,
				context.getString(leaderboardStringId),
				score
		);
	}


	public void saveAttributesFrom(PlayerAttributes attr)
	{
		if(!client.isConnected()) return;

		submit(R.string.leaderboard_best_score, (long)(attr.bestScore * 100));
		submit(R.string.leaderboard_camulated_score, (long)(attr.camulScore * 100));
		submit(R.string.leaderboard_experience, (long)(attr.exp * 100));
		submit(R.string.leaderboard_coins, attr.coins);
	}


	public void addScore(double score)
	{
		if(!client.isConnected()) return;

		Log.d("Leaderboard", "add score");
		attr.camulScore += score;
		submit(R.string.leaderboard_camulated_score, (long)(attr.camulScore * 100));
	}
	public void setScore(double score)
	{
		if(!client.isConnected()) return;

		attr.camulScore = score;
		submit(R.string.leaderboard_camulated_score, (long)(attr.camulScore * 100));
	}


	public void setBestScore(double score)
	{
		if(!client.isConnected()) return;

		attr.bestScore = score;
		submit(R.string.leaderboard_best_score, (long)(attr.bestScore * 100));
	}


	public void addExp(double exp)
	{
		if(!client.isConnected()) return;

		attr.exp += exp;
		submit(R.string.leaderboard_experience, (long)(attr.exp * 100));
	}
	public void setExp(double exp)
	{
		if(!client.isConnected()) return;

		attr.exp = exp;
		submit(R.string.leaderboard_experience, (long)(attr.exp * 100));
	}


	public void addCoins(int coins)
	{
		if(!client.isConnected()) return;

		attr.coins += coins;
		submit(R.string.leaderboard_coins, attr.coins);
	}


	public void setCoins(int coins)
	{
		if(!client.isConnected()) return;

		attr.coins = coins;
		submit(R.string.leaderboard_coins, attr.coins);
	}


	public void clear()
	{
		if(!client.isConnected()) return;

		setExp(0);
		setCoins(0);
		setBestScore(0);
		setScore(0);
	}
}
