package unipi.kr.firefist.gaming;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by KimHeeKue on 2014-11-26.
 */
public class LocalScoreBoard implements IScoreBoard
{

	SharedPreferences pref;
	SharedPreferences.Editor editor;

	public LocalScoreBoard(Context context)
	{
		pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	@Override
	public void setBestScore(double score) {
		editor = pref.edit();
		editor.putFloat(KEY_BEST_SCORE, (float)score);
		editor.commit();
	}

	@Override
	public void setCoins(int coins) {
		editor = pref.edit();
		editor.putInt(KEY_COINS, coins);
		editor.commit();
	}

	@Override
	public void setExp(double exp) {
		editor = pref.edit();
		editor.putFloat(KEY_EXPERIENCE, (float)exp);
		editor.commit();
	}

	@Override
	public void setScore(double score) {
		editor = pref.edit();
		editor.putFloat(KEY_CAMULATED_SCORE, (float)score);
		editor.commit();
	}

	@Override
	public void addCoins(int coins) {
		coins += pref.getInt(KEY_COINS, 0);

		editor = pref.edit();
		editor.putInt(KEY_COINS, coins);
		editor.commit();
	}

	@Override
	public void addExp(double exp) {
		exp += pref.getFloat(KEY_EXPERIENCE, 0);

		editor = pref.edit();
		editor.putFloat(KEY_EXPERIENCE, (float)exp);
		editor.commit();
	}

	@Override
	public void addScore(double score) {
		score += pref.getFloat(KEY_CAMULATED_SCORE, 0);

		editor = pref.edit();
		editor.putFloat(KEY_CAMULATED_SCORE, (float)score);
		editor.commit();
	}


	@Override
	public void loadAttributesTo(PlayerAttributes attr) {
		attr.bestScore = pref.getFloat(KEY_BEST_SCORE, 0);
		attr.camulScore = pref.getFloat(KEY_CAMULATED_SCORE, 0);
		attr.coins = pref.getInt(KEY_COINS, 0);
		attr.exp = pref.getFloat(KEY_EXPERIENCE, 0);
		attr.notifyChanged();
	}

	@Override
	public void saveAttributesTo(PlayerAttributes attr) {
		editor = pref.edit();
		editor.putFloat(KEY_BEST_SCORE, (float)attr.bestScore);
		editor.putFloat(KEY_CAMULATED_SCORE, (float)attr.camulScore);
		editor.putFloat(KEY_EXPERIENCE, (float)attr.exp);
		editor.putInt(KEY_COINS, attr.coins);
		editor.commit();
	}

	@Override
	public void clear() {
		pref.edit().clear().commit();
	}
}
