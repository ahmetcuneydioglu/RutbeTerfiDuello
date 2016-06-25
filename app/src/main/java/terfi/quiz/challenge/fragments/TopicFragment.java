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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import terfi.quiz.challenge.APIManager;
import terfi.quiz.challenge.DataManager;
import terfi.quiz.challenge.NewGameLoading;
import terfi.quiz.challenge.R;
import terfi.quiz.challenge.SessionManager;
import terfi.quiz.challenge.pojo.CategoryList;
import terfi.quiz.challenge.utils.ImageUtil;
import terfi.quiz.challenge.view.GoogleProgress;
import terfi.quiz.challenge.view.RoundedImageView;

public class TopicFragment extends Fragment {

	private ProgressDialog progress;
	ListView listView;
	Custom_Adapter adapter;
	String category;
	String categoryid;
	private ArrayList<CategoryList> categorylist = new ArrayList<CategoryList>();
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
	boolean ismanual = false;
	String selecteduserid= "";
	public static TopicFragment newInstance() {
		return new TopicFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_category, container,
				false);

		context = getActivity();
		normal = Typeface.createFromAsset(context.getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(context.getAssets(), "bold.ttf");
		session = new SessionManager(context);
		user_id = session.getuserid();
		adView = new AdView(context);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);

		ismanual = DataManager.ismanual;
		selecteduserid = DataManager.selecteduserid;

		adapter = new Custom_Adapter(context);
		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);
		LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ad);
		ll.addView(adView);

		listView = (ListView) rootView.findViewById(R.id.lvclassic_cat);
		
		new getcategories().execute();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if(ismanual)
				{
					categoryid = categorylist.get(position).getId();
					DataManager.selectedcategory = categorylist.get(position).getName();
					new getquestionmanualuser().execute();
				}else
				{
				
				categoryid = categorylist.get(position).getId();
				DataManager.selectedcategory = categorylist.get(position).getName();
				new getquestionsbycat().execute();
				}
			}

		});
		return rootView;
	}

	public void displaydata() {

		categorylist = DataManager.categorylist;
		
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);

	}
	
	public class getcategories extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getcategories();

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if (response) {

				if (DataManager.status.equalsIgnoreCase("1")) {

					displaydata();

				}
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public class Custom_Adapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public Custom_Adapter(Context c) {
			mInflater = LayoutInflater.from(c);

		}

		@Override
		public int getCount() {
			return categorylist.size();
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

				convertView = mInflater.inflate(R.layout.category_row, null);

				holder = new ViewHolder();

				holder.txtcatname = (TextView) convertView
						.findViewById(R.id.txtcustomrow);
				holder.txtcatname.setTypeface(bold);
				holder.img = (RoundedImageView ) convertView
						.findViewById(R.id.img);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txtcatname.setText(categorylist.get(position).getName());
			holder.txtcatname.setTag(categorylist.get(position)
					.getSubcat_count());

			String url = DataManager.photourl
					+ categorylist.get(position).getCategory_image();
			try {

				url = URLDecoder.decode(url, "UTF-8");
				url = url.replaceAll(" ", "%20");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			ImageUtil.displayImage(holder.img, url, null);
			return convertView;
		}

		class ViewHolder {
			TextView txtcatname;
			RoundedImageView img;

		}

	}

	// Random User
	public class getquestionsbycat extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getquestionbycatagory(categoryid, user_id);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if (response) {

				if (DataManager.status.equalsIgnoreCase("1")) {

					Intent i = new Intent(context, NewGameLoading.class);
					startActivity(i);

				} else {
					Toast.makeText(getActivity(), "No Users Found. Try Again", Toast.LENGTH_LONG).show();
				}
			} else {

			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	
	// Manual user
	
	public class getquestionmanualuser extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.createManualChallenge(categoryid, user_id, selecteduserid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if (response) {

				if (DataManager.status.equalsIgnoreCase("1")) {

					Intent i = new Intent(context, NewGameLoading.class);
					startActivity(i);

				} else {
					Toast.makeText(getActivity(), "No Users Found. Try Again", Toast.LENGTH_LONG).show();
				}
			} else {

			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
}
