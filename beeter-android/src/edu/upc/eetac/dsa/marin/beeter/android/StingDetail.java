package edu.upc.eetac.dsa.marin.beeter.android;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import edu.upc.eetac.dsa.marin.beeter.android.api.BeeterAPI;
import edu.upc.eetac.dsa.marin.beeter.android.api.Sting;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class StingDetail extends Activity {
	private final static String TAG = StingDetail.class.toString();
	private BeeterAPI api;	
	private class FetchStingTask extends AsyncTask<URL, Void, Sting> {
		private ProgressDialog pd;
	 
		@Override
		protected Sting doInBackground(URL... params) {
			Sting sting = api.getSting(params[0]);
			return sting;
		}
	 
		@Override
		protected void onPostExecute(Sting result) {
			loadSting(result);
			if (pd != null) {
				pd.dismiss();
			}
		}
	 
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(StingDetail.this);
			pd.setTitle("Loading...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
	 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sting_detail_layout);	
		api = new BeeterAPI();
		URL url = null;
		try {
			url = new URL((String) getIntent().getExtras().get("url"));
		} catch (MalformedURLException e) {
		}
		(new FetchStingTask()).execute(url);
	}
	
	private void loadSting(Sting sting) {
		TextView tvDetailSubject = (TextView) findViewById(R.id.tvDetailSubject);
		TextView tvDetailContent = (TextView) findViewById(R.id.tvDetailContent);
		TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
		TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
	 
		tvDetailSubject.setText(sting.getSubject());
		tvDetailContent.setText(sting.getContent());
		tvDetailUsername.setText(sting.getUsername());
		tvDetailDate.setText(SimpleDateFormat.getInstance().format(
				sting.getLastModified()));
	}
	
	
}
