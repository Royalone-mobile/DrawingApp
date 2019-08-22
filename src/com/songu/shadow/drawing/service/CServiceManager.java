package com.songu.shadow.drawing.service;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.songu.shadow.drawing.ActivityChat;
import com.songu.shadow.drawing.ActivitySignIn;
import com.songu.shadow.drawing.ActivitySignUp;
import com.songu.shadow.drawing.MainActivity;
import com.songu.shadow.drawing.SplashScreenActivity;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.fragments.ChatContactFragment;
import com.songu.shadow.drawing.fragments.ChatFragment;
import com.songu.shadow.drawing.fragments.FriendFragment;
import com.songu.shadow.drawing.model.MessageModel;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.utils.HttpUtil;

/**
 * Created by Administrator on 3/11/2015.
 */
public class CServiceManager {
    private Context con;
    public CServiceManager(Context con)
    {
        this.con = con;
    }


    
    /*public void deleteUser(int from,int uid,final ActivityContactList con)
    {
        String url = Globals.m_baseUrl + "deleteuser.php?uid=" + String.valueOf(uid) +"&fromid=" + String.valueOf(from);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response"))
                    {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            con.updateList();
                            //ActivityChat.onSuccessRead(activity, paramString);
                        } else if (response == 400) {
                            //ActivityChat.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }*/
    public void onReadMessage(int target,final ActivityChat activity,final int memberId)
    {
        String url = Globals.m_baseUrl + "readmessage.php?src=" + String.valueOf(target) +"&dest=" + String.valueOf(Globals.mAccount.id);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response"))
                    {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                        	activity.onSuccessRead(activity, paramString,memberId);
                        } else if (response == 400) {
                        	activity.onFail(activity,memberId);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /*
    public void addMember(int gid,int uid)
    {
        String url = Globals.m_baseUrl + "addmember.php?gid=" + String.valueOf(gid) +"&uid=" + String.valueOf(uid);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            //ActivityChat.onSuccessSend();
                        } else if (response == 400) {
                            //ActivityChat.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void deleteMember(int gid, int uid)
    {
        String url = Globals.m_baseUrl + "deletemember.php?gid=" + String.valueOf(gid) +"&uid=" + String.valueOf(uid);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            //ActivityChat.onSuccessSend();
                        } else if (response == 400) {
                            //ActivityChat.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    public void onSendMessage(final MessageModel msg,final ActivityChat activity,final int memberIndex)
    {

        String url = Globals.m_baseUrl + "sendmessage.php?src=" + String.valueOf(msg.fromId) +"&dest=" + String.valueOf(msg.toId) +"&message="+msg.content+"&time="+msg.timeStamp+"&attach="+msg.isFile+"&fname="+msg.attach;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            activity.onSuccessSend(msg,memberIndex);
                        } else if (response == 400) {
                            //ActivityChat.onFail(activity,memberIndex);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

   /* public void onDeclineReminder(int rid,final ActivityContactList con)
    {
        String url = Globals.m_baseUrl + "declinereminder.php?rid=" + String.valueOf(rid);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            con.updateList();

                        } else if (response == 400) {
                            //ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    /*public void onUpdateReminder(int rid,final ActivityContactList con)
    {
        String url = Globals.m_baseUrl + "checkedreminder.php?rid=" + String.valueOf(rid);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            //con.updateList();
                        } else if (response == 400) {
                            //ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    /*public void onAcceptReminder(int rid,final ActivityContactList con)
    {
        String url = Globals.m_baseUrl + "acceptreminder.php?rid=" + String.valueOf(rid);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            con.updateList();
                        } else if (response == 400) {
                            //ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    /*public  void onRegisterReminder(ReminderModel model, final ActivityAddReminder con)
    {
        String url = Globals.m_baseUrl + "reminderadd.php?gid=" + String.valueOf(model.gid) +"&mid=" + String.valueOf(Globals.mAccount.id) +
                "&title=" + model.title + "&purpose=" + model.purpose + "&timestamp="+model.time + "&location="+model.location;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            con.addReminder(paramString);

                        } else if (response == 400) {
                            //ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    public  void onRegisterContact(int from, int to, final FriendFragment con)
    {
        String url = Globals.m_baseUrl + "contactadd.php?from=" + String.valueOf(from) +"&to=" + String.valueOf(to);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            //ActivityContactList.onSuccessGroup(paramString, activity);
                            con.updateList();

                        } else if (response == 400) {
                            //ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /*public void onLoadGroupList (final ActivityContactList activity)
    {
        String url = Globals.m_baseUrl + "grouplist.php?user=" + Globals.mAccount.mName +"&password=" + Globals.mAccount.mPassword;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            ActivityContactList.onSuccessGroup(paramString, activity);

                        } else if (response == 400) {
                            ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    /*public void onLoadReminderList(final ActivityContactList activity,String time) //Load All Event from DB
    {
        String url = Globals.m_baseUrl + "reminderlist.php?user=" + String.valueOf(Globals.mAccount.id) +"&time=" + time;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            activity.onParseReminder(paramString);

                        } else if (response == 400) {
                            ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    public void onLoadContactList(final FriendFragment activity,final ChatContactFragment activity1)
    {
        String url = Globals.m_baseUrl + "contactlist.php?user=" + Globals.mAccount.mName +"&password=" + Globals.mAccount.mPassword;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                        	if (activity != null)
                        		activity.onSuccess(paramString);
                        	else if (activity1 != null)
                        		activity1.onSuccess(paramString);

                        } else if (response == 400) {
                            //ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void onSearchIdResult(final FriendFragment activity,String pattern)
    {
        String url = Globals.m_baseUrl + "searchid.php?user=" + pattern+"&id=" + String.valueOf(Globals.mAccount.id);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
                activity.mHandler.sendEmptyMessageDelayed(2,300);

            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            //ActivityContactList.onSuccess(paramString,activity);
                            activity.mHandler.sendEmptyMessageDelayed(2,300);
                            activity.parseSearchResult(paramString);

                        } else if (response == 400) {
                            //ActivitySignUp.onFail();
                            activity.mHandler.sendEmptyMessageDelayed(2,300);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onProfileImageSave(UserProfile profile,final ActivitySignUp activity)
    {
        if (profile.mImage.equals(""))
        {
            onRegister(profile,activity);
        }
        else
        {
            PostImage m = new PostImage(profile,activity);
            m.postHttp();
        }
    }
    
    public void onRegister(UserProfile profile,final ActivitySignUp activity)
    {
        String url = Globals.m_baseUrl + "useradd.php?user=" + profile.mName
                +"&password="+ profile.mPassword+"&image="+profile.mImage
                +"&fname=" + profile.mFirstName + "&lname=" + profile.mLastName
                +"&email="+profile.mEmail + "&mobile=" + profile.mMobile
                +"&country="+profile.mCountry + "&birth="+profile.mBirthday + "&gender="+profile.mGender;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            ActivitySignUp.onSuccess(paramString,activity);

                        } else if (response == 400) {
                            ActivitySignUp.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void onLogin(String user, String password,final ActivitySignIn activity,final SplashScreenActivity splash)
    {
        String url = Globals.m_baseUrl + "login.php?user=" + user +"&password=" + password;  //url
        HttpUtil.get(url, new AsyncHttpResponseHandler() {  // requst for url with user and password
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {  //that is return when success..
                // param String has JSon String......

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {  //Success Code  200
                        	if (activity != null)
                        		ActivitySignIn.onSuccess(paramString,activity);  //Parse Json String....
                        	else 
                        		splash.goNext(paramString);

                        } else if (response == 400) {  //Fail Code 4000
                        	if (activity != null)
                        		ActivitySignIn.onFail();  //Show Error text
                        	else 
                        	{
                        		Intent m = new Intent(splash,ActivitySignIn.class);
                        		splash.startActivity(m);
                        		splash.finish();
                        	}
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static void getCityName(final SplashScreenActivity activity)
	{		
        HttpUtil.get(Globals.cityUrl, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
            	Globals.g_cityName = "";
            	activity.login();
            }

            public void onFinish() {
                
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("status")) {
                        String  response = localJSONObject1.getString("status");
                        if (response.equals("success")) {
                        	Globals.g_cityName = localJSONObject1.getString("city");
                        	activity.login();
                        } 
                        else 
                        {
                        	

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}
    
    
    /*public void onSetNewPassWord(String user,String pass,final ActivityForget activity)
    {
        String url = Globals.m_baseUrl + "resetpassword.php?user=" + user +"&pass=" + pass;
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            if (activity != null) {
                                Intent m = new Intent(activity, ActivityContactList.class);
                                activity.startActivity(m);
                            }
                        } else if (response == 400) {
                            ActivityForget.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }*/
    /*public void onGetVerificationCode(String user, String email,final ActivityForget activity)
    {
        String url = Globals.m_baseUrl + "verify.php?user=" + user +"&email=" + email;  //Url
        HttpUtil.get(url, new AsyncHttpResponseHandler() {  //that means get url with parameter with user and email
            public void onFailure(Throwable paramThrowable) {
                String s = "fail";
            }

            public void onFinish() {
                String s = "finish";
            }

            public void onSuccess(String paramString) {

                try {
                    JSONObject localJSONObject1 = new JSONObject(paramString);
                    if (localJSONObject1.has("response")) {
                        int response = localJSONObject1.getInt("response");
                        if (response == 200) {
                            activity.successVerificationCode(paramString);

                        } else if (response == 400) {
                            ActivitySignIn.onFail();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

}
