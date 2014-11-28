package unipi.kr.firefist.api;

import org.json.JSONObject;

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
				building.damageRate = jsonBuilding.getDouble("damage_rate");
				building.distance = jsonBuilding.getDouble("distance");

				onSucceeded(building);
			}
			else
				onFailed(data.getString("message"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			onFailed("Exception occurred while get building data from JSON response");
		}
	}


	public abstract void onSucceeded(Building building);

}
