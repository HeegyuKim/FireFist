package unipi.kr.firefist.api.rest;

import org.json.JSONObject;

import unipi.kr.firefist.api.Building;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public abstract class AccessibleBuildingHandler extends JSONHandler
{
	@Override
	public final void onSucceeded(JSONObject data)
	{
		try {
			if (data.getBoolean("ok")) {
				JSONObject jsonBuilding = data.getJSONObject("building");
				Building building = new Building();
				building.id = jsonBuilding.getInt("id");
				building.name = jsonBuilding.getString("name");
				building.normalPhotoUrl = jsonBuilding.getString("photo_normal");
				building.destroyedPhotoUrl = jsonBuilding.getString("photo_destroy");
				building.damageRate = jsonBuilding.getDouble("damage_rate");
				building.distance = jsonBuilding.getDouble("distance");
				building.latitude = jsonBuilding.getDouble("latitude");
				building.longitude = jsonBuilding.getDouble("longitude");
				building.radius = jsonBuilding.getDouble("radius");
				building.myDamage = jsonBuilding.getDouble("my_damage");
				building.myRank = jsonBuilding.getInt("my_rank");

				onSucceeded(building);
			}
			else
			{
				onFailed(data.getString("message"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			onFailed("Exception occurred while get building data from JSON response");
		}
	}


	public abstract void onSucceeded(Building building);

}
