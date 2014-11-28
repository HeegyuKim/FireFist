package unipi.kr.firefist.gaming;

/**
 * Created by KimHeeKue on 2014-11-26.
 */
public class PlayerAttributes
{
	public double exp, bestScore, camulScore;
	public int coins;
	public PlayerHandler handler;

	public PlayerAttributes() {
	}

	public PlayerAttributes(int coins, double exp, double bestScore, double camulScore) {
		this.coins = coins;
		this.exp = exp;
		this.bestScore = bestScore;
		this.camulScore = camulScore;
	}

	public void set(PlayerAttributes attr)
	{
		coins = attr.coins;
		exp = attr.exp;
		bestScore = attr.bestScore;
		camulScore = attr.camulScore;
	}

	public void notifyChanged()
	{
		if(handler != null)
			handler.onPlayerChanged(this);
	}


	public double getExp() {
		return exp;
	}

	public void setExp(double exp) {
		this.exp = exp;
	}

	public double getBestScore() {
		return bestScore;
	}

	public void setBestScore(double bestScore) {
		this.bestScore = bestScore;
	}

	public double getCamulScore() {
		return camulScore;
	}

	public void setCamulScore(double camulScore) {
		this.camulScore = camulScore;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public PlayerHandler getHandler() {
		return handler;
	}

	public void setHandler(PlayerHandler handler) {
		this.handler = handler;
	}

}
