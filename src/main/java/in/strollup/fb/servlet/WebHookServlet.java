package in.strollup.fb.servlet;

import in.strollup.fb.contract.Attachment;
import in.strollup.fb.contract.FbMsgRequest;
import in.strollup.fb.contract.Messaging;
import in.strollup.fb.utils.FbChatHelper;
import in.strollup.fb.utils.RestServiceHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.poc.vi.main.IVoiceVerifier;
import com.poc.vi.main.VoiceVerifier;

/**
 * 
 * @author thekosmix
 * 
 *
 */
public class WebHookServlet extends HttpServlet {

	private static final long serialVersionUID = -2326475169699351010L;

	/************* FB Chat Bot variables *************************/
	public static final String PAGE_TOKEN = "EAAJnu2WtCq0BAP1FFDZC1i9MLKxJPzhVCa5pZAfMd8LQkrNnxI45066KFWWZB5ElKkmCcxMKDRhazz7MiP2D5ohXdLAaKTOxnwCruEwYBH9ZArrsyCqRcL2GtPF2ke87RoJlLGLZA4gRDpU6t42euyUyq6BXti1HCZAdEXcAvvjgZDZD";
	private static final String VERIFY_TOKEN = "puneets_token";
	private static final String FB_MSG_URL = "https://graph.facebook.com/v2.6/me/messages?access_token="
			+ PAGE_TOKEN;
	/*************************************************************/

	/******* for making a post call to fb messenger api **********/
	private static final HttpClient client = HttpClientBuilder.create().build();
	private static final HttpPost httppost = new HttpPost(FB_MSG_URL);
	private static final FbChatHelper helper = new FbChatHelper();
	/*************************************************************/

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("test post");
	
		processRequest(request, response);
	}

	/**
	 * get method is used by fb messenger to verify the webhook
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String queryString = request.getQueryString();
		System.out.println("doGet called !!");
		String msg = "Error, wrong token";

		
		//String repsonse = new VoiceVerifier().authentication(IVoiceVerifier.EMAIL, IVoiceVerifier.PWD, "C:\\voiceBiometrics\\myvoice.wav", "70", "en-GB");
		if (queryString != null) {
			String verifyToken = request.getParameter("hub.verify_token");
			String challenge = request.getParameter("hub.challenge");
			// String mode = request.getParameter("hub.mode");

			if (StringUtils.equals(VERIFY_TOKEN, verifyToken)
					&& !StringUtils.isEmpty(challenge)) {
				msg = challenge;
			} else {
				msg = "";
			}
		} else {
			System.out
					.println("Exception no verify token found in querystring:"
							+ queryString);
		}

		response.getWriter().write(msg);
		response.getWriter().flush();
		response.getWriter().close();
		response.setStatus(HttpServletResponse.SC_OK);
		return;
	}

	private void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse response) throws IOException, ServletException {
		/**
		 * store the request body in stringbuffer
		 */
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = httpRequest.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		System.out.println("messge from FB >>>> " + jb.toString());
		
		/**
		 * convert the string request body in java object
		 */
		FbMsgRequest fbMsgRequest = new Gson().fromJson(jb.toString(),
				FbMsgRequest.class);
		if (fbMsgRequest == null) {
			System.out.println("fbMsgRequest was null");
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		List<Messaging> messagings = fbMsgRequest.getEntry().get(0)
				.getMessaging();
		for (Messaging event : messagings) {
			
			
			String sender = event.getSender().getId();
			if (event.getMessage() != null
					&& event.getMessage().getText() != null) {
				
				System.out.println("processRequest - Meta data : " + event.getMessage().getMetadata());
				
				String text = event.getMessage().getText();
				sendTextMessage(sender, text, false, event.getMessage().getMetadata());
			}else if (event.getMessage() != null && event.getMessage().getAttachments() != null)  {
				
				System.out.println("attachedment found : " + event.getMessage().getAttachments());
				List<Attachment> attachmentList = event.getMessage().getAttachments();
				Attachment attachment = attachmentList.get(0);
				
				
				if (attachment.getPayload() != null)
				{
					String url = attachment.getPayload().getUrl();
					System.out.println("url : " + url);
					saveTheFileFromURL(url);
				}
				String text = "";
				sendTextMessageForVoice(sender, false);
			}
			else if (event.getPostback() != null) {
				String text = event.getPostback().getPayload();
				System.out.println("postback received: " + text);
				sendTextMessage(sender, text, true, "");
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * get the text given by senderId and check if it's a postback (button
	 * click) or a direct message by senderId and reply accordingly
	 * 
	 * @param senderId
	 * @param text
	 * @param isPostBack
	 */
	private void sendTextMessage(String senderId, String text,
			boolean isPostBack, String metaData) {
		List<String> jsonReplies = null;
		System.out.println("isPostBack : " + isPostBack);
		
		if (isPostBack) {
			jsonReplies = helper.getPostBackReplies(senderId, text, metaData);
		} else {
			jsonReplies = helper.getReplies(senderId, text, metaData);
		}

		for (String jsonReply : jsonReplies) {
			try {
				HttpEntity entity = new ByteArrayEntity(
						jsonReply.getBytes("UTF-8"));
				httppost.setEntity(entity);
				HttpResponse response = client.execute(httppost);
				String result = EntityUtils.toString(response.getEntity());
				System.out.println("result : " + result);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	
	
	/**
	 * get the text given by senderId and check if it's a postback (button
	 * click) or a direct message by senderId and reply accordingly
	 * 
	 * @param senderId
	 * @param text
	 * @param isPostBack
	 */
	private void sendTextMessageForVoice(String senderId, boolean isPostBack) {
		
		String text = "";
		boolean success = RestServiceHelper.authenticateByVoice(senderId);
		
		if (success)
		{
			text ="You have been successfully VERRIFIED ! Please ask furhter question.";
		}
		else
		{
			text = "Sorry authenticated FAILED! Please try again.";
		}
		
		
		List<String> jsonReplies = helper.getCustomReplies(senderId, text, null);

		for (String jsonReply : jsonReplies) {
			try {
				HttpEntity entity = new ByteArrayEntity(
						jsonReply.getBytes("UTF-8"));
				httppost.setEntity(entity);
				HttpResponse response = client.execute(httppost);
				String result = EntityUtils.toString(response.getEntity());
				System.out.println("result : " + result);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	

	@Override
	public void destroy() {
		System.out.println("webhook Servlet Destroyed");
	}

	@Override
	public void init() throws ServletException {
		httppost.setHeader("Content-Type", "application/json");
		System.out.println("webhook servlet created!!");
	}
	
	public boolean saveTheFileFromURL(String voiceURL)
	{
		boolean success = true;
		try {
			URL url = new URL(voiceURL);
			URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File("c:\\voiceBiometrics\\myvoice.wav"));
			byte[] buf = new byte[512];
			while (true) {
			    int len = in.read(buf);
			    if (len == -1) {
			        break;
			    }
			    fos.write(buf, 0, len);
			}
			in.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}
}
