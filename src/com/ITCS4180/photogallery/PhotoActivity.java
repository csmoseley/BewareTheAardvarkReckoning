/* 	Assignment: Homework 5
 * 
 * 	File: 		PhotoActivity.java
 * 
 * 	Names: 		Christopher Moseley
 * 				Caril Carrillo
 *   			Farida Bestowros
 */

package com.ITCS4180.photogallery;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoActivity extends Activity {
	Handler handler;
	ProgressDialog progressDialog;
	boolean goodClick = false;
	int currentImage = 0;
	String message = null;
	boolean notfinished = true;
	CountDownTimer cdt;
	String[] imageUrlArray = null;
	boolean error = false;
	ImageData[] imageData = null;
	Bitmap bmp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		// Show the Up button in the action bar.
		setupActionBar();
		

		// Get the message from the intent
		Intent intent = getIntent();
		message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		Parcelable[] imageParcel = intent.getParcelableArrayExtra(MainActivity.EXTRA_IMAGEDATA);
		
		imageData = Arrays.copyOf(imageParcel, imageParcel.length, ImageData[].class);
		//imageData = new ImageData[imageParcel.length];
		//for(int i=0; i < imageParcel.length; i++){
		//	imageData[i] = (ImageData) imageParcel[i];
		//}
		
		Log.i("demo", imageData[0].getTitle());
		if (message.equals("Photos")) {
			new photoGet().execute();
			ImageView imageObj = (ImageView) findViewById(R.id.imageView1);
			imageObj.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:

						if (goodClick) {
							float hitX = event.getX() / v.getWidth();
							float hitY = ((event.getY() - v.getTranslationY()) / v
									.getHeight());
							if ((hitX > .8) && (hitY < 1 && hitY > 0)) {
								if (currentImage == (imageData.length - 1)) {
									currentImage = 0;
								} else {
									currentImage++;
								}
								new photoGet().execute();
							} else if ((hitX < .2) && (hitY < 1 && hitY > 0)) {
								if (currentImage == 0) {
									currentImage = imageData.length - 1;

								} else {
									currentImage--;
								}
								new photoGet().execute();
							}
						}
						goodClick = false;
						break;
					case MotionEvent.ACTION_DOWN:
						float hitX = event.getX() / v.getWidth();
						float hitY = ((event.getY() - v.getTranslationY()) / v
								.getHeight());
						if ((hitX > .8) && (hitY < 1 && hitY > 0)) {
							goodClick = true;
						} else if ((hitX < .2) && (hitY < 1 && hitY > 0)) {
							goodClick = true;
						} else {
							goodClick = false;
						}

					}

					return true;
				}
			});
		} else {
			new photoGet().execute();
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	class photoGet extends AsyncTask<Void, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {

			URL url = null;
			Bitmap bmp = null;
			try {
				url = new URL(imageData[currentImage].getUrl());
			} catch (MalformedURLException e) {
				Log.e("FanHitting", "Bad Url in image array, or bad parsing");
			}
			InputStream inputStream = null;
			URLConnection conn = null;
			try {
				conn = url.openConnection();
			} catch (IOException e) {
				Log.e("demo", "PhotoActivity.conn: issue! (Connection Failure?)");
				error = true; 
			}
			try {
				inputStream = conn.getInputStream();
			} catch (IOException e1) {
				Log.e("demo", "PhotoActivity.inputstream: issue!");
				error = true; 
			}
			Log.e("demo", Boolean.toString(error));
			if(error){
			}else{
				bmp = BitmapFactory.decodeStream(inputStream);
			
			if(bmp.getHeight() > bmp.getWidth()){
				Matrix matrix = new Matrix();
		        matrix.postRotate(270);
		        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			}
	        
	        
			Log.i("demo", "returning a bmp");
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			ImageView displayImg = (ImageView) findViewById(R.id.imageView1);
			if(error == true){
				finish();
			}
			displayImg.setImageBitmap(result);
			TextView titleText = (TextView) findViewById(R.id.titleTV);
			TextView viewText = (TextView) findViewById(R.id.viewTV);
			titleText.setText(imageData[currentImage].getTitle());
			viewText.setText("Views: " + Integer.toString(imageData[currentImage].getViews()));
			if (message.equals("Photos")) {
				progressDialog.dismiss();
			}else{
				if (currentImage == (imageData.length - 1)) {
					currentImage = 0;
				} else {
					currentImage++;
				}
				displayImg.postDelayed(new Runnable() {
					@Override
					public void run(){
						new photoGet().execute();
					}
				}, 2000);
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (message.equals("Photos")) {
				progressDialog = new ProgressDialog(PhotoActivity.this);
				progressDialog.setMessage("Loading Image");
				progressDialog.setCancelable(false);
				progressDialog.show();
			} 
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
