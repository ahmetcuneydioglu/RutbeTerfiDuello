package terfi.quiz.challenge.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import org.json.JSONArray;

import java.util.ArrayList;

import terfi.quiz.challenge.APIManager;
import terfi.quiz.challenge.CheckProfile;
import terfi.quiz.challenge.DataManager;
import terfi.quiz.challenge.R;
import terfi.quiz.challenge.SessionManager;
import terfi.quiz.challenge.pojo.OpponentUser;
import terfi.quiz.challenge.utils.ImageUtil;
import terfi.quiz.challenge.view.GoogleProgress;
import terfi.quiz.challenge.view.RoundedImageView;

public class LeaderBoardFragment extends Fragment {

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
	String user_id;
	Context context;

	public static LeaderBoardFragment newInstance() {
		return new LeaderBoardFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_leaderboard,
				container, false);

		context = getActivity();
		normal = Typeface.createFromAsset(context.getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(context.getAssets(), "bold.ttf");
		session = new SessionManager(context);
		user_id = session.getuserid();
		adView = new AdView(context);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);

		adapter = new Custom_Adapter(context);
		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);
		LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ad);
		ll.addView(adView);

		listView = (ListView) rootView.findViewById(R.id.lvleaderboard);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				DataManager.selecteduserid = userlist.get(position).getOppuserid();
				Intent i = new Intent(getActivity(), CheckProfile.class);
				startActivity(i);
			}
		});
		
		
		new getleaderboard().execute();

		return rootView;
	}

	public class getleaderboard extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getleaderboard(user_id);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if (response) {

				userlist = DataManager.opponenetuser;

				if (userlist.size() > 0) {
					displaydata();
				}
				else	{
					Toast.makeText(getActivity(), "No User...",
							Toast.LENGTH_LONG).show();
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

				convertView = mInflater.inflate(R.layout.leaderboard_row, null);

				holder = new ViewHolder();

				holder.txtusername = (TextView) convertView
						.findViewById(R.id.txtusername);

				holder.txtxp = (TextView) convertView.findViewById(R.id.txtxp);
				holder.txtrank = (TextView) convertView
						.findViewById(R.id.txtrank);
				holder.txtxp.setTypeface(bold);
				holder.txtusername.setTypeface(bold);
				holder.txtrank.setTypeface(bold);
				holder.img = (RoundedImageView) convertView
						.findViewById(R.id.img);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			int rank = position + 1;
			holder.txtusername.setText(userlist.get(position).getOppfname());
			holder.txtxp.setText("XP : " + userlist.get(position).getOppxp());
			holder.txtrank.setText("" + rank);

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
			TextView txtusername, txtxp, txtrank;
			RoundedImageView img;

		}

	}

}
