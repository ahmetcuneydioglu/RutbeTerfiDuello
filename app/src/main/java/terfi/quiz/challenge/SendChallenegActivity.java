package terfi.quiz.challenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

import terfi.quiz.challenge.view.FontelloTextView;
import terfi.quiz.challenge.view.GoogleProgress;

public class SendChallenegActivity extends AppCompatActivity {

	ProgressDialog progress;
	TextView txtright, txtheader;
	String rightans = null;
	String totalquestions = null;
	Setting_preference pref;
	FontelloTextView btnMainMenu, btnShare, btnHighscore;
	int numberques, rightanswer;
	int result;
	String category, standard;
	String score, name;
	SharedPreferences prefs;
	boolean cbonline;
	Setting_preference setuser;
	Typeface normal, bold;
	private InterstitialAd interstitial;
	private AdView adView;
	GoogleApiClient mclient;
	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = DataManager.admobid;
	String myusername, deviceid, profileid, categoryname;
	SessionManager session;
	int myxp =0 ;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);

		adfunction();
		categoryname = DataManager.selectedcategory;
		session = new SessionManager(getApplicationContext());
		myusername = session.getuserid();
		deviceid = session.getdeviceid();
		profileid = session.getuserid();
		
		
	
		myxp = DataManager.currentxp ;
		
		txtright = (TextView) findViewById(R.id.txtright);
		setuser = new Setting_preference(this);

		pref = new Setting_preference(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		txtheader = (TextView) findViewById(R.id.textView);
		TextView txtscoreheader = (TextView) findViewById(R.id.txtscoreheader);

		txtheader.setTypeface(bold);
		txtscoreheader.setTypeface(bold);
		txtright.setTypeface(normal);

		rightans = getIntent().getSerializableExtra("rightans").toString();
		totalquestions = getIntent().getSerializableExtra("totalques")
				.toString();

		numberques = Integer.parseInt(totalquestions);

		rightanswer = Integer.parseInt(rightans);

		result = (rightanswer);

		HashMap<String, String> user = pref.getUserDetails();
		name = user.get(Setting_preference.KEY_USERNAME);

		score = String.valueOf(result);

		btnMainMenu = (FontelloTextView) findViewById(R.id.btnMainMenu);
		btnHighscore = (FontelloTextView) findViewById(R.id.btnHighestscore);
		btnShare = (FontelloTextView) findViewById(R.id.btnShare);
		
		
		txtright.setText("" + myxp);

		btnMainMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent iMainMenu = new Intent(SendChallenegActivity.this, MainActivity.class);
				iMainMenu.setAction("splash");
				finish();
				startActivity(iMainMenu);

			}
		});

		btnHighscore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent iMainMenu = new Intent(SendChallenegActivity.this, MainActivity.class);
				iMainMenu.setAction("leaderboard");
				finish();
				startActivity(iMainMenu);

			}
		});
		
		  btnShare.setOnClickListener(new OnClickListener() {
	        	@Override
	    		public void onClick(View arg0) {
	        		  Intent sharingIntent = new Intent(Intent.ACTION_SEND);
	        	        sharingIntent.setType("text/plain");
	        	        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, DataManager.share );
	        	        startActivity(Intent.createChooser(sharingIntent, "Share using"));
	        		
	        	}
	    		});

		  
		  new sendChallenege().execute();
		  
		
	}
	
	public class sendChallenege extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(SendChallenegActivity.this);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			System.out.println("score---"+score);
			response = APIManager.sendchallnege(DataManager.gameid, myusername, String.valueOf(myxp), DataManager.opponenetuser.get(0).getOppuserid(), categoryname);
					

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();	
			if (DataManager.status.equalsIgnoreCase("1")) {
			
				Toast.makeText(getApplicationContext(), "Düello teklifiniz gönderildi", Toast.LENGTH_LONG).show();
				
			} else if (DataManager.status.equalsIgnoreCase("false")) {
				session.logoutUser();
			}
			
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	
	public void adfunction()
	{
		adView = new AdView(this);
//		mclient = getApiClient();
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);
		normal = Typeface.createFromAsset(getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(getAssets(), "bold.ttf");
		LinearLayout ll = (LinearLayout) findViewById(R.id.ad);
		ll.addView(adView);
		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);

		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-3320533451108667/3642284631");

		// Create ad request.
		AdRequest adRequest1 = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest1);
	}

	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public void onBackPressed() {

		Intent i = new Intent(SendChallenegActivity.this, MainActivity.class);
		i.setAction("splash");
		finish();
		startActivity(i);

	}


}