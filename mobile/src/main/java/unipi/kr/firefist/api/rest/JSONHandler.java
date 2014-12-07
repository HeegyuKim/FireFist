package unipi.kr.firefist.api.rest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public abstract class JSONHandler implements HttpAsyncTask.Handler
{

	@Override
	public Object parse(HttpResponse res) throws Exception
	{
		HttpEntity entity = res.getEntity();
		String jsonText = EntityUtils.toString(entity, "UTF-8");

		try
		{
			return new JSONObject(jsonText);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Exception occurred while parsing the JSON text. \n" + jsonText);
		}
	}


	@Override
	public final void onSucceeded(Object data) {
		onSucceeded((JSONObject) data);
	}

	public abstract void onSucceeded(JSONObject data);

	@Override
	public void onFailed(String message)
	{

	}
}
