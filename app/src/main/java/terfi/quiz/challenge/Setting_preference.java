package terfi.quiz.challenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class Setting_preference {
	SharedPreferences pref;
	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "QUIZ";

	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String KEY_USERNAME = "username";
	private static final String IS_UPDATE= "isupdate";
	
	private static final String SKIP = "skip";
	private static final String FIFTYFIFTY = "fiftyfifty";
	private static final String TIMER = "timer";

	public Setting_preference(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	public void updateSKIP(int skip) {

		editor.putInt(SKIP, skip);
		editor.commit();

	}

	public int getSkip() {
		int skips = 0;

		skips = pref.getInt(SKIP, 5);

		return skips;
	}
	
	public void updatetimer(int timer) {

		editor.putInt(TIMER, timer);
		editor.commit();

	}

	public int gettimer() {
		int timer = 0;

		timer = pref.getInt(TIMER, 5);

		return timer;
	}
	
	public void updatefifty(int fifty) {

		editor.putInt(FIFTYFIFTY, fifty);
		editor.commit();

	}

	public int getfifty() {
		int fifty = 0;

		fifty = pref.getInt(FIFTYFIFTY, 5);

		return fifty;
	}
	public void entername(String name)
	{
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_USERNAME, name);


		editor.commit();
		
	}
	
	public void updated()
	{
		editor.putBoolean(IS_UPDATE, true);
		

		editor.commit();
		
	}
	
	public boolean isLoggedIn(){		
		return pref.getBoolean(IS_LOGIN, false);
	}
	
	public boolean isUpdate(){		
		return pref.getBoolean(IS_UPDATE, false);
	}
	
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();


		

		user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

		return user;
	}
	


}