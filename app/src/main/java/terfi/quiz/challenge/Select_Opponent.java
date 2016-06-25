package terfi.quiz.challenge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;

import java.util.ArrayList;

import terfi.quiz.challenge.pojo.OpponentUser;
import terfi.quiz.challenge.utils.ImageUtil;
import terfi.quiz.challenge.view.GoogleProgress;
import terfi.quiz.challenge.view.RoundedImageView;

public class Select_Opponent extends ActionBarActivity {

	private ProgressDialog progress;
	ListView listView;
	Custom_Adapter adapter;
	String category;
	String categoryid;
	private ArrayList<OpponentUser> userlist = new ArrayList<OpponentUser>();
	SessionManager session;
	JSONArray json1;
	SharedPreferences prefs;
	private AdView adView;
	Typeface normal, bold;
	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = DataManager.admobid;
	boolean cbonline;
	TextView txtheader;
	String user_id, search;
	Context context;
	EditText etsearch;
	Button btnsearch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select__opponent);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		context = this;
		normal = Typeface.createFromAsset(context.getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(context.getAssets(), "bold.ttf");
		etsearch = (EditText) findViewById(R.id.etsearch);
		btnsearch = (Button) findViewById(R.id.btnsearch);
		session = new SessionManager(context);
		user_id = session.getuserid();
		adView = new AdView(context);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);

		adapter = new Custom_Adapter(context);
		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);
		LinearLayout ll = (LinearLayout) findViewById(R.id.ad);
		ll.addView(adView);

		listView = (ListView) findViewById(R.id.lvleaderboard);

		new getrandomusers().execute();

		btnsearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				search = etsearch.getText().toString();

				if (search.length() < 1) {
					Toast.makeText(context, "arama için kelime giriniz",
							Toast.LENGTH_LONG).show();
				} else {
					if (userlist.size() > 0) {
						userlist.clear();
					}
					new searchfriends().execute();
				}
			}
		});
		
		etsearch.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on Enter key press
					search = etsearch.getText().toString();

					if (search.length() < 1) {
						Toast.makeText(context, "arama için kelime giriniz",
								Toast.LENGTH_LONG).show();
					} else {
						
						if (userlist != null) {
							userlist.clear();
						}
						new searchfriends().execute();
					}
					return true;
				}
				return false;
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				DataManager.selecteduserid = userlist.get(position).getOppuserid();
				Intent i = new Intent(Select_Opponent.this, CheckProfile.class);
				finish();
				startActivity(i);
			}
		});

	}
	
	public class searchfriends extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(context, "Kullanıcı aranıyor...",
					"Lütfen bekle....");

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.searchfriend(context, user_id, search);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.dismiss();
			userlist = DataManager.opponenetuser;
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


	public class getrandomusers extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getallusers(user_id);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if (response) {

				userlist = DataManager.opponenetuser;

				if (userlist.size() > 0) {
					displaydata();
				} else {
					Toast.makeText(context, "Kullanıcı bulunamadı...", Toast.LENGTH_LONG)
							.show();
				}
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public void displaydata() {

		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);

	}

	public class Custom_Adapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public Custom_Adapter(Context c) {
			mInflater = LayoutInflater.from(c);

		}

		@Override
		public int getCount() {
			return userlist.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.user_row, null);

				holder = new ViewHolder();

				holder.txtusername = (TextView) convertView
						.findViewById(R.id.txtusername);

				holder.txtxp = (TextView) convertView.findViewById(R.id.txtxp);
				holder.btnstartnewchallenge = (Button) convertView
						.findViewById(R.id.btnstartnewchallenge);
				holder.txtxp.setTypeface(bold);
				holder.txtusername.setTypeface(bold);
				holder.btnstartnewchallenge.setTypeface(bold);
				holder.img = (RoundedImageView) convertView
						.findViewById(R.id.img);
				
				holder.btnstartnewchallenge.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DataManager.selecteduserid = v.getTag().toString();
						DataManager.ismanual = true;
						Intent i = new Intent(Select_Opponent.this, MainActivity.class);
						i.setAction("topic");
						startActivity(i);
					}
				});

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txtusername.setText(userlist.get(position).getOppfname()+ " "+userlist.get(position).getOpplname());
			holder.txtxp.setText("XP : " + userlist.get(position).getOppxp());
			holder.btnstartnewchallenge.setTag(userlist.get(position).getOppuserid());

			String logintype = userlist.get(position).getOpplogintype();

			if (logintype.equals("facebook")) {

				String oppurl = userlist.get(position).getOppprofilepic();

				ImageUtil.displayImage(holder.img, oppurl, null);

			} else if (logintype.equals("email")) {

				String oppurl = DataManager.url
						+ userlist.get(position).getOppprofilepic();

				ImageUtil.displayImage(holder.img, oppurl, null);
			}

			return convertView;
		}

		class ViewHolder {
			TextView txtusername, txtxp;
			RoundedImageView img;
			Button btnstartnewchallenge;
		}

	}
}