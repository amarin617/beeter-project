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
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

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
		
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("alicia", "alicia"
						.toCharArray());
			}
		});
		
		adapter = new StingAdapter(this, stingList);
		setListAdapter(adapter);
		
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
	
	
	
}
