package com.github.sdnwiselab.sdnwise.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import multichain.command.MultiChainCommand;
import multichain.command.MultichainException;
import multichain.object.StreamKeyItem;

public class MultiChainUtils {
	private static MultiChainCommand createMultiChainCommand() {
		return new MultiChainCommand(
				"localhost",
				"7338",
				"multichainrpc",
				"2qrYeu84tpwFSUYaVHS8C1TyVvHhwepawtTsLTEYYkTC");
	}
	
	public static String getFromStream(String streamName, String key) {
		try {
			String url = "http://localhost/streamkeyitem.php?chain=default&stream=" + streamName + "&key=" + key;
			
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
	
			//print result
			//System.out.println(response.toString());
			return response.toString();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}