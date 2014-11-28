package unipi.kr.firefist.api;

import org.json.JSONObject;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public abstract class DamageHandler extends JSONHandler
{
	@Override
	public final void onSucceeded(JSONObject data)
	{
		try {
			if (data.getBoolean("ok")) {
				onSucceeded(
						data.getDouble("damage_rate"),
						data.getBoolean("destroy")
				);
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


	public abstract void onSucceeded(double damageRate, boolean destroyed);


}
