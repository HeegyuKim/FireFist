package unipi.kr.firefist.api.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public class Cracker {

	public static final String URL_FIREFIST =
			"http://applepi.kr/~summelier/app/firefist.php",
			URL_FIREFIST_BUILDING_IMAGE =
					"http://applepi.kr/~summelier/app/buildings/"
			;

	HttpClient client;

	public Cracker()
	{
		client = new DefaultHttpClient();
	}


	private Assembler assemble()
	{
		return new Assembler(client, URL_FIREFIST);
	}

	public Assembler getAccessibleBuilding(String playerId, double lat, double lng)
	{
		return assemble()
				.put("method", "building_accessible")
				.put("player_id", playerId)
				.put("lat", lat)
				.put("lng", lng);
	}

	public Assembler damage(String playerId, int buildingId, double damage)
	{
		return assemble()
				.put("method", "damage")
				.put("building_id", buildingId)
				.put("player_id", playerId)
				.put("damage", damage);
	}

	public String getBuildingImageUrl(int id, boolean destroyed)
	{
		String ext = ".png";

		if(!destroyed)
		{
			return URL_FIREFIST_BUILDING_IMAGE + id + ext;
		}
		else
			return URL_FIREFIST_BUILDING_IMAGE + id + "_destroyed" + ext;
	}
}
