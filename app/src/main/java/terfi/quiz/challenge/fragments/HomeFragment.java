package terfi.quiz.challenge.fragments;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
import terfi.quiz.challenge.MainActivity;
import terfi.quiz.challenge.R;
import terfi.quiz.challenge.Select_Opponent;
import terfi.quiz.challenge.SessionManager;
import terfi.quiz.challenge.Timer_questions;
import terfi.quiz.challenge.pojo.ChallenegePojo;
import terfi.quiz.challenge.view.GoogleProgress;


public class HomeFragment extends Fragment {

	public static HomeFragment newInstance() {
		return new HomeFragment();
	}
	Typeface normal, bold;
	private ProgressDialog progress;
	Custom_Adapter adapter;
	SessionManager session;
	SharedPreferences prefs;
	String user_id, selectedgameid, selectedoppid;
	private ArrayList<ChallenegePojo> challengelist = new ArrayList<ChallenegePojo>();
	ListView listView ;
	TextView txtheader;
	Button btnstartnewchallenge, btnselectopponent; 
	Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		context = getActivity();
		normal = Typeface.createFromAsset(context.getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(context.getAssets(), "bold.ttf");
		session = new SessionManager(getActivity());
		context.registerReceiver(broadcastReceiver, new IntentFilter(
				"CHAT_MESSAGE_RECEIVED"));

		user_id = session.getuserid();
		listView = (ListView) rootView.findViewById(R.id.stickylistheader);
		btnstartnewchallenge = (Button)rootView.findViewById(R.id.btnstartnewchallenge);
		btnselectopponent = (Button)rootView.findViewById(R.id.btnselectopponent);
		listView.setFitsSystemWindows(true);
		
		new getreceivedChallenges().execute();
		btnselectopponent.setTypeface(bold);
		btnstartnewchallenge.setTypeface(bold);
		btnstartnewchallenge.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//					
					DataManager.ismanual = false;
					DataManager.selecteduserid = "";
					Intent i = new Intent(getActivity(), MainActivity.class);
					i.setAction("topic");
					startActivity(i);
					
			}
		});
		
		btnselectopponent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					DataManager.ismanual = true;
					Intent i = new Intent(getActivity(), Select_Opponent.class);
					startActivity(i);
					
			}
		});
	
	
		txtheader = (TextView)rootView.findViewById(R.id.txtheader);
		txtheader.setTypeface(bold);
		
		return rootView;
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			new getreceivedChallenges().execute();

		}
	};
	
	

	public void displaydata() {
		

		adapter = new Custom_Adapter(getActivity());
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);

	}

	public class getreceivedChallenges extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			DataManager.status = "";
			response = APIManager.getReceivedChallenges(user_id);

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
				
				holder.txtcategoryname.setTypeface(bold);
				holder.txtchallenegefrom.setTypeface(bold);
				holder.btnaccept.setTypeface(bold);
				holder.btnreject.setTypeface(bold);
			

				holder.btnaccept.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int selpos = Integer.parseInt((v.getTag().toString()));
						selectedgameid = challengelist.get(selpos).getGameid();
						selectedoppid = challengelist.get(selpos).getUserid();
						String category = challengelist.get(selpos).getCategory_name();
						
						DataManager.selectedgameid = selectedgameid;
						DataManager.selectedoppid = selectedoppid;
						DataManager.selectedcategory = category;
						new acceptChallenge().execute();
					}
				});

				holder.btnreject.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int selpos = Integer.parseInt((v.getTag().toString()));
						selectedgameid = challengelist.get(selpos).getGameid();
						selectedoppid = challengelist.get(selpos).getUserid();
						
						showdialog();
					}
				});

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

			String fullname = challengelist.get(position).getFirstname() + " "
					+ challengelist.get(position).getLastname();

			holder.txtchallenegefrom.setText("" + fullname
					+ "  sana meydan okudu");
			holder.txtchallenegefrom.setTag(challengelist.get(position)
					.getUserid());
			holder.btnaccept.setTag("" + position);
			holder.btnreject.setTag("" + position);

			return convertView;
		}

		class ViewHolder {
			TextView txtcategoryname, txtchallenegefrom;
			FetchableImageView img;
			Button btnaccept, btnreject;
		}

	}


	public void showdialog() {

		
		final Dialog myDialog = new Dialog(getActivity());
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		myDialog.setContentView(R.layout.custom_dialog_entername);
		myDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));


			Button btnyes = (Button) myDialog.findViewById(R.id.btnOk);
			Button btnno = (Button) myDialog.findViewById(R.id.btncancel);

		
			btnyes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					new rejectChallenege().execute();
					myDialog.cancel();

				}
			});

			btnno.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					myDialog.cancel();

				}
			});
			
			myDialog.show();
	}

	public class rejectChallenege extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager
					.rejectchallenge(selectedgameid, selectedoppid);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			if (DataManager.status.equalsIgnoreCase("1")) {

				Toast.makeText(getActivity(),
						"meydan okumayı reddettin", Toast.LENGTH_LONG).show();
				
				new getreceivedChallenges().execute();
			} else if (DataManager.status.equalsIgnoreCase("false")) {
				session.logoutUser();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}
	
	public class acceptChallenge extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(context);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {

			response = APIManager
					.acceptChallenge(selectedgameid, user_id);

			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			progress.cancel();
			if (DataManager.status.equalsIgnoreCase("1")) {
				DataManager.ischallenege = true;
				Intent i = new Intent(context, Timer_questions.class);
				((Activity) context).finish();
				startActivity(i);
			} else if (DataManager.status.equalsIgnoreCase("false")) {
				session.logoutUser();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

}
