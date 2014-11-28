package unipi.kr.firefist.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public class HttpAsyncTask extends AsyncTask<Void, Void, Object>
{
	public static interface Handler
	{
		Object parse(HttpResponse res) throws Exception;

		void onSucceeded(Object data);

		void onFailed(String message);
	}


	HttpClient client;
	Handler handler;
	JSONObject request;
	String server, errorMessage;


	public HttpAsyncTask(
			HttpClient client,
			String server,
			JSONObject request,
			Handler handler
	)
	{
		this.request = request;
		this.client = client;
		this.handler = handler;
		this.server = server;
	}



	@Override
	protected Object doInBackground(Void... voids)
	{
		HttpPost post = new HttpPost(server);
		HttpResponse response = null;

		try
		{
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("json", request.toString()));
			post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));

			response = client.execute(post);
		}
		catch(Exception e)
		{
			Log.d("예외", "연걸 중 예외 발생.");
			errorMessage = e.getMessage();
			e.printStackTrace();
			return null;
		}


		try
		{
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != 200)
			{
				Log.d("Status Failed", "Code " + statusCode);
				errorMessage = "Response status code is " + statusCode + ", not OK(200).";
				return null;
			}

			return handler.parse(response);
		}
		catch(Exception e)
		{
			Log.d("예외", "응답 처리 중 예외 발생");
			errorMessage = e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Object data) {
		if(data == null)
			handler.onFailed(errorMessage);
		else
			handler.onSucceeded(data);
	}
}
