package terfi.quiz.challenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import terfi.quiz.challenge.view.GoogleProgress;


public class FbLogin extends Activity  {


	private static final String TAG = "MainFragment";

	Typeface normal, bold;
	TextView txtskip;
	GoogleCloudMessaging gcm;
	String regid, name, username, location, lastname, logintype, profileurl,
			gender, city, country;
	String PROJECT_NUMBER = DataManager.PROJECT_NUMBER;
	private ProgressDialog progress;
	String msg = "";
	SharedPreferences prefs;
	SessionManager session;
	Button btnlogin, register;
	// google+ login
	private static final int RC_SIGN_IN = 0;
	GPSTracker gps;
	String strLatitude, strLongitude, xp;
	private boolean mIntentInProgress;
	private ConnectionResult mConnectionResult;
	private boolean mSignInClicked;
	private CallbackManager callbackManager;
	Geocoder geocoder;
	double longitude = 0, latitude = 0;
	private String deviceid = "";
	Button btnregister, btnemailsignin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_fb_login);

		normal = Typeface.createFromAsset(getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(getAssets(), "bold.ttf");


		session = new SessionManager(this);
		callbackManager = CallbackManager.Factory.create();
		if (!session.isLoggedIn()) {
			callFacebookLogout(getApplicationContext());


		}
		deviceid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		geocoder = new Geocoder(this, Locale.getDefault());
		gps = new GPSTracker(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		getRegId();

		getlocation();

		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"terfi.quiz.challenge", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}

		 btnregister = (Button) findViewById(R.id.btnregister);
		 
		
		btnemailsignin = (Button) findViewById(R.id.btnemailsignin);
		LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);
		loginButton.setReadPermissions("email");
		loginButton.registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(final LoginResult loginResult) {
						// App code
						System.out.println("Success---" + loginResult.getAccessToken());
						Toast.makeText(FbLogin.this, "Login Successful!", Toast.LENGTH_LONG).show();
						logintype = "2";
						GraphRequest request =    GraphRequest.newMeRequest(
								loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(JSONObject json, GraphResponse response) {
										if (response.getError() != null) {
											// handle error
											System.out.println("ERROR");
										} else {
											System.out.println("Success");
											try {

												String jsonresult = String.valueOf(json);
												System.out.println("JSON Result" + jsonresult);
												username = json.getString("id");
												name =json.getString("name");
												lastname = "";
												String firsturl = "http://graph.facebook.com/"+username+"/picture?type=large";
												profileurl = GetDirectURL(firsturl);
//												email = json.getString("email");
//												gender = json.getString("gender").toString();

												logintype = "facebook";

												new login().execute();

//												username = json.getString("id");
//												password = json.getString("id");
//												name = json.getString("name");
//												email = json.getString("email");
//
//												login();
											} catch (JSONException e) {
												System.out.println("here----" + e.getMessage());
												e.printStackTrace();
											}
										}
									}

								});
						Bundle parameters = new Bundle();
						parameters.putString("fields", "id,name,email");
						request.setParameters(parameters);
						request.executeAsync();
//                                executeAsync();



					}
					@Override
					public void onCancel() {
						// App code
						System.out.println("onCancel");

					}

					@Override
					public void onError(FacebookException exception) {
						// App code
						System.out.println("exception--"+exception.toString());

					}
				});

		
		btnregister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(FbLogin.this, RegisterActivity.class);
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);
				
			}
		});
		
		btnemailsignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(FbLogin.this, LoginActivity.class);
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);
			}
		});
		
		 btnemailsignin.setTypeface(bold);
		 btnregister.setTypeface(bold);

	}

	public static void callFacebookLogout(Context context) {

//		callFacebookLogout(context);

	}

//	private void signOutFromGplus() {
//
//		if (mGoogleApiClient.isConnected()) {
//			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//			mGoogleApiClient.disconnect();
//			mGoogleApiClient.connect();
//			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
//					.setResultCallback(new ResultCallback<Status>() {
//
//						@Override
//						public void onResult(Status arg0) {
//							// TODO Auto-generated method stub
//							mGoogleApiClient.disconnect();
//						}
//
//					});
//		}
//		if (!mGoogleApiClient.isConnecting()) {
//			mSignInClicked = false;
//			return;
//
//		}
//
//
//
//	}

//	private void signInWithGplus() {
//
//		if (!mGoogleApiClient.isConnecting()) {
//			mSignInClicked = true;
//			resolveSignInError();
//
//		}
//	}

	public void getlocation() {

		if (gps.canGetLocation()) {
			try {
				if (gps.canGetLocation()) {
					Log.d("Your Location", "latitude:" + gps.getLatitude()
							+ ", longitude: " + gps.getLongitude());
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
				}

				if (latitude == 0.0 && longitude == 0.0) {
					city = "";
					country = "";
				} else {
					List<Address> addresses;
					
					
					addresses = geocoder
							.getFromLocation(latitude, longitude, 1);

					
				

					city = "";
					country = "";

				}

				System.out.println("city----" + city);
				System.out.println("country----" + country);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

//	private void getProfileInformation() {
//		try {
//			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//				Person currentPerson = Plus.PeopleApi
//						.getCurrentPerson(mGoogleApiClient);
//				String personName = currentPerson.getDisplayName();
//				String personPhotoUrl = currentPerson.getImage().getUrl();
//				String personGooglePlusProfile = currentPerson.getUrl();
//				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//				username = currentPerson.getId();
//				name = currentPerson.getDisplayName();
//				lastname = "";
//				profileurl = currentPerson.getImage().getUrl();
//				int sex = currentPerson.getGender();
////				city = currentPerson.getCurrentLocation();
//
//				System.out.println("sex---" + sex);
//				System.out.println("city---" + city);
//
//				if(sex == 0)
//				{
//					gender = "male";
//				}else
//				{
//					gender = "female";
//				}
//
//				logintype = "google";
//
//				Log.e(TAG, "Name: " + personName + ", plusProfile: "
//						+ personGooglePlusProfile + ", username: " + username
//						+ ", Image: " + personPhotoUrl);
//
//				profileurl = personPhotoUrl.substring(0,
//						personPhotoUrl.length() - 2) + 150;
//
//				new login().execute();
//			} else {
//				Toast.makeText(getApplicationContext(),
//						"Person information is null", Toast.LENGTH_LONG).show();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("Really Exit?")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intent = new Intent(Intent.ACTION_MAIN);
								intent.addCategory(Intent.CATEGORY_HOME);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								finish();
								startActivity(intent);
							}
						}).create().show();
	}

//	private void onSessionStateChange(Session session, SessionState state,
//			Exception exception) {
//		if (state.isOpened()) {
//
//			Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//				@Override
//				public void onCompleted(GraphUser user, Response response) {
//					// TODO Auto-generated method stub
//
//					if (user != null) {
//
//						username = user.getId();
//						name = user.getFirstName();
//						lastname = user.getLastName();
//
//						String firsturl = "http://graph.facebook.com/"+username+"/picture?type=large";
//						profileurl = GetDirectURL(firsturl);
//						gender = user.getProperty("gender").toString();
//
//						logintype = "facebook";
//
//						new login().execute();
//					}
//				}
//			}).executeAsync();
//
//		} else if (state.isClosed()) {
//			Log.i(TAG, "Logged out...");
//		}
//
//	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
		// mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

//		if (mGoogleApiClient.isConnected()) {
//			mGoogleApiClient.disconnect();
//		}
	}


	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}



	@Override
	public void onPause() {
		super.onPause();
		//uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	//	uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	//	uiHelper.onSaveInstanceState(outState);
	}

	private void resolveSignInError() {

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				startIntentSenderForResult(mConnectionResult.getResolution()
						.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;

			}
		}

	}

	public void getRegId() {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(FbLogin.this);
					}
					regid = gcm.register(PROJECT_NUMBER);
					
					if(regid != null)
					{
					msg = "Device registered, registration ID=" + regid;
					}

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

					System.out.println("Error---" + ex.getMessage());
				}

				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				System.out.println("Registerid---" + regid);
			}
		}.execute(null, null, null);

	}

	public class login extends AsyncTask<String, Void, String> {
		boolean response = false;

		@Override
		protected void onPreExecute() {
			progress = GoogleProgress.Progressshow(FbLogin.this);
			progress.show();

		}

		@Override
		protected String doInBackground(String... params) {

			registernotification();

			return "";

		}

		@Override
		protected void onPostExecute(String result) {

			progress.cancel();

			DataManager.username = username;
		
			if (logintype.equals("facebook")) {

					
					String firsturl = "http://graph.facebook.com/"+username+"/picture?type=square";
					GetDirectURL(firsturl);
					session.createLoginSession(username, name, lastname,
							deviceid, xp);
					session.setlogintype(logintype);
					
					Intent i = new Intent(FbLogin.this, MainActivity.class);
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

	public void registernotification() {

		String url = DataManager.url + "register.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();	
		paramas.add(new BasicNameValuePair("gcmid", regid));
		paramas.add(new BasicNameValuePair("userid", username));
		paramas.add(new BasicNameValuePair("firstname", name));
		paramas.add(new BasicNameValuePair("lastname", lastname));
		paramas.add(new BasicNameValuePair("profilepic", profileurl));
		paramas.add(new BasicNameValuePair("logintype", logintype));
		paramas.add(new BasicNameValuePair("gender", gender));
		paramas.add(new BasicNameValuePair("country", ""));
		paramas.add(new BasicNameValuePair("deviceid", deviceid));
		UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(paramas, HTTP.UTF_8);
			get.setEntity(ent);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("returnString---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);

					
					String xp = obj.getString("xp");
					session.setxp(xp);
//					SharedPreferences.Editor se = prefs.edit();
//					se.putString("status", status);
//					se.commit();

				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void connectionerror() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(FbLogin.this);

		alertDialog.setTitle("Error!");

		alertDialog.setMessage("Connection Lost ! Try Again");

		alertDialog.setPositiveButton("Retry",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						new login().execute();

					}
				});

		alertDialog.show();
	}





	
}
