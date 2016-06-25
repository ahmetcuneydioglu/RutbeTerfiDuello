package terfi.quiz.challenge;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import terfi.quiz.challenge.utils.AndroidMultiPartEntity;
import terfi.quiz.challenge.utils.AndroidMultiPartEntity.ProgressListener;

public class RegisterActivity extends Activity implements OnClickListener {

	EditText etemail, etfname, etlname, etpassword, etconfirmpass, etgender;
	Button btnprofilepic, btnregister, btncancel;
	ProgressDialog pDialog;
	ConnectionDetector connection;
	String email = "", firstname = "", password = "", confirmpassword = "",
			lastname = "", gender = "",  country = "", regid = "";
	String msg = "";
	public static final int MEDIA_TYPE_IMAGE = 1;
	Uri fileuri;
	private static final int REQUEST_CAMERA = 1;
	private static final int SELECT_FILE = 2;
	String deviceid = "", selectedfile = "";
	public static final int progress_bar_type = 0;
	long totalSize = 0;
	double longitude = 0, latitude = 0;
	String PROJECT_NUMBER = DataManager.PROJECT_NUMBER;
	GPSTracker gps;
	String strLatitude, strLongitude;
	Geocoder geocoder;
	SessionManager session;
	GoogleCloudMessaging gcm;
	boolean fileselected = false;
	Typeface normal, bold;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		normal = Typeface.createFromAsset(getAssets(), "normal.ttf");
		bold = Typeface.createFromAsset(getAssets(), "bold.ttf");
		session = new SessionManager(this);
		geocoder = new Geocoder(this, Locale.getDefault());
		gps = new GPSTracker(this);
		getRegId();

		getlocation();

		deviceid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		connection = new ConnectionDetector(this);
		widgets();

	}

	public void getRegId() {

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;

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

	public void widgets() {
		etemail = (EditText) findViewById(R.id.etemail);
		etfname = (EditText) findViewById(R.id.etfname);
		etlname = (EditText) findViewById(R.id.etlname);
		etpassword = (EditText) findViewById(R.id.etpassword);
		etconfirmpass = (EditText) findViewById(R.id.etconfirmpass);
		etgender = (EditText) findViewById(R.id.etgender);
		btnprofilepic = (Button) findViewById(R.id.btnprofile);
		btnregister = (Button) findViewById(R.id.btnregister);
		btncancel = (Button) findViewById(R.id.btncancel);

		etgender.setOnClickListener(this);
		btnprofilepic.setOnClickListener(this);
		btnregister.setOnClickListener(this);
		btncancel.setOnClickListener(this);
		
		etemail.setTypeface(bold);
		etfname.setTypeface(bold);
		etlname.setTypeface(bold);
		etpassword.setTypeface(bold);
		etconfirmpass.setTypeface(bold);
		etgender.setTypeface(bold);
		btnprofilepic.setTypeface(bold);
		btncancel.setTypeface(bold);
		btnregister.setTypeface(bold);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnprofilepic) {
			selectpic();
		} else if (v == btnregister) {
			register();
		} else if (v == btncancel) {
			Intent i = new Intent(RegisterActivity.this, FbLogin.class);
			finish();
			startActivity(i);
			overridePendingTransition(0, 0);
		} else if (v == etgender) {
			selectgender(etgender);
		}

	}

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
				
					country = "";
				} else {
					List<Address> addresses;
					addresses = geocoder
							.getFromLocation(latitude, longitude, 1);

					System.out.println("address..." + addresses.toArray());

					
					country = "";

				}

			
				System.out.println("country----" + country);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void selectgender(final EditText v) {

		final Dialog myDialog = new Dialog(this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		myDialog.setContentView(R.layout.custom_spinner_dialog);
		myDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		TextView txtheader = (TextView) myDialog.findViewById(R.id.txtheader);

		txtheader.setText("Cinsiyetiniz");

		ArrayList<String> genderlist = new ArrayList<String>();
		genderlist.add("Erkek");
		genderlist.add("Kız");

		final ListView listview = (ListView) myDialog
				.findViewById(R.id.spinnerlist);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, genderlist);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				myDialog.dismiss();
				TextView txtview = (TextView) view.findViewById(R.id.spntxt);
				gender = txtview.getText().toString();
				v.setText(gender);

			}
		});

		myDialog.show();

	}

	public void register() {
		email = etemail.getText().toString();
		firstname = etfname.getText().toString();
		password = etpassword.getText().toString();
		confirmpassword = etconfirmpass.getText().toString();
		lastname = etlname.getText().toString();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (email.length() < 2) {
			etemail.setError("Lütfen geçerli bir email adresi girin");
			etemail.setFocusable(true);
		} else if (firstname.length() < 1) {
			etfname.setError("Lütfen geçerli bir ad girin");
			etfname.setFocusable(true);
		} else if (password.length() < 1) {
			etpassword.setError("Lütfen geçerli bir şifre girin");
			etpassword.setFocusable(true);
		} else if (confirmpassword.length() < 1) {
			etconfirmpass.setError("Lütfen geçerli bir teyit şifresi girin");
			etconfirmpass.setFocusable(true);
		} else if (lastname.length() < 1) {
			etlname.setError("Lütfen geçerli bir soyisim girin");
			etlname.setFocusable(true);
		} else {
			if (!password.equals(confirmpassword)) {
				etconfirmpass.setError("şifre eşleşmiyor");
				etconfirmpass.setFocusable(true);
			} else {
				if (connection.isConnectingToInternet()) {
					if (email.contains("@")) {
						if (email.contains(".")) {
							new UploadFileToServer().execute();
						} else {
							etemail.setError("Lütfen geçerli bir email adresi girin");
							etemail.setFocusable(true);
						}
					} else {
						etemail.setError("Lütfen geçerli bir email adresi girin");
						etemail.setFocusable(true);
					}
				} else {
					Toast.makeText(RegisterActivity.this,
							"İnternete bağlanın...", Toast.LENGTH_LONG)
							.show();
				}
			}
		}

	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(RegisterActivity.this, FbLogin.class);
		finish();
		startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void selectpic() {

		final Dialog myDialog = new Dialog(this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		myDialog.setContentView(R.layout.custom_camera);
		myDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		Button btncamera = (Button) myDialog.findViewById(R.id.btncamera);
		Button btngallery = (Button) myDialog.findViewById(R.id.btngallery);

		btncamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				Uri uriSavedImage = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				fileuri = uriSavedImage;
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				fileuri = uriSavedImage;

				startActivityForResult(cameraIntent, REQUEST_CAMERA);
				myDialog.cancel();
			}
		});

		btngallery.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(intent, SELECT_FILE);
				myDialog.cancel();
			}
		});

		myDialog.show();
	}

	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image / video
	 */
	public File getOutputMediaFile(int type) {

		// External sdcard location
		File imagesFolder = new File(Environment.getExternalStorageDirectory(),
				"MySteps");
		imagesFolder.mkdirs();

		File image = new File(imagesFolder, deviceid + ".png");

		return image;
	}

	private Bitmap getBitmap(String path) {
		int IMAGE_MAX_SIZE = 250000;
		File externalFile = new File(path);
		Uri uri = Uri.fromFile(externalFile);
		ContentResolver mconContentResolver = getContentResolver();
		InputStream in = null;
		try {

			in = mconContentResolver.openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d("TAG", "scale = " + scale + ", orig-width: " + o.outWidth
					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in = mconContentResolver.openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Log.d("TAG", "1th scale operation dimenions - width: " + width
						+ ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();

			} else {
				b = BitmapFactory.decodeStream(in);
			}
			in.close();

			Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
					+ b.getHeight());
			return b;
		} catch (IOException e) {
			Log.e("TAG", e.getMessage(), e);
			return null;
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		android.database.Cursor cursor = managedQuery(contentUri, proj, // Which
																		// columns
																		// to
																		// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				if (data != null) {

					Uri selectedImageUri = data.getData();
					fileselected = true;
					selectedfile = getRealPathFromURI(selectedImageUri);
					Bitmap bitmap = getBitmap(selectedfile);

					FileOutputStream fOut;
					try {
						fOut = new FileOutputStream(selectedfile);

						bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
						fOut.flush();
						fOut.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			else if (requestCode == REQUEST_CAMERA) {

				File imagesFolder = new File(
						Environment.getExternalStorageDirectory(), "MySteps");
				File image = new File(imagesFolder, deviceid + ".png");
				imagesFolder.mkdirs();
				fileselected = true;
				selectedfile = image.toString();

				Bitmap bitmap = getBitmap(selectedfile);

				FileOutputStream fOut;
				try {
					fOut = new FileOutputStream(image);

					bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
					fOut.flush();
					fOut.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case progress_bar_type: // we set this to 0
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Sending file....");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
			return pDialog;

		default:
			return null;
		}
	}

	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			pDialog.setProgress(progress[0]);

		}

		@Override
		protected String doInBackground(Void... params) {
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(DataManager.url
					+ "registerviaemail.php");

			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

				if (fileselected) {
					File sourceFile = new File(selectedfile);

					// Adding file data to http body
					entity.addPart("image", new FileBody(sourceFile));
				}

				// Adding file data to http body
				Charset chars = Charset.forName("UTF-8");

				StringBody emailb = new StringBody(email, chars);
				StringBody deviceidb = new StringBody(deviceid, chars);
				StringBody firstnameb = new StringBody(firstname, chars);
				StringBody passwordb = new StringBody(password, chars);
				StringBody lastnameb = new StringBody(lastname, chars);
				StringBody genderb = new StringBody(gender, chars);
				StringBody cityb = new StringBody("", chars);
				StringBody countryb = new StringBody("", chars);
				StringBody logintypeb = new StringBody("email", chars);
				StringBody gcmidb = new StringBody(regid, chars);

				entity.addPart("email", emailb);
				entity.addPart("firstname", firstnameb);
				entity.addPart("password", passwordb);
				entity.addPart("lastname", lastnameb);
				entity.addPart("gender", genderb);
				entity.addPart("city", cityb);
				entity.addPart("country", countryb);  // we will add this part later...
				entity.addPart("logintype", logintypeb);
				entity.addPart("gcmid", gcmidb);
				entity.addPart("deviceid", deviceidb);

				totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}

			return responseString;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("Response", "Response from server: " + result);
			super.onPostExecute(result);
			pDialog.dismiss();
			String success = "";
			try {
				JSONObject jobj = new JSONObject(result);

				success = jobj.getString("success");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (success.equals("1")) {
				Toast.makeText(
						getApplicationContext(),
						"Başarılı bir şekilde kaydoldunuz...Giriş yapabilirsiniz",
						Toast.LENGTH_LONG).show();
				Intent i = new Intent(RegisterActivity.this, FbLogin.class);
				finish();
				startActivity(i);
				overridePendingTransition(0, 0);
			}
		}

	}

}
