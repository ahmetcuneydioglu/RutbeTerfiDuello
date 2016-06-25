package terfi.quiz.challenge;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashScreen extends Activity {
	
	ImageView img;
	TextView txtwelcome;
	Setting_preference pref;
	private boolean mIsBackButtonPressed;
	private static final int SPLASH_DURATION = 5000; // 5 seconds
	SessionManager session;
	String profileid, deviceid;
	ConnectionDetector connection;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash_screen);
		
		img = (ImageView)findViewById(R.id.image);
		txtwelcome = (TextView)findViewById(R.id.txtwelcome);
		
		animation();
		
		pref = new Setting_preference(this);
		connection = new ConnectionDetector(this);
		session = new SessionManager(this);
		deviceid = session.getdeviceid();
		profileid = session.getuserid();
		DataManager.username = session.getuserid();

		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (!mIsBackButtonPressed) {

					if (session.isLoggedIn()) {
						if(connection.isConnectingToInternet())
						{
							new getuserprofile().execute();
						}else
						{
						Intent i = new Intent(SplashScreen.this,
								MainActivity.class);
						i.setAction("splash");
						finish();
						startActivity(i);
						overridePendingTransition(0, 0);
						}
					} else {
						Intent i = new Intent(SplashScreen.this, FbLogin.class);
						
						finish();
						startActivity(i);
						overridePendingTransition(0, 0);
					}
				}
			}

		}, SPLASH_DURATION);

	}
	
	
	
	private void animation() {
		
		ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(img, "scaleX", 5.0F, 1.0F);
		scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleXAnimation.setDuration(1200);
		ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(img, "scaleY", 5.0F, 1.0F);
		scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleYAnimation.setDuration(1200);
		ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(img, "alpha", 0.0F, 1.0F);
		alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		alphaAnimation.setDuration(1200);
		ObjectAnimator textanimation = ObjectAnimator.ofFloat(txtwelcome, "alpha", 0.0F, 1.0F);
		textanimation.setStartDelay(1700);
		textanimation.setDuration(500);
		textanimation.start();
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation).with(textanimation);
		animatorSet.setStartDelay(500);
		animatorSet.start();
		
	}
	
	@Override
	public void onBackPressed() {

		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();

	}

	public class getuserprofile extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getuserprofile(getApplicationContext(),
					profileid, deviceid, profileid);

			return "";	

		}

		@Override
		protected void onPostExecute(String result) {

			if (response) {
				session.setxp(DataManager.userprofilelist.get(0).getXp());
				if(session.getLogintype().equals("facebook"))
				{
				
				String firsturl = "http://graph.facebook.com/"+profileid+"/picture?type=large";
				GetDirectURL(firsturl);
				}else if (session.getLogintype().equals("email"))
				{
					
				}
					
					Intent i = new Intent(SplashScreen.this, MainActivity.class);
					i.setAction("splash");
					finish();
					startActivity(i);
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
