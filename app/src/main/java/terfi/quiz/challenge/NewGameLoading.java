package terfi.quiz.challenge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import terfi.quiz.challenge.pojo.MyProfilePojo;
import terfi.quiz.challenge.pojo.OpponentUser;
import terfi.quiz.challenge.utils.ImageUtil;
import terfi.quiz.challenge.view.GoogleProgress;
import terfi.quiz.challenge.view.RoundedImageView;

public class NewGameLoading extends Activity {
	String name, country, logintype;
	ArrayList<OpponentUser> opponentUsers = new ArrayList<OpponentUser>();
	ArrayList<MyProfilePojo> userlist = new ArrayList<MyProfilePojo>();
	String myusername, deviceid, profileid;
	TextView txtmyname, txtoppname, txtoppxp, txtmyxp;
	RoundedImageView myimg, oppimg;

	ProgressDialog progress;

	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game_loading);

		session = new SessionManager(getApplicationContext());
		myusername = session.getuserid();
		deviceid = session.getdeviceid();
		profileid = session.getuserid();
		opponentUsers = DataManager.opponenetuser;
		widgets();

		new getuserprofile().execute();

	}

	public void widgets() {
		txtmyname = (TextView) findViewById(R.id.txtmyname);
		txtoppname = (TextView) findViewById(R.id.txtoppname);

		txtmyxp = (TextView) findViewById(R.id.txtmyxp);
		txtoppxp = (TextView) findViewById(R.id.txtoppxp);

		myimg = (RoundedImageView) findViewById(R.id.myimg);
		oppimg = (RoundedImageView) findViewById(R.id.oppimg);
		String firsturl = session.getPhotourl();

		ImageUtil.displayImage(myimg, firsturl, null);

	}

	public void displaydata() {

		String xp = DataManager.myxp;

		session.setxp(xp);

		txtmyname.setText(session.firstname() + " " + session.lastname());
		txtoppname.setText(opponentUsers.get(0).getOppfname() + " "
				+ opponentUsers.get(0).getOpplname());

		txtmyxp.setText("XP : " + session.getXp());
		txtoppxp.setText("XP : " + opponentUsers.get(0).getOppxp());

		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				Intent i = new Intent(NewGameLoading.this,
						Timer_questions.class);
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);

			}

		}, 4000);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		
	}


	public class getuserprofile extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(NewGameLoading.this);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getuserprofile(NewGameLoading.this,
					myusername, deviceid, profileid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {
				userlist = DataManager.userprofilelist;
				opponentUsers = DataManager.opponenetuser;

				if (opponentUsers.get(0).getOpplogintype().equals("facebook")) {
					String oppurl = opponentUsers.get(0).getOppprofilepic();

					System.out.println("url---" + oppurl);

					ImageUtil.displayImage(oppimg, oppurl, null);
				} else if (opponentUsers.get(0).getOpplogintype()
						.equals("email")) {
					String oppurl = DataManager.url
							+ opponentUsers.get(0).getOppprofilepic();

					System.out.println("url---" + oppurl);

					ImageUtil.displayImage(oppimg, oppurl, null);
				}

				displaydata();
			} else if (DataManager.status.equalsIgnoreCase("false")) {
				session.logoutUser();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public String GetDirectURL(String url_send) {

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		URL url;
		URL secondURL = null;
		try {
			url = new URL(url_send);
			HttpURLConnection ucon = null;
			try {
				ucon = (HttpURLConnection) url.openConnection();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ucon.setInstanceFollowRedirects(false);
			secondURL = new URL(ucon.getHeaderField("Location"));
			session.setphotourl(secondURL.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return secondURL.toString();
	}
}
