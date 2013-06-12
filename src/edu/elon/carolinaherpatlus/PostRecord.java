package edu.elon.carolinaherpatlus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class PostRecord extends AsyncTask<String, Boolean, String> {

	@Override
	protected String doInBackground(String... params) {
		HttpClient postClient = new DefaultHttpClient();
		//TODO URL change in params
		//HttpPost post = new HttpPost("http://herpecho.appspot.com/echo");
		//first parameter is the URL to post to
		HttpPost post = new HttpPost(params[0]);
		try {
			//second parameter is the json string to post
			StringEntity se = new StringEntity(params[1]);
			post.setEntity(se);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			HttpResponse response = postClient.execute(post);
			StatusLine searchStatus = response.getStatusLine();
			if (searchStatus.getStatusCode() == 200) { // OK
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				InputStreamReader input = new InputStreamReader(content);
				BufferedReader reader = new BufferedReader(input);
				StringBuilder result = new StringBuilder();
				String linein;
				while ((linein = reader.readLine()) != null) {
					result.append(linein);
				}

				// this is deleting the file and assumes this already exists
				// the file is created outside the scope of the postrecord
				// third parameter is the location of a .json if posting from file
				File file = new File(params[2]);
				Log.d("Response", result.toString());
				Log.d("Deleting file", params[2]);
				file.delete();
				
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
