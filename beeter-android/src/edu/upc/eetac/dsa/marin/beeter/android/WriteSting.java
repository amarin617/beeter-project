package edu.upc.eetac.dsa.marin.beeter.android;
import java.net.MalformedURLException;
import java.net.URL;

import edu.upc.eetac.dsa.marin.beeter.android.api.BeeterAPI;
import edu.upc.eetac.dsa.marin.beeter.android.api.Sting;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class WriteSting extends Activity {
	private final static String TAG = WriteSting.class.toString();
 
	private class PostStingTask extends AsyncTask<String, Void, Sting> {
		private URL url;
		private ProgressDialog pd;
 
		public PostStingTask(URL url) {
			super();
			this.url = url;
		}
 
		@Override
		protected Sting doInBackground(String... params) {
			BeeterAPI api = new BeeterAPI();
			Sting sting = api.createSting(url, params[0], params[1]);
			return sting;
		}
 
		@Override
		protected void onPostExecute(Sting result) {
			showStings();
			if (pd != null) {
				pd.dismiss();
			}
		}
 
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(WriteSting.this);
 
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}
 
	}
 
	private URL url;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_sting_layout);
 
		url = (URL) getIntent().getExtras().get("url");
	}
 
	public void cancel(View v) {
		finish();
	}
 
	public void postSting(View v) {
		EditText etSubject = (EditText) findViewById(R.id.etSubject);
		EditText etContent = (EditText) findViewById(R.id.etContent);
 
		String subject = etSubject.getText().toString();
		String content = etContent.getText().toString();
 
		(new PostStingTask(url)).execute(subject, content);
	}
	
	private void showStings(){
		Intent intent = new Intent(this, Beeter.class);
		startActivity(intent);
	} 	
}