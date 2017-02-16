package in.strollup.fb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.core.UriBuilder;

import com.poc.vi.main.IVoiceVerifier;
import com.poc.vi.main.VoiceVerifier;

public class RestServiceHelper {
	
//	static WebTarget target = ClientBuilder.newClient().target(getBaseURI());
	
	
//	private static URI getBaseURI() {
//		
//		//  http://localhost:7101/eCareRestApp-Client-context-root/jersey/balance/{param}
//		  return UriBuilder.fromUri("http://localhost:7101/eCareRestApp-Client-context-root/jersey").build();
//	  }
	
	public static void main(String[] args) {
//		String response = target.path("balance").
//	              path("7557772080").
//	              request().
//	              accept(MediaType.TEXT_PLAIN).
//	              get(Response.class)
//	              .toString();
		
//		System.out.println(response);
		
		
	}
	
	public static boolean validateOtac(String uniqueID, String otac)
	{
		
		String response = "";
		try {

			
			String urlString = "http://localhost:7101/eCareRestApp-Client-context-root/jersey/otac/validate/" + uniqueID + "/" + otac;
			 
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			
			
			String output;
			while ((output = br.readLine()) != null) {
				response += output;
			}

			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();


		}
		if (response.contains("true"))
		return true;
		else
			return false;
	}
	public static boolean saveOtac(String uniqueID, String otac)
	{
		
		String response = "";
		try {

			
			String urlString = "http://localhost:7101/eCareRestApp-Client-context-root/jersey/otac/save/" + uniqueID + "/" + otac;
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			
			
			String output;
			while ((output = br.readLine()) != null) {
				response += output;
			}

			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();


		}
		return true;
	}
	
	public static boolean saveUserData(String uniqueId, String key, String value)
	{
		
		String response = "";
		try {

			
			String urlString = "http://localhost:7101/eCareRestApp-Client-context-root/jersey/userdata/save/" + uniqueId + "/" + key + "/" + value;
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			
			
//			String output;
//			while ((output = br.readLine()) != null) {
//				response += output;
//			}

			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();


		}
		return true;
	}
	
	
	public static String getUserData(String uniqueId, String key)
	{
		
		String response = "";
		try {

			
			String urlString = "http://localhost:7101/eCareRestApp-Client-context-root/jersey/userdata/get/" + uniqueId + "/" + key;
			System.out.println("urlString : " + urlString);
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			
			
			String output;
			while ((output = br.readLine()) != null) {
				response += output;
			}

			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();


		}
		return response;
	}
	
	
	
	
	public static boolean isAuthenticated(String uniqueID, String ctn)
	{
		String response ="";
		try {

			URL url = new URL("http://localhost:7101/eCareRestApp-Client-context-root/jersey/otac/isAuthenticated/" + uniqueID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			
			
			String output;
			while ((output = br.readLine()) != null) {
				response += output;
			}

			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();


		}
		System.out.println("Response from servce isAuthenticated : " + response);
		if (response.contains("true"))
		return true;
		else
			return false;
	}
	
	public static String getBalance(String ctn)
	{
		
		String response = "";
		 try {

				URL url = new URL("http://localhost:7101/eCareRestApp-Client-context-root/jersey/balance/7557772080");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				
				System.out.println("Output from Server .... \n");
				String output;
				while ((output = br.readLine()) != null) {
					response += output;
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();


			}
		
		System.out.println(response);
		
		return response;
	}
	
	public static String getBill(String ctn)
	{
		
		String response = "";
		 try {

				URL url = new URL("http://localhost:7101/eCareRestApp-Client-context-root/jersey/gil/bill/puneet/sri");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				
				System.out.println("Output from Server .... \n");
				String output;
				while ((output = br.readLine()) != null) {
					response += output;
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();


			}
		 
		 
		 String str[] = response.split(":");
		 
		 String finalResponse = "Your bill for the month is £ " + str[0];
		 if (str[1].equals("true"))
			 finalResponse += "\n \n \nYour current bill is GREATER than Last bill!";
		 else
			 finalResponse += "\n \n \nYour current bill is LESS than Last bill!";
		 
		
		System.out.println(finalResponse);
		
		return finalResponse;
	}
	
	
	public static String whyBillIsHigh()
	{
		String finalResponse = "";
		
		// https://c74e4bc0.ngrok.io/eCareRestApp-Client-context-root/jersey/gis/reason/22222/44444
		try {

			URL url = new URL("https://c74e4bc0.ngrok.io/eCareRestApp-Client-context-root/jersey/gis/reason/22222/44444");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			
			System.out.println("Output from Server .... \n");
			String output;
			while ((output = br.readLine()) != null) {
				finalResponse += output;
			}

			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();


		}
		
		
		
		
		return finalResponse;
	}
	
	
	
	public static String getBillDueDate(String ctn)
	{
		String response = "";
		 try {
				URL url = new URL("http://localhost:7101/eCareRestApp-Client-context-root/jersey/gil/due/puneet/sri");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				
				System.out.println("Output from Server .... \n");
				String output;
				while ((output = br.readLine()) != null) {
					response += output;
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();
			}
		 
		 
		 String finalResponse = "The amount will be collected by DD on " + response;
		
		return finalResponse;
	}
	
	
	public static boolean authenticateByVoice(String uniqueID)
	{
		
//		String response = "";
//		 try {
//
//				URL url = new URL(" http://localhost:7101/eCareRestApp-Client-context-root/jersey/voicebio/auth/" + uniqueID + "/email/pwd");
//				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//				conn.setRequestMethod("GET");
//				conn.setRequestProperty("Accept", "application/json");
//
//				if (conn.getResponseCode() != 200) {
//					throw new RuntimeException("Failed : HTTP error code : "
//							+ conn.getResponseCode());
//				}
//
//				BufferedReader br = new BufferedReader(new InputStreamReader(
//					(conn.getInputStream())));
//
//				
//				//System.out.println("Output from Server .... \n");
//				String output;
//				while ((output = br.readLine()) != null) {
//					response += output;
//				}
//
//				conn.disconnect();
//
//			  } catch (MalformedURLException e) {
//
//				e.printStackTrace();
//
//			  } catch (IOException e) {
//
//				e.printStackTrace();
//
//
//			}
		
		String repsonse = new VoiceVerifier().authentication(IVoiceVerifier.EMAIL, IVoiceVerifier.PWD, "C:\\voiceBiometrics\\myvoice.wav", "70", "en-GB");
//		msg += " : " + msg;
		
		
		 boolean finalResponse;
		 if (repsonse.contains("SUC"))
			 finalResponse = true;
		 else
			 finalResponse = false;
		 
		 System.out.println("finalResponse : " + finalResponse);
		 
		return finalResponse;
	}

}
