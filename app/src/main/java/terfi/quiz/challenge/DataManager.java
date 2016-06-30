package terfi.quiz.challenge;

import java.util.ArrayList;

import terfi.quiz.challenge.pojo.CategoryList;
import terfi.quiz.challenge.pojo.ChallenegePojo;
import terfi.quiz.challenge.pojo.HistoryPojo;
import terfi.quiz.challenge.pojo.MyProfilePojo;
import terfi.quiz.challenge.pojo.OpponentUser;
import terfi.quiz.challenge.pojo.QuestionsPojo;

public class DataManager {
	
	
	//Change url....
	public static String url = "http://xn--rtbeterfi-q9a.com/zaptiyeDuello/";
	public static String photourl = "http://xn--rtbeterfi-q9a.com/zaptiyeDuello/upload/";
	
	public static String PROJECT_NUMBER = "447398075468";  // GCM Project number Replace with yours
	
	
	public  static  String timer = "61" ;

	public static int addcounter = 7;  // show interstial ads after every 4 questions
	public static String appurl = "https://play.google.com/store/apps/details?id=terfi.quiz.challenge";
	public static String admobid = "ca-app-pub-3320533451108667/1188345833";
	public static String share = "Rütbe terfi uygulamasını bu adresten indirebilirsiniz : "+appurl;

	public static String username = "";
	public static ArrayList<CategoryList> categorylist;
	public static ArrayList<QuestionsPojo> questionlist;
	public static ArrayList<OpponentUser> opponenetuser;
	public static ArrayList<ChallenegePojo> challenegelist;
	public static ArrayList<HistoryPojo> historylist;
	public  static  ArrayList<CategoryList> categorynamelist ;
	public static ArrayList<MyProfilePojo> userprofilelist;
	public static String gameid = "";
	public static String status = "";
	public static String message = "";
	public static String myxp = "";
	

	
	public static boolean ismanual = false;
			
	public static boolean ischallenege = false;
	
	
	
	public static String selectedgameid = "";
	public static String selectedoppid = "";
	public static String selectedcategory = "";
	public static String selecteduserid = "";
	
	public static String endgamescore = "";
	public static String gameresult = "";
	public static int currentxp = 0;
}
