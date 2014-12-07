package unipi.kr.firefist.api;

import unipi.kr.firefist.gaming.PlayerAttributes;
import unipi.kr.firefist.gaming.PlayerHandler;

/**
 * Created by KimHeeKue on 2014-11-26.
 */
public interface IScoreBoard {

	public static final String
			PREFERENCE_NAME = "LocalScoreBoard",
			KEY_BEST_SCORE = "best-score",
			KEY_CAMULATED_SCORE = "camulated-score",
			KEY_EXPERIENCE = "experience",
			KEY_COINS = "coins"
					;

	public void connect();
	public void disconnect();

	public void loadAttributesTo(PlayerHandler handler);
	public void saveAttributesFrom(PlayerAttributes attr);

	public void addScore(double score);
	public void setScore(double score);


	public void setBestScore(double score);


	public void addExp(double exp);
	public void setExp(double exp);


	public void addCoins(int coins);
	public void setCoins(int coins);


	public void clear();
}
