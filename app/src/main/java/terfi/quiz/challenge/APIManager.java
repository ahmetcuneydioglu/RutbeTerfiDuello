package terfi.quiz.challenge;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import terfi.quiz.challenge.pojo.CategoryList;
import terfi.quiz.challenge.pojo.ChallenegePojo;
import terfi.quiz.challenge.pojo.HistoryPojo;
import terfi.quiz.challenge.pojo.MyProfilePojo;
import terfi.quiz.challenge.pojo.OpponentUser;
import terfi.quiz.challenge.pojo.QuestionsPojo;

public class APIManager {

	public static String url = DataManager.url;

	public static boolean submitquestion(String question, String opt1,
			String opt2, String opt3, String opt4, String answer, String catid,
			String subcatid) {
		boolean result = false;

		String url = DataManager.url + "submitquestion.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("question", question));
		paramas.add(new BasicNameValuePair("option1", opt1));
		paramas.add(new BasicNameValuePair("option2", opt2));
		paramas.add(new BasicNameValuePair("option3", opt3));
		paramas.add(new BasicNameValuePair("option4", opt4));
		paramas.add(new BasicNameValuePair("answer", answer));
		paramas.add(new BasicNameValuePair("catid", catid));
		paramas.add(new BasicNameValuePair("subcatid", subcatid));
		System.out.println("subcatid---" + subcatid);

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
				System.out.println("response---" + returnString.toString());

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean getcategories() {
		boolean result = false;

		String geturl = url + "getCategories.php";
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;

				ArrayList<CategoryList> categorylist = new ArrayList<CategoryList>();

				try {

					JSONObject c = new JSONObject(response);

					String status = c.getString("success");

					if (status.equals("1")) {
						JSONArray jarray = c.getJSONArray("category");

						System.out.println("response---" + response);

						for (int i = 0; i < jarray.length(); i++)

						{
							CategoryList cat = new CategoryList();

							String id = new String(jarray.getJSONObject(i)
									.getString("id").getBytes("UTF-8"), "UTF-8");
							String category_name = new String(jarray
									.getJSONObject(i)
									.getString("category_name")
									.getBytes("UTF-8"), "UTF-8");
							String category_image = new String(jarray
									.getJSONObject(i)
									.getString("category_image")
									.getBytes("UTF-8"), "UTF-8");
							String subcat_count = new String(jarray
									.getJSONObject(i).getString("subcat_count")
									.getBytes("UTF-8"), "UTF-8");

							cat.setId(id);
							cat.setName(category_name);
							cat.setSubcat_count(subcat_count);
							cat.setCategory_image(category_image);

							categorylist.add(cat);
						}

						DataManager.categorylist = categorylist;

						DataManager.status = status;
					} else {
						DataManager.status = status;
					}

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

		return result;
	}

	public static boolean getquestionbycatagory(String categoryid,
			String user_id) {
		boolean result = false;

		String geturl = url + "createNewChallenge.php?category_id="
				+ categoryid + "&user_id=" + user_id;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<OpponentUser> opponentuser = new ArrayList<OpponentUser>();
				ArrayList<QuestionsPojo> questionlist = new ArrayList<QuestionsPojo>();
				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);
					String xp = c.getString("xp");
					JSONArray jarray = c.getJSONArray("questions");

					JSONObject jobj = null;

					for (int i = 0; i < jarray.length(); i++)

					{
						QuestionsPojo que = new QuestionsPojo();

						jobj = jarray.getJSONObject(i);

						String id = jobj.getString("id");
						String question = jobj.getString("question_text");
						String quesimage = jobj.getString("quesimage");
						String option1 = jobj.getString("option1");
						String option2 = jobj.getString("option2");
						String option3 = jobj.getString("option3");
						String option4 = jobj.getString("option4");
						String correctans = jobj.getString("correctans");

						if (correctans.equalsIgnoreCase("option1")) {
							correctans = option1;
						} else if (correctans.equalsIgnoreCase("option2")) {
							correctans = option2;
						} else if (correctans.equalsIgnoreCase("option3")) {
							correctans = option3;
						} else if (correctans.equalsIgnoreCase("option4")) {
							correctans = option4;
						}

						que.setQid(id);
						que.setQuestion(question);
						que.setImage(quesimage);
						que.setOpt1(option1);
						que.setOpt2(option2);
						que.setOpt3(option3);
						que.setOpt4(option4);
						que.setAnswer(correctans);

						questionlist.add(que);

					}
					DataManager.questionlist = questionlist;
					JSONArray opponentarray = c.getJSONArray("opponent_user");

					JSONObject oarray = null;

					for (int i = 0; i < opponentarray.length(); i++)

					{
						OpponentUser opp = new OpponentUser();

						oarray = opponentarray.getJSONObject(i);

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String oppxp = oarray.getString("opp_xp");
						String logintype = oarray.getString("logintype");

						opp.setOppuserid(ouserid);
						opp.setOppcountry(ocountry);
						opp.setOppfname(ofirstname);
						opp.setOpplname(olastname);
						opp.setOppprofilepic(oprofilepic);
						opp.setOppxp(oppxp);
						opp.setOpplogintype(logintype);

						opponentuser.add(opp);
					}
					DataManager.gameid = c.getString("game_id");
					DataManager.myxp = xp;
					DataManager.opponenetuser = opponentuser;

				} catch (JSONException e) {
					DataManager.status = "0"; 
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

		return result;
	}
	
	
	public static boolean createManualChallenge(String categoryid,
			String user_id, String oppuserid) {
		boolean result = false;

		String geturl = url + "createNewChallengeWithUserid.php?category_id="
				+ categoryid + "&user_id=" + user_id+"&oppuserid="+oppuserid;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<OpponentUser> opponentuser = new ArrayList<OpponentUser>();
				ArrayList<QuestionsPojo> questionlist = new ArrayList<QuestionsPojo>();
				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);
					String xp = c.getString("xp");
					JSONArray jarray = c.getJSONArray("questions");

					JSONObject jobj = null;

					for (int i = 0; i < jarray.length(); i++)

					{
						QuestionsPojo que = new QuestionsPojo();

						jobj = jarray.getJSONObject(i);

						String id = jobj.getString("id");
						String question = jobj.getString("question_text");
						String quesimage = jobj.getString("quesimage");
						String option1 = jobj.getString("option1");
						String option2 = jobj.getString("option2");
						String option3 = jobj.getString("option3");
						String option4 = jobj.getString("option4");
						String correctans = jobj.getString("correctans");

						if (correctans.equalsIgnoreCase("option1")) {
							correctans = option1;
						} else if (correctans.equalsIgnoreCase("option2")) {
							correctans = option2;
						} else if (correctans.equalsIgnoreCase("option3")) {
							correctans = option3;
						} else if (correctans.equalsIgnoreCase("option4")) {
							correctans = option4;
						}

						que.setQid(id);
						que.setQuestion(question);
						que.setImage(quesimage);
						que.setOpt1(option1);
						que.setOpt2(option2);
						que.setOpt3(option3);
						que.setOpt4(option4);
						que.setAnswer(correctans);

						questionlist.add(que);

					}
					DataManager.questionlist = questionlist;
					JSONArray opponentarray = c.getJSONArray("opponent_user");

					JSONObject oarray = null;

					for (int i = 0; i < opponentarray.length(); i++)

					{
						OpponentUser opp = new OpponentUser();

						oarray = opponentarray.getJSONObject(i);

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String oppxp = oarray.getString("opp_xp");
						String logintype = oarray.getString("logintype");

						opp.setOppuserid(ouserid);
						opp.setOppcountry(ocountry);
						opp.setOppfname(ofirstname);
						opp.setOpplname(olastname);
						opp.setOppprofilepic(oprofilepic);
						opp.setOppxp(oppxp);
						opp.setOpplogintype(logintype);

						opponentuser.add(opp);
					}
					DataManager.gameid = c.getString("game_id");
					DataManager.myxp = xp;
					DataManager.opponenetuser = opponentuser;

				} catch (JSONException e) {
					DataManager.status = "0"; 
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

		return result;
	}

	public static boolean logout(String userid, String deviceid) {
		boolean result = false;

		String url = DataManager.url + "logout.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
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
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean changepassword(String userid, String deviceid,
			String newpassword) {
		boolean result = false;

		String url = DataManager.url + "updatepassword.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("userid", userid));
		paramas.add(new BasicNameValuePair("password", newpassword));
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
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean resetpassword(String email, String userid) {
		boolean result = false;

		String url = DataManager.url + "resetPassword.php";
		HttpClient client = new DefaultHttpClient();
		HttpPost get = new HttpPost(url);

		List<NameValuePair> paramas = new ArrayList<NameValuePair>();
		paramas.add(new BasicNameValuePair("email", email));

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
				System.out.println("response---" + returnString.toString());
				JSONObject obj;
				try {
					obj = new JSONObject(returnString);
					String success = obj.getString("success");

					DataManager.status = success;

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean getuserprofile(Context context, String myuserid,
			String deviceid, String profileid) {

		boolean result = false;
		String url = DataManager.url + "getuserprofile.php?userid=" + myuserid
				+ "&deviceid=" + deviceid + "&profileid=" + profileid;
		url = url.replaceAll(" ", "%20");
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		String returnString = null;
		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {

				returnString = EntityUtils.toString(resEntity);
				System.out.println("response---" + returnString.toString());
				try {

					JSONObject obj = new JSONObject(returnString);
					// String xp = obj.getString("xp");
					String success = obj.getString("success");
					if (success.equals("1")) {

						String totalmatch = obj.getString("total");
						String win = obj.getString("win");
						String xp = obj.getString("xp");
						JSONArray array = obj.getJSONArray("users");

						System.out.println("array ---" + array.length());

						ArrayList<MyProfilePojo> userlist = new ArrayList<MyProfilePojo>();
						MyProfilePojo data;

						for (int i = 0; i < array.length(); i++)

						{

							data = new MyProfilePojo();

							String userid = new String(array.getJSONObject(i)
									.getString("userid").getBytes("UTF-8"),
									"UTF-8");

							String firstname = new String(array
									.getJSONObject(i).getString("firstname")
									.getBytes("UTF-8"), "UTF-8");

							String lastname = new String(array.getJSONObject(i)
									.getString("lastname").getBytes("UTF-8"),
									"UTF-8");

							String profilepic = new String(array
									.getJSONObject(i).getString("profilepic")
									.getBytes("UTF-8"), "UTF-8");

							String logintype = new String(array
									.getJSONObject(i).getString("logintype")
									.getBytes("UTF-8"), "UTF-8");

							String country = new String(array.getJSONObject(i)
									.getString("country").getBytes("UTF-8"),
									"UTF-8");

							data.setCountry(country);
							data.setUserid(userid);
							data.setFname(firstname);
							data.setLname(lastname);
							data.setLogintype(logintype);
							data.setProfilepic(profilepic);
							data.setTotal(totalmatch);
							data.setWin(win);
							data.setXp(xp);
							userlist.add(data);
							result = true;
						}
						
						DataManager.status = success;
						DataManager.userprofilelist = userlist;
					} else if (success.equals("2")) {

						DataManager.status = success;
						String message = obj.getString("message");
					} else {
						DataManager.status = success;
						String message = obj.getString("message");
						DataManager.message = message;
					}
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

		return result;
	}

	public static boolean sendchallnege(String gameid, String userid,
			String userscore, String opponentid, String categoryname) {
		boolean result = false;

		String geturl = url + "sendChallenge.php?game_id=" + gameid
				+ "&user_id=" + userid + "&user_score=" + userscore
				+ "&opponent_id=" + opponentid+"&categoryname="+categoryname;
		 
			 geturl = geturl.replaceAll(" ", "%20");
		
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;

				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

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

		return result;
	}

	public static boolean getReceivedChallenges(String user_id) {
		boolean result = false;

		String geturl = url + "getReceivedChallenges.php?user_id="
				+ user_id;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<ChallenegePojo> challengelist = new ArrayList<ChallenegePojo>();
				System.out.println("user_id---" + user_id);
				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

					JSONArray jarray = c.getJSONArray("gamedetails");

					JSONObject oarray = null;

					for (int i = 0; i < jarray.length(); i++)

					{
						ChallenegePojo opp = new ChallenegePojo();

						oarray = jarray.getJSONObject(i);

						String gameid = oarray.getString("gameid");
						String catid = oarray.getString("catid");
						String category_name = oarray
								.getString("category_name");

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String ologintype = oarray.getString("logintype");

						opp.setUserid(ouserid);
						opp.setProfilepic(oprofilepic);
						opp.setFirstname(ofirstname);
						opp.setLastname(olastname);
						opp.setCountry(ocountry);
						opp.setLogintype(ologintype);
						opp.setGameid(gameid);
						opp.setCategory_name(category_name);
						opp.setCatid(catid);

						challengelist.add(opp);
					}

					DataManager.challenegelist = challengelist;
					DataManager.status = "1";
				} catch (JSONException e) {
					result = true;
					DataManager.status = "0";
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

		return result;
	}

	public static boolean rejectchallenge(String gameid, String oppuserid) {
		boolean result = false;

		String geturl = url + "rejectChallenge.php?game_id=" + gameid
				+ "&opponent_user_id=" + oppuserid;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;

				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

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

		return result;
	}

	public static boolean acceptChallenge(String game_id, String user_id) {
		boolean result = false;

		String geturl = url + "acceptChallenge.php?game_id=" + game_id
				+ "&user_id=" + user_id;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;

				ArrayList<QuestionsPojo> questionlist = new ArrayList<QuestionsPojo>();
				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);
					String xp = c.getString("xp");
					JSONArray jarray = c.getJSONArray("questions");

					JSONObject jobj = null;

					for (int i = 0; i < jarray.length(); i++)

					{
						QuestionsPojo que = new QuestionsPojo();

						jobj = jarray.getJSONObject(i);

						String id = jobj.getString("id");
						String question = jobj.getString("question_text");
						String quesimage = jobj.getString("quesimage");
						String option1 = jobj.getString("option1");
						String option2 = jobj.getString("option2");
						String option3 = jobj.getString("option3");
						String option4 = jobj.getString("option4");
						String correctans = jobj.getString("correctans");

						if (correctans.equalsIgnoreCase("option1")) {
							correctans = option1;
						} else if (correctans.equalsIgnoreCase("option2")) {
							correctans = option2;
						} else if (correctans.equalsIgnoreCase("option3")) {
							correctans = option3;
						} else if (correctans.equalsIgnoreCase("option4")) {
							correctans = option4;
						}

						que.setQid(id);
						que.setQuestion(question);
						que.setImage(quesimage);
						que.setOpt1(option1);
						que.setOpt2(option2);
						que.setOpt3(option3);
						que.setOpt4(option4);
						que.setAnswer(correctans);

						questionlist.add(que);

					}
					DataManager.questionlist = questionlist;

					DataManager.myxp = xp;

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

		return result;
	}

	public static boolean endGame(String game_id, String user_id,
			String userscore, String opponentid, String category_name) {
		boolean result = false;

		String geturl = url + "endGame.php?game_id=" + game_id + "&user_id="
				+ user_id + "&user_score=" + userscore + "&opponent_id="
				+ opponentid + "&category_name=" + category_name;

		geturl = geturl.replaceAll(" ", "%20");

		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<OpponentUser> opponentuser = new ArrayList<OpponentUser>();
				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

					JSONArray opponentarray = c.getJSONArray("opponent_user");

					JSONObject oarray = null;

					for (int i = 0; i < opponentarray.length(); i++)

					{
						OpponentUser opp = new OpponentUser();

						oarray = opponentarray.getJSONObject(i);

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String score = oarray.getString("score");
						String gameresult = oarray.getString("result");

						opp.setOppuserid(ouserid);
						opp.setOppcountry(ocountry);
						opp.setOppfname(ofirstname);
						opp.setOpplname(olastname);
						opp.setOppprofilepic(oprofilepic);

						opponentuser.add(opp);
						DataManager.endgamescore = score;
						DataManager.gameresult = gameresult;
					}

				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}

				DataManager.opponenetuser = opponentuser;

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static boolean getleaderboard(String user_id) {
		boolean result = false;

		String geturl = url + "getLeaderboard.php?userid=" + user_id;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<OpponentUser> opponentuser = new ArrayList<OpponentUser>();

				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

					JSONArray opponentarray = c.getJSONArray("users");

					JSONObject oarray = null;

					for (int i = 0; i < opponentarray.length(); i++)

					{
						OpponentUser opp = new OpponentUser();

						oarray = opponentarray.getJSONObject(i);

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String logintype = oarray.getString("logintype");
						String oppxp = oarray.getString("xp");

						opp.setOppuserid(ouserid);
						opp.setOppcountry(ocountry);
						opp.setOppfname(ofirstname);
						opp.setOpplname(olastname);
						opp.setOppprofilepic(oprofilepic);
						opp.setOppxp(oppxp);
						opp.setOpplogintype(logintype);

						opponentuser.add(opp);
					}

					DataManager.opponenetuser = opponentuser;

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

		return result;
	}
	
	public static boolean gethistory(String user_id) {
		boolean result = false;

		String geturl = url + "getUserHistory.php?user_id=" + user_id;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<HistoryPojo> historylist = new ArrayList<HistoryPojo>();

				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

					JSONArray opponentarray = c.getJSONArray("gamedetails");
					
					JSONObject oarray = null;

					for (int i = 0; i < opponentarray.length(); i++)

					{
						HistoryPojo opp = new HistoryPojo();

						oarray = opponentarray.getJSONObject(i);

						String myscore = oarray.getString("myscore");
						String oppscore = oarray.getString("oppscore");
						String category_name = oarray.getString("category_name");
						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String logintype = oarray.getString("logintype");
						String gameresult = oarray.getString("gameresult");

						opp.setCatname(category_name);
						opp.setOppscore(oppscore);
						opp.setMyscore(myscore);
						opp.setFname(ofirstname);
						opp.setLname(olastname);
						opp.setGameresult(gameresult);
						opp.setCountry(ocountry);
						opp.setLogintype(logintype);
						opp.setProfilepic(oprofilepic);
						

						historylist.add(opp);
					}
					
					if(historylist.size() > 0)
					{

					DataManager.historylist = historylist;
					}else
					{
						result = false;
					}

				} catch (JSONException e) {
					result = false;
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

		return result;
	}
	
	public static boolean getsentchallenges(String user_id) {
		boolean result = false;

		String geturl = url + "getSentChallengesByUserId.php?user_id="
				+ user_id;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<ChallenegePojo> challengelist = new ArrayList<ChallenegePojo>();
				System.out.println("user_id---" + user_id);
				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

					JSONArray jarray = c.getJSONArray("gamedetails");

					JSONObject oarray = null;

					for (int i = 0; i < jarray.length(); i++)

					{
						ChallenegePojo opp = new ChallenegePojo();

						oarray = jarray.getJSONObject(i);

						String gameid = oarray.getString("gameid");
						String catid = oarray.getString("catid");
						String category_name = oarray
								.getString("category_name");

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");

						opp.setUserid(ouserid);
						opp.setProfilepic(oprofilepic);
						opp.setFirstname(ofirstname);
						opp.setLastname(olastname);
						opp.setCountry(ocountry);
						opp.setGameid(gameid);
						opp.setCategory_name(category_name);
						opp.setCatid(catid);

						challengelist.add(opp);
					}

					DataManager.challenegelist = challengelist;
					DataManager.status = "1";
				} catch (JSONException e) {
					result = true;
					DataManager.status = "0";
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

		return result;
	}

	public static boolean getallusers(String user_id) {
		boolean result = false;

		String geturl = url + "getallusers.php?userid=" + user_id;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, 4000);
		HttpConnectionParams.setSoTimeout(param, 10000);

		HttpClient client = new DefaultHttpClient(param);
		// Send Httpget request
		HttpGet get = new HttpGet(geturl);

		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<OpponentUser> opponentuser = new ArrayList<OpponentUser>();

				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

					JSONArray opponentarray = c.getJSONArray("users");

					JSONObject oarray = null;

					for (int i = 0; i < opponentarray.length(); i++)

					{
						OpponentUser opp = new OpponentUser();

						oarray = opponentarray.getJSONObject(i);

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String logintype = oarray.getString("logintype");
						String oppxp = oarray.getString("xp");

						opp.setOppuserid(ouserid);
						opp.setOppcountry(ocountry);
						opp.setOppfname(ofirstname);
						opp.setOpplname(olastname);
						opp.setOppprofilepic(oprofilepic);
						opp.setOppxp(oppxp);
						opp.setOpplogintype(logintype);

						opponentuser.add(opp);
					}

					DataManager.opponenetuser = opponentuser;

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

		return result;
	}
	
	public static boolean searchfriend(Context context, String myuserid,
			String search) {

		
		boolean result = false;
		String url = DataManager.url + "searchuser.php?userid=" + myuserid
				+ "&search=" + search;
		url = url.replaceAll(" ", "%20");
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		
		try {

			HttpResponse responsePOST;
			String response = null;

			responsePOST = client.execute(get);

			HttpEntity resEntity = responsePOST.getEntity();

			if (resEntity != null) {

				// get response
				response = EntityUtils.toString(resEntity);
				result = true;
				ArrayList<OpponentUser> opponentuser = new ArrayList<OpponentUser>();

				System.out.println("response---" + response);
				try {

					JSONObject c = new JSONObject(response);

					JSONArray opponentarray = c.getJSONArray("users");

					JSONObject oarray = null;

					for (int i = 0; i < opponentarray.length(); i++)

					{
						OpponentUser opp = new OpponentUser();

						oarray = opponentarray.getJSONObject(i);

						String ouserid = oarray.getString("userid");
						String ofirstname = oarray.getString("firstname");
						String olastname = oarray.getString("lastname");
						String oprofilepic = oarray.getString("profilepic");
						String ocountry = oarray.getString("country");
						String logintype = oarray.getString("logintype");
						String oppxp = oarray.getString("xp");

						opp.setOppuserid(ouserid);
						opp.setOppcountry(ocountry);
						opp.setOppfname(ofirstname);
						opp.setOpplname(olastname);
						opp.setOppprofilepic(oprofilepic);
						opp.setOppxp(oppxp);
						opp.setOpplogintype(logintype);

						opponentuser.add(opp);
					}

					DataManager.opponenetuser = opponentuser;
					DataManager.status = "1"; 
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

		return result;
	}
}
