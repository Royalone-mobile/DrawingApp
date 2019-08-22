package com.songu.shadow.drawing.service;


import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.widget.Toast;

import com.ipmacro.utils.aes.AESUtil;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.MessageModel;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(Build.VERSION_CODES.FROYO) public class HttpPosting {  //Upload File
  public boolean isRunning = false;
  public int l_m;
  public TimerTask task;
  public  Timer mTimer;
  public PostClass posting;
  //public ActivityChat activity;
  public MessageModel msg;
  public String file;
    public String ff;

  /*public HttpPosting(ActivityChat activity,String filename, MessageModel msg)
  {
	  this.activity = activity;
      this.msg = msg;
      this.file = filename;

  }*/
 public void postHttp()  //Execute Upload
  {
	  isRunning = true;        
      
      posting =  new PostClass();         
      posting.execute("");
  }

 	
  public class PostClass extends AsyncTask<String, Void, Boolean> {
    String responseString = "";
    boolean recording = false;
    @Override
    protected void onPreExecute() {

        super.onPreExecute();

    }
      public int uploadFile(String sourceFileUri) {
          int serverResponseCode = 0;
          String serverUrl = Globals.m_baseUrl + "fileupload.php";
          String fileName = sourceFileUri;

          HttpURLConnection conn = null;
          DataOutputStream dos = null;
          String lineEnd = "\r\n";
          String twoHyphens = "--";
          String boundary = "*****";
          int bytesRead, bytesAvailable, bufferSize;
          byte[] buffer;
          int maxBufferSize = 1 * 1024 * 1024;
          File sourceFile = new File(sourceFileUri);

          if (!sourceFile.isFile()) {

              //Toast.makeText(activity, "File does not exist", Toast.LENGTH_SHORT).show();
              return 0;
          }
          else
          {
              try {

                  String filenameArray[] = sourceFileUri.split("\\.");
                  String extension = filenameArray[filenameArray.length-1];
                  ff =  "." +extension;
                  // open a URL connection to the Servlet
                  //fileName.split("\\.");
                  // open a URL connection to the Servlet
                  FileInputStream fileInputStream = new FileInputStream(sourceFile);
                  URL url = new URL(serverUrl);

                  // Open a HTTP  connection to  the URL
                  conn = (HttpURLConnection) url.openConnection();
                  conn.setDoInput(true); // Allow Inputs
                  conn.setDoOutput(true); // Allow Outputs
                  conn.setUseCaches(false); // Don't use a Cached Copy
                  conn.setRequestMethod("POST");
                  conn.setRequestProperty("Connection", "Keep-Alive");
                  conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                  conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                  conn.setRequestProperty("uploaded_file", fileName);
                  dos = new DataOutputStream(conn.getOutputStream());
                  dos.writeBytes(twoHyphens + boundary + lineEnd);
                  dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                          + fileName + "\"" + lineEnd);

                  dos.writeBytes(lineEnd);

                  // create a buffer of  maximum size
                  bytesAvailable = fileInputStream.available();

                  bufferSize = Math.min(bytesAvailable, maxBufferSize);
                  buffer = new byte[bufferSize];

                  // read file and write it into form...
                  bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                  while (bytesRead > 0) {

                      dos.write(buffer, 0, bufferSize);
                      bytesAvailable = fileInputStream.available();
                      bufferSize = Math.min(bytesAvailable, maxBufferSize);
                      bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                  }

                  // send multipart form data necesssary after file data...
                  dos.writeBytes(lineEnd);
                  dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                  // Responses from the server (code and message)
                  serverResponseCode = conn.getResponseCode();
                  String serverResponseMessage = conn.getResponseMessage();


                  if(serverResponseCode == 200){

                  }

                  //close the streams //
                  fileInputStream.close();
                  dos.flush();
                  dos.close();

              } catch (MalformedURLException ex) {

                  ex.printStackTrace();
              } catch (Exception e) {


              }
              return serverResponseCode;

          } // End else block
      }
    @Override
    protected Boolean doInBackground(String... urls) {
        uploadFile(file);
        return true;
    }
    @Override
    protected void onPostExecute(Boolean isSuccess) {
        if(isSuccess){
            String ss = "";
            try
            {
                //ss = URLDecoder.decode(msg.content,"UTF8");
                //ss = AESUtil.decrypt(ss);
                ss = URLDecoder.decode(msg.content, "UTF-8");
                byte[] arrSS = Base64.decode(ss, Base64.DEFAULT);
                String strSS = new String(arrSS, "UTF-8");
                ss = AESUtil.decrypt(strSS);
            }
            catch(Exception e)
            {

            }
            msg.attach = Globals.m_baseUrl + "uploads/"+ss;
            //Globals.m_service.onSendMessage(msg,activity,0);
        }
        else{
            //Toast.makeText(activity, "Connection failed", Toast.LENGTH_SHORT);
        }
    }
}
}
