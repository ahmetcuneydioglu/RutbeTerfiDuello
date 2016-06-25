package terfi.quiz.challenge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import terfi.quiz.challenge.pojo.MyProfilePojo;
import terfi.quiz.challenge.utils.ImageUtil;
import terfi.quiz.challenge.view.FontelloTextView;
import terfi.quiz.challenge.view.GoogleProgress;
import terfi.quiz.challenge.view.RoundedImageView;

public class CheckProfile extends Activity {

	private ProgressDialog progress;
	// Custom_Adapter adapter;
	SessionManager session;
	SharedPreferences prefs;
	String user_id;
	ArrayList<MyProfilePojo> userlist = new ArrayList<MyProfilePojo>();
	TextView txttotalmatch, txtwinmatch, txtwinningpercentage, txtname, txtxp,
			txtheader;
	RoundedImageView image;
	Typeface normal, bold;
	FontelloTextView txthome;
	Button btnchallengeme;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_profile);
		normal = Typeface.createFromAsset(getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(getAssets(), "bold.ttf");
		session = new SessionManager(this);

		user_id = DataManager.selecteduserid;

		txttotalmatch = (TextView) findViewById(R.id.txttotalmatch);
		txtwinmatch = (TextView) findViewById(R.id.txtwinmatch);
		txtwinningpercentage = (TextView) findViewById(R.id.txtwinningpercentage);
		txtname = (TextView) findViewById(R.id.txtname);
		txtxp = (TextView) findViewById(R.id.txtxp);
		txtheader = (TextView) findViewById(R.id.txtheader);
		txthome = (FontelloTextView) findViewById(R.id.txthome);
		btnchallengeme = (Button) findViewById(R.id.btnChallenge);

		image = (RoundedImageView) findViewById(R.id.image);
		txttotalmatch.setTypeface(bold);
		txtwinmatch.setTypeface(bold);
		txtwinningpercentage.setTypeface(bold);
		txtname.setTypeface(bold);
		txtxp.setTypeface(bold);
		txtheader.setTypeface(bold);
		
		txthome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		btnchallengeme.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				DataManager.selecteduserid = user_id;
				DataManager.ismanual = true;
				Intent i = new Intent(CheckProfile.this, MainActivity.class);
				i.setAction("topic");
				startActivity(i);
			}
		});

		new getprofile().execute();
	}

	public class getprofile extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(CheckProfile.this);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getuserprofile(CheckProfile.this, user_id,
					session.getdeviceid(), user_id);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if (response) {

				userlist = DataManager.userprofilelist;

				displaydata();
			} else {

			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public void displaydata() {

		String win = userlist.get(0).getWin();
		String total = userlist.get(0).getTotal();

		double intwin = Double.valueOf(win);
		double inttotal = Double.valueOf(total);

		double percentage = (100 * intwin) / inttotal;

		txttotalmatch.setText("" + total);
		txtwinmatch.setText("" + win);
		
		DecimalFormat precision = new DecimalFormat("0"); 
		txtwinningpercentage.setText("" +precision.format(percentage) + " %");

		String xp = "XP : " + userlist.get(0).getXp();
		String name = userlist.get(0).getFname();

		txtname.setText("" + name);
		txtxp.setText("" + xp);

		String logintype = userlist.get(0).getLogintype();
		
		if (logintype.equals("facebook")) {

			String oppurl = userlist.get(0).getFname();

			ImageUtil.displayImage(image, oppurl, null);

		} else if (logintype.equals("email")) {

			String oppurl = session.getPhotourl();

			ImageUtil.displayImage(image, oppurl, null);
		}
		
		if (logintype.equals("facebook")) {

			String oppurl = userlist.get(0).getProfilepic();

			ImageUtil.displayImage(image, oppurl, null);

		} else if (logintype.equals("email")) {

			String oppurl = DataManager.url
					+ userlist.get(0).getProfilepic();

			ImageUtil.displayImage(image, oppurl, null);
		}
	}
	


}

