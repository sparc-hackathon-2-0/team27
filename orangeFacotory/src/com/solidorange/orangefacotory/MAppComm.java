package com.solidorange.orangefacotory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;

import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Application;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Handles the communication requirements of the FMObile application.
 */
public class MAppComm extends Application{
	static String tag = MAppComm.class.getName();
	private String uid;
	private static String appServerURL;
	private static String simID;
	private static int pollDelay;
	private String firstScreen;
	private static String msgURL;
	private int vibrDuration;
	private int msgRemindInterval;
	@SuppressWarnings("unused")
	private int getTimeout;
	private String logStr;
	private int logFileSize;
	private int logLevel;
	public int resetApp;
	@SuppressWarnings("unused")
	private boolean configset;
	public static String connectionStatus="";
	public String URL="";
	public static int iterator=0;
	public static HashMap<String, String> selections;
	public static ArrayList<HashMap<String, String>> commlist;
	public static String msg1=null;
	
	
	public HashMap<String, String> commands;
	
	public String title;
	
	@Override
    public void onCreate() {

		uid = "NOTFOUND";
	    //dbInstance = "d7iprd";
	    //appServerURL = "http://fmo2.clemson.edu/dev/fmobile";
	    appServerURL = "http://fmo2.clemson.edu/dev/fmobile";
	    //appServerURL = "http://fmo2.clemson.edu/dev/fmobile";
	    //appServerURL = "http://www.clemson.edu/facilities/support/dev";
	    //simID = "5959C0128883A1B2677492A23DCB84B5"; test version
	    //simID = "00080000635030"; //From emulator // UID = RARFLIN
	    //simID=getAccountID();
	    simID="A000002C6527E3";//A100000A660992
	    vibrDuration = 500;
	    pollDelay=60000;
	    firstScreen = "/fmobile.php";
	    msgURL = "/getmsg.php";
	    msgRemindInterval = 60000;
	    getTimeout = 20000;
	    logFileSize = 20000;
	    logLevel = 10;
	    title=null;
	    configset=false;
	    selections= new HashMap<String, String>();
	    commlist= new ArrayList<HashMap<String, String>>();
	    
        super.onCreate();
    }
 
/**
 * @return SIMID of the device
 */
  public static String simID(){
    return simID;
  }
  public String UID(){
	    return uid;
	  }
/**
 * @return URL of the application server being used
 */
  public static String appServURL() {
    return appServerURL;
  }

/**
 * @return true if the SIMID of the device is associated with a user;
 *         false otherwise
 */
  public boolean simidAssociated() {
	  if(uid.equals("NOTFOUND")){return false;}
	  else return true;
		
	  
  }

/**
 * @return the delay in ms between polling requests
 */
  public static int getDelay() {
    return pollDelay;
  }

/**
 * @return the filename that generates the first screen for the 
 *         FMObile application
 */
  public String getFirstScreen() {
    return firstScreen;
  }

/**
 * @return duration of alert vibration in ms
 */
  public int getVibrDuration() {
    return vibrDuration;
  }

/**
 * @return the URL that is polled for messages
 */
  public static String getMsgURL() {
    return msgURL;
  }

/**
 * @return interval in ms between reminder prompts that a message is waiting
 */
  public int getMsgRemindInterval() {
    return msgRemindInterval;
  }
  /**
   *  @return the full text of the application log
   */
    public String getLog() {
      return logStr;
    }

  /**
   * Writes generic entry to log.  Log is maintained at a maximum
   * number of characters as defined in the configuration.
   * Logged text is also written to the console for
   * use in development.  It is assumed that this goes to 
   * /dev/null on the Motorola i355.
   *
   * @param toLog the string of text to add to the log
   */
    public void log(String toLog) {
      String toAdd = System.currentTimeMillis() + " - " + toLog + "\n";
      logStr = toAdd+logStr;
      System.out.print(toAdd);
      clearLog(logFileSize);
    }

  /**
   * Writes emtry to log including information about the supplied Object.
   * 
   * @see #log(String)
   * @param obj Object about which to record information
   * @param toLog text to add to log
   */
    public void log(Object obj,String toLog) {
      log(obj.getClass().getName()+": "+obj.toString()+": "+toLog);
    }

  /**
   * Writes emtry to log including information about the supplied Object and
   * supplied Exception.
   * 
   * @see #log(String)
   * @see #log(Object,String)
   * @param obj Object about which to record information
   * @param toLog text to add to log
   * @param e Exception about which to record information
   */
    public void log(Object obj,String toLog,Exception e) {
      log(obj,toLog+": "+e.toString()+" "+e.getMessage());
    }

  /**
   * Writes emtry to log including information about the supplied Object and
   * supplied Exception subject to the supplied logging level.  If the 
   * logging level set in configuration >= level, the log entry is made.  
   * Otherwise, the log entry is discarded.
   * 
   * @see #log(String)
   * @see #log(Object,String)
   * @see #log(Object,String,Exception)
   * @param obj Object about which to record information
   * @param toLog text to add to log
   * @param e Exception about which to record information
   * @param level priority level of log message
   */
    public void log(Object obj,String toLog,Exception e,int level) {
      if (level <= logLevel) {
        log(obj,toLog,e);
      }
    }

  /**
   * Completely clears log.
   */
    public void clearLog() {
      logStr = "";
    }

  /**
   * Clears oldest part of log such that the total length of the log
   * is <= supplied length.
   *
   * @param newestChars maximum allowed length of log
   * @see #clearLog
   */
    public void clearLog(int newestChars) {
      if (logStr.length() > newestChars) {
        logStr = logStr.substring(0,newestChars);
      }
    }
/**
 * Gets configuration data from application server.  The following parameters
 * are retrieved.
 * <p><b>polldelay</b> polling delay
 * <p><b>firstscreen</b> first screen presented in user interface
 * <p><b>vibrduration</b> duration of alert vibration in ms
 * <p><b>msgurl</b> URL which is polled for incoming messages
 * <p><b>msgremindinterval</b> interval in ms between reminder alerts
 * <p><b>gettimeout</b> time in ms a GET request is allowed to process
 * <p><b>uid</b> userid associated with the running app
 * <p><b>logfilesize</b> the maximum size in chars or the log file
 * <p><b>loglevel</b> the minimum priority level of messages to be logged
 * <p>The config file is evaluated for each of these.  If any are not present, 
 * the default values for <b>all</b> are used instead.
 */
  public void getConfig() {
	
        Log.v(tag,"Attempting to get Config");
        String url = appServerURL+"/config.php?simid="+simID;
        Log.d(tag,"With URL: "+url);
        String xml= XMLfunctions.getXML(url);
        if(!connectionStatus.equals(""))
        {
        	Log.e(tag,"connectionStatus: "+connectionStatus);
        }
        else
        {
	                
	//        try {
	//			xml=readFileAsString("/mnt/sdcard/FMobile2/Cache/config.xml");
	//		} catch (IOException e1) {
	//			// TODO Auto-generated catch block
	//			e1.printStackTrace();
	//		}
	        Log.d(tag,"Succesfull call to Get XML");
	        Log.d(tag,"Retieved :: "+xml);
	    
	        Document doc = XMLfunctions.XMLfromString(xml);
	        Log.d(tag,"Succesfull call to XMLfromstring");
	        if(doc==null){
	        	throw new EmptyStackException();  
	        }        
	        Log.d(tag,"Document is Not Null");
	
			NodeList nodes = doc.getElementsByTagName("config");
			Log.d(tag,"Probing listitem from document");
			Element e = (Element)nodes.item(0);
			pollDelay=Integer.parseInt(XMLfunctions.getValue(e, "polldelay"));
	        firstScreen=XMLfunctions.getValue(e, "firstscreen");
	        vibrDuration = Integer.parseInt(XMLfunctions.getValue(e, "vibrduration"));
	        msgURL=XMLfunctions.getValue(e, "msgurl");
	        msgRemindInterval = Integer.parseInt(XMLfunctions.getValue(e, "msgremindinterval"));
	        getTimeout = Integer.parseInt(XMLfunctions.getValue(e, "gettimeout"));
	        uid=XMLfunctions.getValue(e, "uid");
	        Log.w(tag,uid);
	        logFileSize = Integer.parseInt(XMLfunctions.getValue(e, "logfilesize"));
	        logLevel = Integer.parseInt(XMLfunctions.getValue(e, "loglevel"));
			Log.v(tag,"Successfully Mapped into myList the listitems");
			Log.e(tag,"Caching Config file: ");
			resetApp=1;
			//configset=DownloadFromUrl(url,"config.xml");
	    //}
	    //else
	    //{
						
		//}
        }

  }
  public boolean DownloadFromUrl(String DownloadUrl, String fileName) 
  {
	  try 
	  {
		 URL url = new URL(DownloadUrl); //you can write here any link
		 long startTime = System.currentTimeMillis();
		 Log.d("DownloadManager", "download begining");
		 Log.i("DownloadManager", "Saving in /mnt/sdcard/FMobile/Cache/");
		 Log.d("DownloadManager", "download url:" + url);
		 Log.d("DownloadManager", "downloaded file name:" + fileName);
		 /* Open a connection to that URL. */
		 URLConnection ucon = url.openConnection();
		 ucon.setConnectTimeout(4000);
		 ucon.setReadTimeout(4000);
		 /*
		 * Define InputStreams to read from the URLConnection.
		 */
		 InputStream is = ucon.getInputStream();
		 BufferedInputStream bis = new BufferedInputStream(is);
		 /*
		 * Read bytes to the Buffer until there is nothing more to read(-1).
		 */
		 ByteArrayBuffer baf = new ByteArrayBuffer(5000);
		 int current = 0;
		 while ((current = bis.read()) != -1) 
		 {
			 baf.append((byte) current);
		 }
		 /* FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
		 fos.write(baf.toByteArray());
		 fos.flush();
		 fos.close();*/
		 
		 File dir = new File ("/mnt/sdcard/FMobile2/Cache/");
		 
		 if(dir.exists()==false) 
			 dir.mkdirs();
		 File file = new File(dir, fileName);
		 //Convert the Bytes read to a String.
		 FileOutputStream fos = new FileOutputStream(file);
		 fos.write(baf.toByteArray());
		 fos.flush();
		 fos.close();
		 
		 Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
		 Log.d("DownloadManager", "downloaded file name:" + fileName);
		 return true;
		 } 
	  catch (IOException e) 
	  {
		  Log.d("DownloadManager", "Error: " + e);
		  return false;
	  }
	
  }

  public static String readFileAsString(String filePath) throws java.io.IOException
  {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line;
      String results="";
      line = reader.readLine();
      while(line != null)
      {
          results += line;
          line = reader.readLine();
      }
      reader.close();
      return results;
  }
  public String getAccountID()
  {
	  /*String possibleEmail="";
	  Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
	  Account[] accounts = AccountManager.get(this).getAccounts();
	  
	  for (Account account : accounts) 
	  {
		  if (emailPattern.matcher(account.name).matches()) 
		  {
          possibleEmail =AccountManager.KEY_ACCOUNT_NAME;// account.name;
          Log.d(tag,""+possibleEmail);
          possibleEmail =account.name+"Tomato";
          Log.d(tag,""+possibleEmail);
		  }
	  }
	  return possibleEmail;
	  */
//	  Build ver = new Build();
//      Secure ver2=new Secure();
//      Log.i(tag,""+ver2.ANDROID_ID);
      TelephonyManager mTelephonyMgr =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//      String x= mTelephonyMgr.getSubscriberId();
//      Log.i(tag,""+x);
      String imei = mTelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
      Log.i(tag,""+imei);
      return imei;
//      String phoneNumber=mTelephonyMgr.getLine1Number(); // Requires READ_PHONE_STATE
//      Log.i(tag,""+phoneNumber);
//      String softwareVer = mTelephonyMgr.getDeviceSoftwareVersion(); // Requires READ_PHONE_STATE
//      Log.i(tag,""+softwareVer);
//      Log.i(tag,""+ver.BOARD);
      
  }
  
  public static void setCommlist(ArrayList<HashMap<String, String>> list){
	  commlist=list;
	  int x=commlist.size();
	  Log.d(tag,"Size of incoming List: "+x);
  }
  public ArrayList<HashMap<String, String>> getCommlist()
  {
	  return commlist;
  }
  public void clearcommlist()
  {
	  commlist.clear();
  }
  
  public String getMsg1()
  {
	  return msg1;
  }
  
  public static void setMsg1(String msg)
  {
	  msg1=msg;
	  Log.e(tag, "value of msg1: "+msg1);
  }
  
  public static void clearComm()
  {
	  setMsg1(null);
	  commlist.clear();
	  selections.clear();  
  }

 
  


}

