package sig.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.modules.ChatLog.ChatLogMessage;

public class FileUtils {
	public static String[] readFromFile(String filename) {
		File file = new File(filename);
		//System.out.println(file.getAbsolutePath());
		List<String> contents= new ArrayList<String>();
		if (file.exists()) {
			try(
					FileReader fw = new FileReader(filename);
				    BufferedReader bw = new BufferedReader(fw);)
				{
					String readline = bw.readLine();
					do {
						if (readline!=null) {
							//System.out.println(readline);
							contents.add(readline);
							readline = bw.readLine();
						}} while (readline!=null);
					fw.close();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return contents.toArray(new String[contents.size()]);
	}
	
	  private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	
	  private static String readFilter(Reader rd, HashMap<Long,String> channel_ids) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    boolean allowed=false;
	    boolean quotation_mark=false;
	    boolean endquotation_mark=false;
	    boolean foundChannel=false;
	    boolean nextBrace=false;
	    boolean outputStuff=false;
	    String numb = "";
	    int braceCount=0;
	    int channelCount=0;
	    int vals=0;
	    int cp;
	    while ((cp = rd.read()) != -1) {
	    	if (braceCount==0) {
	    		allowed=true;
	    	} else 
	    	if (braceCount==1 && !quotation_mark){
	    		quotation_mark=true;
	    		numb="";
	    		allowed=false;
	    	} else
	    	if (!endquotation_mark) {
	    	  if ((char)cp >= '0' &&
	    			  (char)cp <= '9') {
	    		  allowed=false;
	    		  numb+=(char)cp;
	    	  } else {
	    		  allowed=false;
	    		  endquotation_mark=true;
	    		  try {
		    		  if (channel_ids.containsKey(Long.parseLong(numb))) {
		    			  if (channelCount>=1) {
		    				  sb.append(",");
		    			  }
		    			  sb.append("\""+numb+"\"");
		    			  foundChannel=true;
		    			  System.out.println("Found channel "+numb);
		    			  outputStuff=true;
		    		  }
	    		  } catch (NumberFormatException e) {
	    			  
	    		  }
	    	  }
	    	} else 
	    	if (!nextBrace && foundChannel) {
	    		allowed=true;
	    		if ((char)cp == '{') {
	    			nextBrace=true;
	    		}
	    	} else
	    	if (foundChannel) {
	    		allowed=true;
	    		if (braceCount==1) {
	    			allowed=false;
	    			channelCount++;
	    			quotation_mark=false;
	    			endquotation_mark=false;
	    			foundChannel=false;
	    			nextBrace=false;
	    		}
	    	} else {
	    		allowed=false;
	    		if (braceCount==1) {
	    			allowed=false;
	    			quotation_mark=false;
	    			endquotation_mark=false;
	    			foundChannel=false;
	    			nextBrace=false;
	    		}
	    	}
	    	
	    	/*if (outputStuff && vals++<1000) {
	    		System.out.print((char)cp);
	    	}*/
	    	  if ((char)cp == '{') {
	    		  braceCount++;
	    		  //System.out.println("Brace count is "+braceCount+".");
	    	  } else
    		  if ((char)cp == '}') {
	    		  braceCount--;
	    		  //System.out.println("Brace count is "+braceCount+".");
	    	  }
	    	
		      if (allowed) {
		    	  sb.append((char) cp);
		      }
	     }
	    sb.append("}");
	    //System.out.println("=============");
	    //System.out.println(sb.toString());
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrlWithFilter(String url, HashMap<Long,String> filter) throws IOException, JSONException {
	    return readJsonFromUrlWithFilter(url,filter,null,false);
	  }

	  public static JSONObject readJsonFromFileWithFilter(String file, HashMap<Long,String> filter) throws IOException, JSONException {
	    InputStream is = new FileInputStream(new File(file));
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readFilter(rd,filter);
	      JSONObject json = new JSONObject(jsonText);
	      jsonText=null;
	      return json;
	    } finally {
	      is.close();
	    }
	  }

	  public static JSONObject readJsonFromUrlWithFilter(String url, HashMap<Long,String> filter, String file, boolean writeToFile) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readFilter(rd,filter);
	      if (writeToFile) {
	    	  writetoFile(new String[]{jsonText},file);
	      }
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    return readJsonFromUrl(url,null,false);
	  }
	  
	  public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		    return readJsonArrayFromUrl(url,null,false);
		  }

	  public static JSONObject readJsonFromFile(String file) throws IOException, JSONException {
	    InputStream is = new FileInputStream(new File(file));
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      jsonText=null;
	      return json;
	    } finally {
	      is.close();
	    }
	  }

	  public static JSONObject readJsonFromUrl(String url, String file, boolean writeToFile) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      if (writeToFile) {
	    	  writetoFile(new String[]{jsonText},file);
	      }
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }

	  public static JSONArray readJsonArrayFromUrl(String url, String file, boolean writeToFile) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      if (writeToFile) {
	    	  writetoFile(new String[]{jsonText},file);
	      }
	      JSONArray json = new JSONArray(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	  
	  public static void logToFile(String message, String filename) {
		 if ((!filename.equalsIgnoreCase("debug.log") &&
				 !filename.equalsIgnoreCase("debug2.log")) || (filename.equalsIgnoreCase("debug.log") && RabiRaceModule.DEBUGMODE==1)
				 ||
				 (filename.equalsIgnoreCase("debug2.log") && RabiRaceModule.DEBUGMODE==2)
				  ) {
			 logToFile(message,filename,false);  
		 }
	  }
	  public static void logToFile(String message, String filename, boolean outputToChatLog) {
		  File file = new File(filename);
			try {

				if (!file.exists()) {
					file.createNewFile();
				}
				OutputStream out = new FileOutputStream(file,true);
			    Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				PrintWriter pw = new PrintWriter(writer);

				pw.println(message);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (outputToChatLog && sigIRC.chatlogmodule_enabled) {
				ChatLogMessage.importMessages(message);
			}
		}
	  
	  public static void writetoFile(String[] data, String filename) {
		  File file = new File(filename);
			try {

				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file,false);
				PrintWriter pw = new PrintWriter(fw);
				
				for (String s : data) {
					pw.println(s);
				}
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	  }
	  
	  public static void copyFile(File source, File dest) throws IOException {
	    FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
	    try {
	        sourceChannel = new FileInputStream(source).getChannel();
	        destChannel = new FileOutputStream(dest).getChannel();
	        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	       	}finally{
	           sourceChannel.close();
	           destChannel.close();
	       }
	  }
	  
	  public static void deleteFile(String filename) {
		  File file = new File(filename);
		  if (file.exists()) {
			  file.delete();
		  }
	  }
	  

	  public static void downloadFileFromUrl(String url, String file,boolean bearer) throws IOException, JSONException {
		  File filer = new File(file);
		  filer.createNewFile();
		  
		  URL website = new URL(url);
		  HttpURLConnection connection = (HttpURLConnection) website.openConnection();
		    /*for (String s : connection.getHeaderFields().keySet()) {
		    	System.out.println(s+": "+connection.getHeaderFields().get(s));
		    }*/
		    connection.setRequestMethod("GET");
		    //connection.setRequestProperty("Content-Type", "application/json");
		    connection.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
		    connection.setRequestProperty("Authorization", ((bearer)?"Bearer":"OAuth") + " "+sigIRC.oauth.replace("oauth:", ""));
		    if (sigIRC.CLIENTID.length()!=0) {
		    	connection.setRequestProperty("Client-ID", sigIRC.CLIENTID);
		    	//System.out.println("Using "+ sigIRC.oauth+"/"+sigIRC.CLIENTID);
		    }
		    try {
			  ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
			  FileOutputStream fos = new FileOutputStream(file);
			  fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			  fos.close();
		    } catch (ConnectException e) {
		    	System.out.println("Failed to connect, moving on...");
		    }
	  }
	  
	  public static void downloadFileFromUrl(String url, String file) throws IOException, JSONException {
		  downloadFileFromUrl(url,file,false); //Uses OAUTH instead of Bearer
	  }
}
