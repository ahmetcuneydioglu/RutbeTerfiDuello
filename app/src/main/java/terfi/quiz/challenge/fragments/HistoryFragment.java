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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import terfi.quiz.challenge.APIManager;
import terfi.quiz.challenge.CheckProfile;
import terfi.quiz.challenge.DataManager;
import terfi.quiz.challenge.R;
import terfi.quiz.challenge.SessionManager;
import terfi.quiz.challenge.pojo.HistoryPojo;
import terfi.quiz.challenge.utils.ImageUtil;
import terfi.quiz.challenge.view.GoogleProgress;
import terfi.quiz.challenge.view.RoundedImageView;

public class HistoryFragment extends Fragment {

	public static HistoryFragment newInstance() {
		return new HistoryFragment();
	}
	Typeface normal, bold;
	private ProgressDialog progress;
//	Custom_Adapter adapter;
	SessionManager session;
	SharedPreferences prefs;
	String user_id;
	Context context;
	TextView txttotalmatch, txtwinmatch, txtwinningpercentage;
	ListView listView ;
	private ArrayList<HistoryPojo> historylist = new ArrayList<HistoryPojo>();
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
		
		
		new getprofile().execute();
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				DataManager.selecteduserid = historylist.get(position).getUserid();
				Intent i = new Intent(getActivity(), CheckProfile.class);
				startActivity(i);
			}
		});

		return rootView;
	}

	public class getprofile extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager.gethistory(user_id);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();
			if (response) {
				
				historylist = DataManager.historylist;

				if(historylist.size()> 0)
				{
				displaydata();
				}
			} else {

			Toast.makeText(getActivity(), "No Records found", Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	public void displaydata() {
		
		if(historylist.size() > 0)
		{
			Custom_Adapter adapter = new Custom_Adapter(getActivity());
			adapter.notifyDataSetChanged();
			listView.setAdapter(adapter);
		}
		
		
	}

	
	public class Custom_Adapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public Custom_Adapter(Context c) {
			mInflater = LayoutInflater.from(c);

		}

		@Override
		public int getCount() {
			return historylist.size();
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

				convertView = mInflater.inflate(R.layout.history_row, null);

				holder = new ViewHolder();

				holder.txtusername = (TextView) convertView
						.findViewById(R.id.txtusername);

				holder.txtxp = (TextView) convertView.findViewById(R.id.txtxp);
				holder.txtresult = (TextView) convertView
						.findViewById(R.id.txtresult);
				holder.txtxp.setTypeface(bold);
				holder.txtusername.setTypeface(bold);
				holder.txtresult.setTypeface(bold);
				holder.img = (RoundedImageView) convertView
						.findViewById(R.id.img);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txtusername.setText(historylist.get(position).getFname());
			
			String myscore = "Your Score : "+historylist.get(position).getMyscore();
			String oppscore = historylist.get(position).getFname()+"'s Score : "+historylist.get(position).getOppscore();
			holder.txtxp.setText(myscore + "\n" + oppscore);
		

			String logintype = historylist.get(position).getLogintype();

			if (logintype.equals("facebook")) {

				String oppurl = historylist.get(position).getProfilepic();

				ImageUtil.displayImage(holder.img, oppurl, null);

			} else if (logintype.equals("email")) {

				String oppurl = DataManager.url
						+ historylist.get(position).getProfilepic();

				ImageUtil.displayImage(holder.img, oppurl, null);
			}
			
			String resultstatus = historylist.get(position).getGameresult();
			String result = "";
			if(resultstatus.equals("0"))
			{
				result = "Game \n Tie";
			}else if(resultstatus.equals("1"))
			{
				result = "You \n Won";
			}else if(resultstatus.equals("2"))
			{
				result = "You \n Lost";
			}
			holder.txtresult.setText("" + result);

			return convertView;
		}

		class ViewHolder {
			TextView txtusername, txtxp, txtresult;
			RoundedImageView img;

		}

	}
	
}
