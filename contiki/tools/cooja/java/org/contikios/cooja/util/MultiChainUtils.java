package org.contikios.cooja.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import multichain.command.MultiChainCommand;
import multichain.command.MultichainException;
import multichain.object.StreamKeyItem;
import multichain.object.queryobjects.RawParam;

public class MultiChainUtils {
	private static MultiChainCommand createMultiChainCommand() {
		return new MultiChainCommand(
				"localhost",
				"7338",
				"multichainrpc",
				"2qrYeu84tpwFSUYaVHS8C1TyVvHhwepawtTsLTEYYkTC");
	}
	
	private static Logger createMeasureLogger(String addr) {
		Logger MeasureLOGGER = Logger.getLogger("Measure0." + addr.toString());
        MeasureLOGGER.setLevel(Level.parse("FINEST"));
        try {
            FileHandler fh;
            File dir = new File("logs");
            dir.mkdir();
            fh = new FileHandler("logs/Measures0." + addr + ".log");
            fh.setFormatter(new SimpleFormatter());
            MeasureLOGGER.addHandler(fh);
            MeasureLOGGER.setUseParentHandlers(false);
        } catch (IOException | SecurityException ex) {
        }
        return MeasureLOGGER;
	}
	
	private static String getAddressFrom(MultiChainCommand multiChainCommand) throws MultichainException {
		List<String> addressFromList = multiChainCommand.getAddressCommand().getAddresses();
		return addressFromList.get(addressFromList.size() - 1);
	}
	
	private static String getPrivateKey(MultiChainCommand multiChainCommand, String addressFrom) throws MultichainException {
		return multiChainCommand.getKeyCommand().getPrivkey(addressFrom).toString();
	}
		
	public static void writeCertificateToStream(String streamName, String id, String key, LocalDateTime validTo) {
		try {
			MultiChainCommand multiChainCommand = createMultiChainCommand();
			String addressFrom = getAddressFrom(multiChainCommand);
			String privateKey = getPrivateKey(multiChainCommand, addressFrom);
			
			JsonCertificate json = new JsonCertificate(id, key, validTo);

			Gson gson = new Gson();
			String signedMsg = multiChainCommand.getMessagingCommand().signMessage(privateKey, gson.toJson(json));
			String keys = id + "\n" + signedMsg;
			
			MultipartUtils multipart = new MultipartUtils("http://localhost/?chain=default&page=publish", "UTF-8");
            multipart.addFormField("from", addressFrom);
            multipart.addFormField("name", streamName);
            multipart.addFormField("key", keys);
            multipart.addFormField("json", gson.toJson(json));
            multipart.addFormField("publish", "Publish Item");
            
            Integer sentBytes = addressFrom.getBytes().length + streamName.getBytes().length + keys.getBytes().length + gson.toJson(json).getBytes().length + "Publish Item".getBytes().length;
            Logger MeasureLOGGER = createMeasureLogger(id);
            MeasureLOGGER.log(Level.FINEST,
                    "{0}|{1}|{2}|{3}",
                    new Object[]{
                        "Key submission sent bytes",
                		id,
                        sentBytes,
                        new Date().toString()
                    });
 
            List<String> response = multipart.finish();
             
            //System.out.println("SERVER REPLIED:");
             
            //for (String line : response) {
            //    System.out.println(line);
            //}
		} catch (MultichainException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeExperienceToStream(String streamName, String id, int value, String token, String signature) {
		try {
			MultiChainCommand multiChainCommand = createMultiChainCommand();
			String addressFrom = getAddressFrom(multiChainCommand);
			String privateKey = getPrivateKey(multiChainCommand, addressFrom);
			
			JsonExperience json = new JsonExperience(id, value, token, signature);

			Gson gson = new Gson();
			String signedMsg = multiChainCommand.getMessagingCommand().signMessage(privateKey, gson.toJson(json));
			String keys = id + "\n" + signedMsg;
			
			MultipartUtils multipart = new MultipartUtils("http://localhost/?chain=default&page=publish", "UTF-8");
            multipart.addFormField("from", addressFrom);
            multipart.addFormField("name", streamName);
            multipart.addFormField("key", keys);
            multipart.addFormField("json", gson.toJson(json));
            multipart.addFormField("publish", "Publish Item");
            
            Integer sentBytes = addressFrom.getBytes().length + streamName.getBytes().length + keys.getBytes().length + gson.toJson(json).getBytes().length + "Publish Item".getBytes().length;
            Logger MeasureLOGGER = createMeasureLogger(id);
            MeasureLOGGER.log(Level.FINEST,
                    "{0}|{1}|{2}|{3}",
                    new Object[]{
                        "Feedback submission sent bytes",
                		id,
                        sentBytes,
                        new Date().toString()
                    });
 
            List<String> response = multipart.finish();
             
            //System.out.println("SERVER REPLIED:");
             
            //for (String line : response) {
            //    System.out.println(line);
            //}
		} catch (MultichainException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getCertificateFromStream(String streamName, String key) {
		try {
			String url = "http://localhost/streamkeycertificate.php?chain=default&stream=" + streamName + "&key=" + key;
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();
			
			Integer receivedBytes = response.length() * 8;
            Logger MeasureLOGGER = createMeasureLogger(key);
            MeasureLOGGER.log(Level.FINEST,
                    "{0}|{1}|{2}|{3}",
                    new Object[]{
                        "Key retrieval received bytes",
                		key,
                		receivedBytes,
                		new Date().toString()
                    });
	
			//print result
			//System.out.println(response.toString());
			return response.toString();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static float getTrustIndexFromStream(String streamName, String key) {
		try {
			String url = "http://localhost/streamkeytrustindex.php?chain=default&stream=" + streamName + "&key=" + key;
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();
			
			Integer receivedBytes = response.length() * 8;
            Logger MeasureLOGGER = createMeasureLogger(key);
            MeasureLOGGER.log(Level.FINEST,
                    "{0}|{1}|{2}|{3}",
                    new Object[]{
                        "Trust index retrieval received bytes",
                		key,
                		receivedBytes,
                		new Date().toString()
                    });
	
			//print result
			//System.out.println(response.toString());
			return Float.valueOf(response.toString());
		} catch(Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	private static class JsonCertificate {
		private String id;
		private String key;
		private String validTo;
		private String timestamp;
		
		public JsonCertificate(String id, String key, LocalDateTime validTo) {
			this.id = id;
			this.key = key;
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			this.validTo = dateTimeFormatter.format(validTo);
			LocalDateTime now = LocalDateTime.now();
			this.timestamp = dateTimeFormatter.format(now);
		}
	}
	
	private static class JsonExperience {
		private String id;
		private int value;
		private String token;
		private String signature;
		private String timestamp;
		
		public JsonExperience(String id, int value, String token, String signature) {
			this.id = id;
			this.value = value;
			this.token = token;
			this.signature = signature;
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			this.timestamp = dateTimeFormatter.format(now);
		}
	}
}
