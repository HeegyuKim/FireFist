package unipi.kr.firefist.api;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public class Building
{
	public int id;
	public double damageRate, distance;
	public String name;

	public Building(){}

	public Building(int id, double damageRate, double distance, String name) {
		this.id = id;
		this.damageRate = damageRate;
		this.distance = distance;
		this.name = name;
	}
}
