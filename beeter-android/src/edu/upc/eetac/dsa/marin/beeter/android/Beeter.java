package edu.upc.eetac.dsa.marin.beeter.android;

import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import edu.upc.eetac.dsa.marin.beeter.android.api.BeeterAPI;
import edu.upc.eetac.dsa.marin.beeter.android.api.Sting;
import edu.upc.eetac.dsa.marin.beeter.android.api.StingCollection;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Beeter extends ListActivity {
	private final static String TAG = Beeter.class.toString();
	String serverAddress;
	String serverPort;
	private BeeterAPI api;
	private ArrayList<Sting> stingList = new ArrayList<Sting>();
	private StingAdapter adapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
	 
		AssetManager assetManager = getAssets();
		Properties config = new Properties();
		try {
			config.load(assetManager.open("config.properties"));
			serverAddress = config.getProperty("server.address");
			serverPort = config.getProperty("server.port");
	 
			Log.d(TAG, "Configured server " + serverAddress + ":" + serverPort);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			finish();
		}
		setContentView(R.layout.beeter_layout);
	 
		stingList = new ArrayList<Sting>();
		adapter = new StingAdapter(this, stingList);
		setListAdapter(adapter);
	 
		SharedPreferences prefs = getSharedPreferences("beeter-profile", Context.MODE_PRIVATE);
		final String username = prefs.getString("username", null);
		final String password = prefs.getString("password", null);
	 
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password
						.toCharArray());
			}
		});
		Log.d(TAG, "authenticated with " + username + ":" + password);
	 
		api = new BeeterAPI();
		URL url = null;
		try {
			url = new URL("http://" + serverAddress + ":" + serverPort
					+ "/beeter-api/stings?&offset=0&length=20");
		} catch (MalformedURLException e) {
			Log.d(TAG, e.getMessage(), e);
			finish();
		}
		(new FetchStingsTask()).execute(url);
	}
	
	private class FetchStingsTask extends AsyncTask<URL, Void, StingCollection> {
		private ProgressDialog pd;
	 
		@Override
		protected StingCollection doInBackground(URL... params) {
			StingCollection stings = api.getStings(params[0]);
			return stings;
		}
		
		@Override
		protected void onPostExecute(StingCollection result) {
			addStings(result);
			if (pd != null) {
				pd.dismiss();
			}
		}
	 
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(Beeter.this);
			pd.setTitle("Searching...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
	 
	}
	
	private void addStings(StingCollection stings){
		stingList.addAll(stings.getStings());
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Sting sting = stingList.get(position);
	 
		// HATEOAS version
		URL url = null;
		try {
			url = new URL(sting.getLinks().get(0).getUri());
		} catch (MalformedURLException e) {
			return;
		}
	 
		// No HATEOAS
		// URL url = null;
		// try {
		// url = new URL("http://" + serverAddress + ":" + serverPort
		// + "/beeter-api/stings/" + id);
		// } catch (MalformedURLException e) {
		// return;
		// }
		Log.d(TAG, url.toString());
		
		Intent intent = new Intent(this, StingDetail.class);
		intent.putExtra("url", url.toString());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.beeter_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miWrite:
			URL url = null;
			try {
				url = new URL("http://" + serverAddress + ":" + serverPort
						+ "/beeter-api/stings");
			} catch (MalformedURLException e) {
				Log.d(TAG, e.getMessage(), e);
			}
			Intent intent = new Intent(this, WriteSting.class);
			intent.putExtra("url", url);
			startActivity(intent);
			
			return true;
	 
		default:
			return super.onOptionsItemSelected(item);
		}
	 
	}
	
	
}
