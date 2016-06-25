package terfi.quiz.challenge.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.shutterbug.FetchableImageView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import terfi.quiz.challenge.APIManager;
import terfi.quiz.challenge.DataManager;
import terfi.quiz.challenge.R;
import terfi.quiz.challenge.SessionManager;
import terfi.quiz.challenge.pojo.ChallenegePojo;
import terfi.quiz.challenge.view.GoogleProgress;

public class PendingFragment extends Fragment {

	public static PendingFragment newInstance() {
		return new PendingFragment();
	}
	Typeface normal, bold;
	private ProgressDialog progress;
	Custom_Adapter adapter;
	SessionManager session;
	SharedPreferences prefs;
	String user_id;
	Context context;
	TextView txttotalmatch, txtwinmatch, txtwinningpercentage;
	ListView listView ;
	private ArrayList<ChallenegePojo> challengelist = new ArrayList<ChallenegePojo>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);

		context = getActivity();
		normal = Typeface.createFromAsset(context.getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(context.getAssets(), "bold.ttf");
		session = new SessionManager(getActivity());

		user_id = session.getuserid();

		txttotalmatch = (TextView)rootView.findViewById(R.id.txttotalmatch);
		txtwinmatch = (TextView)rootView.findViewById(R.id.txtwinmatch);
		txtwinningpercentage = (TextView)rootView.findViewById(R.id.txtwinningpercentage);
		
		
		
		listView = (ListView) rootView
				.findViewById(R.id.lvhistory);
	
		new getsentchallenges().execute();
		return rootView;
	}

	public void displaydata() {
		

		adapter = new Custom_Adapter(getActivity());
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);

	}

	public class getsentchallenges extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.getsentchallenges(user_id);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {

				challengelist = DataManager.challenegelist;
				if (challengelist.size() > 0) {

					displaydata();
				}
				else
				{
					Toast.makeText(context, "No Pending Challenges sent", Toast.LENGTH_LONG).show();
				}
			}
				else if (DataManager.status.equalsIgnoreCase("0")) {
										
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
			return challengelist.size();
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

				convertView = mInflater.inflate(R.layout.challenege_row, null);

				holder = new ViewHolder();

				holder.txtcategoryname = (TextView) convertView
						.findViewById(R.id.txtcategoryname);
				holder.txtchallenegefrom = (TextView) convertView
						.findViewById(R.id.txtchallenegefrom);
				holder.img = (FetchableImageView) convertView
						.findViewById(R.id.img);

				holder.btnaccept = (Button) convertView
						.findViewById(R.id.btnaccept);
				holder.btnreject = (Button) convertView
						.findViewById(R.id.btnreject);

				holder.btnaccept.setVisibility(View.GONE);

				holder.btnreject.setVisibility(View.GONE);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txtcategoryname.setText(challengelist.get(position)
					.getCategory_name());
			holder.txtcategoryname.setTag(challengelist.get(position)
					.getCatid());

			String url = DataManager.photourl
					+ challengelist.get(position).getProfilepic();
			try {

				url = URLDecoder.decode(url, "UTF-8");
				url = url.replaceAll(" ", "%20");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			holder.img.setImage(url,
					getResources().getDrawable(R.drawable.splash_icon));

			String fullname = challengelist.get(position).getFirstname();
					

			holder.txtchallenegefrom.setText("You have challenged " + fullname);
			
			

			return convertView;
		}

		class ViewHolder {
			TextView txtcategoryname, txtchallenegefrom;
			FetchableImageView img;
			Button btnaccept, btnreject;
		}

	}
	
}
