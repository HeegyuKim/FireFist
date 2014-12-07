package unipi.kr.firefist.api.rest;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimHeeKue on 2014-11-28.
 */

public class Assembler
{
	HttpClient client;
	JSONObject request;
	String url;

	public Assembler(HttpClient client, String url)
	{
		this.client = client;
		this.url = url;
		this.request = new JSONObject();
	}


	public Assembler put(String key, Object value)
	{
		try {
			request.put(key, value);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			return null;
		}
		return this;
	}


	public void asyncPost(HttpAsyncTask.Handler handler)
	{
		HttpAsyncTask task = new HttpAsyncTask(client, url, request, handler);
		task.execute();
	}
}