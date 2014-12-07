package unipi.kr.firefist.api;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public class Building
{
	public int id, myRank;
	public double damageRate, distance, myDamage, latitude, longitude, radius;
	public String name, normalPhotoUrl, destroyedPhotoUrl;
	public boolean destroyed = false;

	public Building(){}

	public Building(int id, double damageRate, double distance, String name, boolean destroyed) {
		this.id = id;
		this.damageRate = damageRate;
		this.distance = distance;
		this.name = name;
		this.destroyed = destroyed;
	}

	public String getBefittingUrl()
	{
		return destroyed? destroyedPhotoUrl : normalPhotoUrl;
	}
}
