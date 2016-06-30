package terfi.quiz.challenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.shutterbug.FetchableImageView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import terfi.quiz.challenge.pojo.QuestionsPojo;
import terfi.quiz.challenge.pojo.QuizPojo;
import terfi.quiz.challenge.utils.ImageUtil;
import terfi.quiz.challenge.view.CircleProgress;
import terfi.quiz.challenge.view.RoundedImageView;

public class Timer_questions extends Activity implements
		 View.OnClickListener {

	String que, opt1, opt2, opt3, opt4, answer, category, url, strtimer,
			categoryname;
	int rightans = 0, wrongans = 0, i = 0, addcounter = 0, totalQueLen,
			myxp = 0;
	ArrayList<QuestionsPojo> questionlist = new ArrayList<QuestionsPojo>();
	TextView taQue, taOpt1, taOpt2, taOpt3, taOpt4, no_of_questions, textView,
			txtmyname, txtoppname, txtoppxp, txtmyxp;
	static int currentQuestion = 0;
	MediaPlayer mp;
	QuizPojo cn = null;
	MyCounter timer = null;
	Setting_preference pref;
	long savedtimer;
	Vibrator vibe;
	private AdView adView;
	private static final String AD_UNIT_ID = DataManager.admobid;
	Random rand = new Random();
	boolean fifty = false, pass = false, time = false, ischallenge = false;
	Typeface normal, bold;
	CircleProgress progresstimer;
	FetchableImageView img;
	SharedPreferences myPrefs;
	SharedPreferences.Editor prefsEditor;
	private final String TAG_NAME = "ads";
	private InterstitialAd interstitial;
	static CountDownTimer timer2 = null;
	Toast toast;
	RoundedImageView myimg, oppimg;
	SessionManager session;
	long currentmilisecond = 0;
	boolean backpressstart = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer_questions);

		ischallenge = DataManager.ischallenege;
		toast = new Toast(this);
		textView = new TextView(this);
		textView.setTextColor(Color.WHITE);
		textView.setBackgroundColor(Color.TRANSPARENT);
		textView.setTextSize(50);
		session = new SessionManager(this);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

		toast.setView(textView);

		progresstimer = (CircleProgress) findViewById(R.id.progressBar1);

		timer2 = new CountDownTimer(4000, 1000) {
			public void onTick(long millisUntilFinished) {
				Integer milisec = new Integer(
						new Double(millisUntilFinished).intValue());
				Integer cd_secs = milisec / 1000;
				backpressstart = true;
				Integer seconds = (cd_secs % 3600) % 60;
				textView.setText(" " + seconds);
				toast.show();
			}

			public void onFinish() {
				textView.setText("Başlıyoruz..");
				toast.cancel();
				startup();
			}

		}.start();

	}

	@SuppressWarnings("deprecation")
	public void startup() {
		
		backpressstart = false;
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		prefsEditor = myPrefs.edit();
		addcounter = myPrefs.getInt(TAG_NAME, 0);

		questionlist = DataManager.questionlist;

		normal = Typeface.createFromAsset(getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(getAssets(), "bold.ttf");

		// tv = (TextView) findViewById(R.id.tv);
		// tv.setText(" ");

		pref = new Setting_preference(getApplicationContext());

		strtimer = DataManager.timer;
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		savedtimer = Long.parseLong(strtimer);
		categoryname = getIntent().getStringExtra("categoryname");
		
		timer = new MyCounter(savedtimer * 1000, 1000);
		timer.start();


		currentQuestion = 0;
		rightans = 0;
		wrongans = 0;
		taQue = (TextView) this.findViewById(R.id.taque);
		taOpt1 = (TextView) this.findViewById(R.id.taOpt5);
		taOpt2 = (TextView) this.findViewById(R.id.taOpt6);
		taOpt3 = (TextView) this.findViewById(R.id.taOpt7);
		taOpt4 = (TextView) this.findViewById(R.id.taOpt8);
		img = (FetchableImageView) findViewById(R.id.imageView1);

		txtmyname = (TextView) findViewById(R.id.txtmyname);
		txtoppname = (TextView) findViewById(R.id.txtoppname);

		txtmyxp = (TextView) findViewById(R.id.txtmyxp);
		txtoppxp = (TextView) findViewById(R.id.txtoppxp);

		myimg = (RoundedImageView) findViewById(R.id.myimg);
		oppimg = (RoundedImageView) findViewById(R.id.oppimg);

		String firsturl = session.getPhotourl();

		ImageUtil.displayImage(myimg, firsturl, null);
		String oppurl = "", logintype = "";

		
		if (!ischallenge) {
			
			oppurl = DataManager.opponenetuser.get(0).getOppprofilepic();
			logintype = DataManager.opponenetuser.get(0).getOpplogintype();
			txtoppname.setText(""
					+ DataManager.opponenetuser.get(0).getOppfname());
		} else {
			
			oppurl = DataManager.challenegelist.get(0).getProfilepic();
			logintype = DataManager.challenegelist.get(0).getLogintype();
			txtoppname.setText(""
					+ DataManager.challenegelist.get(0).getFirstname());
		}

		if (logintype.equals("facebook")) {

			ImageUtil.displayImage(oppimg, oppurl, null);


		} else if (logintype.equals("email")) {
			
			if(!oppurl.equals("no"))
			{
			oppurl= DataManager.url
					+ oppurl;

			ImageUtil.displayImage(oppimg, oppurl, null);
			}
		}
		
		

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		taOpt1.setOnClickListener(this);
		taOpt2.setOnClickListener(this);
		taOpt3.setOnClickListener(this);
		taOpt4.setOnClickListener(this);

		taOpt1.setTypeface(bold);
		taOpt2.setTypeface(bold);
		taOpt3.setTypeface(bold);
		taOpt4.setTypeface(bold);

		taQue.setTypeface(bold);

		totalQueLen = questionlist.size();

		getquestionsanswers(currentQuestion);

		adView = new AdView(this);

		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);
		AdRequest adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);
		LinearLayout ll = (LinearLayout) findViewById(R.id.ad);
		ll.addView(adView);

		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-3320533451108667/3642284631");

		// Create ad request.
		AdRequest adRequest1 = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest1);
		AdListener adListener = new AdListener() {

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();

			}

			@Override
			public void onAdClosed() {
				super.onAdClosed();

				nextquestion(0);
			}
		};

		interstitial.setAdListener(adListener);
	}

	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		} else {
			displaynextquestion();
		}
	}

	@Override
	public void onClick(View v1) {

		timer.cancel();

		TextView tmp = (TextView) v1;
		String sel = tmp.getText().toString();

		selectanswer(0, tmp, sel);
	}

	public void selectanswer(int SPLASHTIME, final TextView tmp,
			final String sel) {

		if (sel.equals(answer)) {

			if (currentmilisecond <= 45) {
				myxp = myxp + 1;
			} else if (currentmilisecond == 46) {
				myxp = myxp + 2;
			} else if (currentmilisecond == 47) {
				myxp = myxp + 3;
			} else if (currentmilisecond == 48) {
				myxp = myxp + 4;
			} else if (currentmilisecond == 49) {
				myxp = myxp + 5;
			} else if (currentmilisecond > 50) {
				myxp = myxp + 6;
			} else if (currentmilisecond > 51) {
				myxp = myxp + 7;
			} else if (currentmilisecond > 52) {
				myxp = myxp + 8;
			} else if (currentmilisecond > 53) {
				myxp = myxp + 9;
			} else if (currentmilisecond > 54) {
				myxp = myxp + 10;
			} else if (currentmilisecond > 55) {
				myxp = myxp + 11;
			} else if (currentmilisecond > 56) {
				myxp = myxp + 12;
			} else if (currentmilisecond > 57) {
				myxp = myxp + 13;
			} else if (currentmilisecond > 58) {
				myxp = myxp + 14;
			} else if (currentmilisecond > 59) {
				myxp = myxp + 15;
			}

			txtmyxp.setText("" + myxp);

			tmp.setBackgroundResource(R.drawable.green);
			tmp.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.right_icon, 0);
			vibrate();

			audio("right");

			rightans++;
			nextquestion(3000);
		} else {

			tmp.setBackgroundResource(R.drawable.red);
			tmp.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.wrong_icon, 0);

			if (answer.equals(taOpt1.getText().toString())) {
				taOpt1.setBackgroundResource(R.drawable.green);
				taOpt1.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.right_icon, 0);

			}
			if (answer.equals(taOpt2.getText().toString())) {
				taOpt2.setBackgroundResource(R.drawable.green);
				taOpt2.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.right_icon, 0);

			}
			if (answer.equals(taOpt3.getText().toString())) {
				taOpt3.setBackgroundResource(R.drawable.green);
				taOpt3.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.right_icon, 0);

			}
			if (answer.equals(taOpt4.getText().toString())) {
				taOpt4.setBackgroundResource(R.drawable.green);
				taOpt4.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.right_icon, 0);

			}
			wrongans++;

			audio("wrong");

			vibrate();

			nextquestion(4000);
		}

		taOpt1.setClickable(false);
		taOpt2.setClickable(false);
		taOpt3.setClickable(false);
		taOpt4.setClickable(false);

	}

	public void nextquestion(int SPLASHTIME) {

		timer.cancel();

		Handler handler = new Handler();

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				addcounter++;
				prefsEditor.putInt(TAG_NAME, addcounter);
				prefsEditor.commit();

				if (addcounter == DataManager.addcounter) {
					displayInterstitial();
					addcounter = 0;
					prefsEditor.putInt(TAG_NAME, addcounter);
					prefsEditor.commit();
				} else {
					displaynextquestion();

				}
			}

		}, SPLASHTIME);
	}

	public void displaynextquestion() {

		currentQuestion++;

		if (currentQuestion < totalQueLen) {

			timer = new MyCounter(savedtimer * 1000, 1000);
			timer.start();

			getquestionsanswers(currentQuestion);

			audio("next");

		} else {

			timer.cancel();
			DataManager.currentxp = myxp;
			if (!ischallenge) {

				Intent iScore = new Intent(Timer_questions.this,
						SendChallenegActivity.class);
				iScore.putExtra("rightans", rightans);
				iScore.putExtra("totalques", totalQueLen);
				iScore.putExtra("category", category);
				iScore.putExtra("xp", myxp);
				finish();
				startActivity(iScore);
			} else {
				Intent iScore = new Intent(Timer_questions.this,
						EndGameActivity.class);
				iScore.putExtra("rightans", rightans);
				iScore.putExtra("totalques", totalQueLen);
				iScore.putExtra("category", category);
				iScore.putExtra("xp", myxp);
				finish();
				startActivity(iScore);
			}
		}

	}

	

	public void audio(String message) {

		if(message.equals("right"))
		{
			startSound("correct.wav");
		}else
		{
			startSound("wrong.wav");
		}

	}
	
	public void startSound(String filename){
	  
	    MediaPlayer player = new MediaPlayer();
	    try {
	    	  AssetFileDescriptor afd = getAssets().openFd(filename);
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
		    player.prepare();
		    player.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public void vibrate() {

		vibe.vibrate(100);

	}

	public void getquestionsanswers(int index) {

		que = questionlist.get(index).getQuestion();
		opt1 = questionlist.get(index).getOpt1();
		opt2 = questionlist.get(index).getOpt2();
		opt3 = questionlist.get(index).getOpt3();
		opt4 = questionlist.get(index).getOpt4();
		answer = questionlist.get(index).getAnswer();
		url = questionlist.get(index).getImage();

		setCurrentQuestion();

	}

	public void setCurrentQuestion() {

		try {

			i++;

			taOpt1.setVisibility(View.VISIBLE);
			taOpt2.setVisibility(View.VISIBLE);
			taOpt3.setVisibility(View.VISIBLE);
			taOpt4.setVisibility(View.VISIBLE);

			taOpt1.setClickable(true);
			taOpt2.setClickable(true);
			taOpt3.setClickable(true);
			taOpt4.setClickable(true);

			ArrayList<String> optionlist = new ArrayList<String>();

			optionlist.add(opt1);
			optionlist.add(opt2);
			optionlist.add(opt3);
			optionlist.add(opt4);

			Collections.shuffle(optionlist);
			taOpt1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			taOpt2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			taOpt3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			taOpt4.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			taOpt1.setTextColor(Color.BLACK);
			taOpt2.setTextColor(Color.BLACK);
			taOpt3.setTextColor(Color.BLACK);
			taOpt4.setTextColor(Color.BLACK);
			taOpt1.setBackgroundResource(R.drawable.normal);
			taOpt2.setBackgroundResource(R.drawable.normal);
			taOpt3.setBackgroundResource(R.drawable.normal);
			taOpt4.setBackgroundResource(R.drawable.normal);
			taQue.setText(que);
			taOpt1.setText(optionlist.get(0).toString());
			taOpt2.setText(optionlist.get(1).toString());
			taOpt3.setText(optionlist.get(2).toString());
			taOpt4.setText(optionlist.get(3).toString());

			if (url.equalsIgnoreCase("blank")) {
				img.setVisibility(View.GONE);
			} else {
				url = DataManager.photourl + url;
				img.setVisibility(View.VISIBLE);
				img.setImage(url);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyCounter extends CountDownTimer {

		public MyCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {

			nextquestion(1000);

		}

		@Override
		public void onTick(long millisUntilFinished) {

			Integer milisec = new Integer(
					new Double(millisUntilFinished).intValue());

			Integer cd_secs = milisec / 1000;

			Integer seconds = (cd_secs % 3600) % 60;
			currentmilisecond = seconds;

			progresstimer.setmSubCurProgress(seconds);
		}

	}

	@Override
	public void onBackPressed() {

	
		if(!backpressstart)
		{
			timer.cancel();
		new AlertDialog.Builder(this)
				.setTitle("Çıkış?")
				.setMessage("Bu testten ayrılmak istediğine emin misin?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {

								timer.cancel();
								DataManager.currentxp = myxp;
								if (!ischallenge) {
									Intent iScore = new Intent(
											Timer_questions.this,
											SendChallenegActivity.class);
									iScore.putExtra("rightans", rightans);
									iScore.putExtra("totalques", totalQueLen);
									iScore.putExtra("category", category);
									iScore.putExtra("xp", myxp);
									finish();
									startActivity(iScore);
								} else {
									Intent iScore = new Intent(		
											Timer_questions.this,
											EndGameActivity.class);
									iScore.putExtra("rightans", rightans);
									iScore.putExtra("totalques", totalQueLen);
									iScore.putExtra("category", category);
									iScore.putExtra("xp", myxp);
									finish();
									startActivity(iScore);
								}
							}
						}).create().show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		timer.cancel();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();

		timer.cancel();

	}

}
