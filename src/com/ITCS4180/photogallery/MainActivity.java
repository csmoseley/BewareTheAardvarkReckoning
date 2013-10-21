package com.ITCS4180.photogallery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.ITCS4180.photogallery.MESSAGE";
	public final static String EXTRA_IMAGEDATA = "com.ITCS4180.photogallery.IMAGEDATA";
	ArrayList<ImageData> photosList = new ArrayList<ImageData>();
	Parcel photosParcel;
	static int viewSize = 0;
	ProgressDialog progressDialog;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	public void launchGallery(View v) {
		// Perform action on click
		intent = new Intent(this, PhotoActivity.class);
		Button photosBtnObj = (Button) findViewById(v.getId());
		String message = photosBtnObj.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		switch(rg.getCheckedRadioButtonId()){
		case R.id.radio0:
			new photoXML().execute();
		break;
		case R.id.radio1:
			new photoJSON().execute();
		break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class photoXML extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL url = new URL(
						"http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=420cd85af2900ce8637ef0f5ff42496a&tags=UNCC&extras=views%2Curl_m&per_page=100&format=rest");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.connect();
				int statusCode = con.getResponseCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					InputStream in = con.getInputStream();
					photosList = parseXML(in);
					for(int i=0;i<(viewSize); i++){
						Log.i("urls","Title#" + Integer.toString(i) +": "+ photosList.get(i).getTitle());
						Log.i("urls","URL#" + Integer.toString(i) +": "+ photosList.get(i).getUrl());
						Log.i("urls","views#" + Integer.toString(i) +": "+ photosList.get(i).getViews());
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("Retrieving Image Info...");
				progressDialog.setCancelable(false);
				progressDialog.show(); 
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.i("demo", "done2");
			intent.putParcelableArrayListExtra(EXTRA_IMAGEDATA, photosList);
			Log.i("demo", "done3");
			startActivity(intent);
			progressDialog.dismiss();
		}
		
	}
	static ArrayList<ImageData> parseXML(InputStream xmlIn) throws XmlPullParserException, NumberFormatException, IOException{						
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(xmlIn, "UTF-8");
		ArrayList<ImageData> photoData = new ArrayList<ImageData>();
		
		int count = 0;
		int event = parser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				Log.i("demo", "done1");
				break;
			case XmlPullParser.START_TAG:
				if(parser.getName().equals("photo")){
					photoData.add(new ImageData(
							parser.getAttributeValue(null, "title").trim(),
							parser.getAttributeValue(null, "url_m").trim(),
							Integer.parseInt(parser.getAttributeValue(null, "views"))
							));
				}
				break;
			case XmlPullParser.END_TAG:
				if(parser.getName().equals("photo")){
					count++;
				}
			default:
				break;
			}
			event = parser.next();
		}
		
		viewSize = count;
		return photoData;
	}
	class photoJSON extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				URL url = new URL("http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=420cd85af2900ce8637ef0f5ff42496a&tags=UNCC&extras=views%2Curl_m&per_page=100&format=json&nojsoncallback=1");
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setRequestMethod("GET");
				con.connect();
				int statusCode = con.getResponseCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					BufferedReader reader = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line = reader.readLine();
					while (line != null) {
						sb.append(line);
						line = reader.readLine();
					}
					Log.d("demo", sb.toString());
					
					photosList = parseJSON(sb.toString());
					return null;
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("Retrieving Image Info...");
				progressDialog.setCancelable(false);
				progressDialog.show(); 
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.i("demo",Integer.toString(photosList.size()));
			intent.putParcelableArrayListExtra(EXTRA_IMAGEDATA, photosList);
			startActivity(intent);
			Log.i("demo", "done1");
			progressDialog.dismiss();
		}
		
	}
	static ArrayList<ImageData> parseJSON(String jsonString) throws IOException, JSONException{						
		Log.i("demo", "done1");
		JSONArray imageJSONArray = new JSONArray(jsonString);
		Log.i("demo", "done3");
		int count = 0;
		ArrayList<ImageData> photoData = new ArrayList<ImageData>();
		Log.i("demo", "done4");
		for(int i=0; i<imageJSONArray.length(); i++){
			JSONObject imageJSONObject = imageJSONArray.getJSONObject(i);
			System.out.println(imageJSONObject.getString("title"));
			photoData.add(new ImageData(
					imageJSONObject.getString("title"),
					imageJSONObject.getString("url_m"),
					imageJSONObject.getInt("views")
					));
			count++;
		}
		viewSize = count;
		Log.i("demo", "done2");
		return photoData;
	}
	
}