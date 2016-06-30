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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

import terfi.quiz.challenge.view.FontelloTextView;
import terfi.quiz.challenge.view.GoogleProgress;

public class EndGameActivity extends AppCompatActivity {

	String game_id, categoryname, opponentuserid, myuserid;
	TextView txtmyscore, txtoppscore, txtmyname, txtoppname, txtresult;
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
	final private static int DIALOG_LOGIN = 1;
	private InterstitialAd interstitial;
	private AdView adView;
	GoogleApiClient mclient;
	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = DataManager.admobid;
	String myusername, deviceid, profileid;
	SessionManager session;
	ProgressDialog progress;
	int myxp =0 ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_game);

	
		
		myxp =  DataManager.currentxp ;
		
		session = new SessionManager(this);
		DataManager.ischallenege = false;
		myuserid = session.getuserid();
		game_id  = DataManager.selectedgameid;
		categoryname = DataManager.selectedcategory;
		opponentuserid = DataManager.selectedoppid;

		session = new SessionManager(getApplicationContext());
		myusername = session.getuserid();
		deviceid = session.getdeviceid();
		profileid = session.getuserid();

		txtmyscore = (TextView) findViewById(R.id.txtmyscore);
		setuser = new Setting_preference(this);

		pref = new Setting_preference(this);
	
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		txtoppname = (TextView) findViewById(R.id.txtoppname);
		txtoppscore = (TextView) findViewById(R.id.txtoppscore);
		txtmyname = (TextView) findViewById(R.id.txtmyname);
		txtresult = (TextView) findViewById(R.id.txtresult);

		txtmyname.setTypeface(bold);
		txtresult.setTypeface(bold);
		txtmyscore.setTypeface(bold);
		
		txtoppname.setTypeface(bold);
		txtoppscore.setTypeface(bold);
	
		

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

	

		btnMainMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent iMainMenu = new Intent(EndGameActivity.this,
						MainActivity.class);
				iMainMenu.setAction("splash");
				finish();
				startActivity(iMainMenu);

			}
		});

		btnHighscore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent iMainMenu = new Intent(EndGameActivity.this,
						MainActivity.class);
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
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						DataManager.share);
				// sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				// share);
				startActivity(Intent
						.createChooser(sharingIntent, "Share using"));

			}
		});
		new endgame().execute();

	}
	
	@Override
	public void onBackPressed() {
		
		Intent iMainMenu = new Intent(EndGameActivity.this,
				MainActivity.class);
		iMainMenu.setAction("splash");
		finish();
		startActivity(iMainMenu);
	}
	
	public void displaydata()
	{
		
		txtmyscore.setText("" + myxp);
		txtmyname.setText("You");
		
		txtoppscore.setText(""+DataManager.endgamescore);
		txtoppname.setText(""+DataManager.opponenetuser.get(0).getOppfname());
		
		txtresult.setText(""+DataManager.gameresult);
	}

	public class endgame extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(EndGameActivity.this);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			System.out.println("score---" + score);
			response = APIManager.endGame(game_id, myuserid, String.valueOf(myxp),
					opponentuserid, categoryname);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {

				displaydata();

			} else if (DataManager.status.equalsIgnoreCase("false")) {
				session.logoutUser();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public void adfunction() {
		adView = new AdView(this);
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
}
