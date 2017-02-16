package in.strollup.fb.utils;

import in.strollup.fb.contract.Attachment;
import in.strollup.fb.contract.Button;
import in.strollup.fb.contract.Element;
import in.strollup.fb.contract.Message;
import in.strollup.fb.contract.Messaging;
import in.strollup.fb.contract.Payload;
import in.strollup.fb.contract.Recipient;
import in.strollup.fb.profile.FbProfile;
import in.strollup.fb.servlet.WebHookServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

/**
 * A Utility class which replies to fb messgaes (or postbacks).<br/>
 * If you already have a service which takes string as input and gives some
 * output, you can easily embed that service in this utility to make your own AI
 * bot.<br/>
 * 
 * A Service can be a search engine for music or a help desk of a company.
 * 
 * @author siddharth
 *
 */
public class FbChatHelper {
	private String vr = "VR headsets";
	private List<TitleSubTitle> vrImageUrls;
	private static String profileLink = "https://graph.facebook.com/v2.6/SENDER_ID?access_token="
			+ WebHookServlet.PAGE_TOKEN;

	public FbChatHelper() {
		vrImageUrls = new ArrayList();

		TitleSubTitle strollup = new TitleSubTitle();
		strollup.setUrl("https://static.strollup.in/image/191/100/StrollUp-FB-Logo-200x200.jpg");
		strollup.setTitle("StrollUp");
		strollup.setSubTitle("Explore best events and activities over chat");
		vrImageUrls.add(strollup);

		TitleSubTitle rift = new TitleSubTitle();
		rift.setUrl("http://messengerdemo.parseapp.com/img/rift.png");
		rift.setTitle("Rift VR");
		rift.setSubTitle("Rift VR is the only virtual reality head gear you'll ever need");
		vrImageUrls.add(rift);

		TitleSubTitle gearVr = new TitleSubTitle();
		gearVr.setUrl("http://messengerdemo.parseapp.com/img/gearvr.png");
		gearVr.setTitle("Gear VR");
		gearVr.setSubTitle("Gear VR is not just the best, it's better than the best");
		vrImageUrls.add(gearVr);
	}

	private class TitleSubTitle {
		private String url;
		private String title;
		private String subTitle;

		public void setUrl(String url) {
			this.url = url;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setSubTitle(String subTitle) {
			this.subTitle = subTitle;
		}
	}

	/**
	 * methods which analyze the postbacks ie. the button clicks sent by
	 * senderId and replies according to it.
	 * 
	 * @param senderId
	 * @param text
	 * @return
	 */
	public List<String> getPostBackReplies(String senderId, String text, String metaData) {
		List<String> postbackReplies = new ArrayList<String>();
		String msg = "received postback msg: " + text;

		Message fbMsg = getMsg(msg, metaData);
		String fbReply = getJsonReply(senderId, fbMsg);
		postbackReplies.add(fbReply);

		Message vrCards = getVRCards();
                
		String fbVrCards = getJsonReply(senderId, vrCards);
		postbackReplies.add(fbVrCards);

		return postbackReplies;
	}

	/**
	 * methos which analyze the simple texts sent by senderId and replies
	 * according to it.
	 * 
	 * @param senderId
	 * @param text
	 * @return
	 */
	public List<String> getReplies(String senderId, String text, String metaData) {
		System.out.println("getReplies >> text : " + text + " metaData : " + metaData + " senderId : " + senderId);
	
		List<String> replies = new ArrayList<String>();
		String link = StringUtils.replace(profileLink, "SENDER_ID", senderId);
		FbProfile profile = getObjectFromUrl(link, FbProfile.class);
		// profile.

		text = text.toLowerCase();
		String msg = "Hello " + profile.getFirstName()
				+ ", I've received msg: " + text;
		Message fbMsg = null;

		String uniqueId = senderId;
		if (uniqueId == null || uniqueId.isEmpty())
		{
			uniqueId = "uniqueIdX";
		}
		String otac = "1234";
		//System.out.println("otac  " + otac);
		if (text.contains("my") && text.contains("number")) {
			String ctn = getNumberValueFromText(text);
			System.out.println("Enterred CTN : " + ctn);
			// setting the meta data for the FIRST time
			metaData = ctn;
			boolean response = RestServiceHelper.saveOtac(ctn, "1234");
			
			RestServiceHelper.saveUserData(uniqueId, "CTN", ctn);

			msg = "I now know your mobile number. we have sent OTAC to your mobile number  "
					+ ctn;

		} else if (text.contains("otac") && text.contains("my")) {
			String enteredOtac = getNumberValueFromText(text);
			System.out.println("Entered Otac : " + enteredOtac);
			String ctn = RestServiceHelper.getUserData(uniqueId, "CTN");
			boolean response = RestServiceHelper.validateOtac(ctn,
					enteredOtac);

			if (response) {
				msg = "You have been VERIFIED !! Please ask further questions.";
				// msg += RestServiceHelper.getBill("123615269");
			} else {
				msg = "INCORRECT otac, please enter again !!";
			}

		} else if (text.contains("balance")) {
				
			String ctn = RestServiceHelper.getUserData(uniqueId, "CTN");
				if (RestServiceHelper.isAuthenticated(uniqueId, "")) {
				msg = RestServiceHelper.getBalance(ctn);
			} else {
				
				
				if (ctn == null || ctn.equalsIgnoreCase("null"))
				{
					msg = "Your are not authenticated please provide your mobile number !";
				}
				else
				{
				RestServiceHelper.saveOtac(uniqueId, otac);
				msg = "\nYour are not authenticated please enter the otac send to your mobile number"
						+ ctn;
				}
			}
		} 
		
		
		
		else if (text.contains("bill") && text.contains("much")) {
			
			String ctn = RestServiceHelper.getUserData(uniqueId, "CTN");
			if (RestServiceHelper.isAuthenticated(ctn, "")) {
			msg = RestServiceHelper.getBill(ctn);
		} else {
			
			
			if (ctn == null || ctn.equalsIgnoreCase("null"))
			{
				msg = "Your are not authenticated please provide your mobile number !";
			}
			else
			{
			RestServiceHelper.saveOtac(uniqueId, otac);
			msg = "\nYour are not authenticated please enter the otac send to your mobile number"
					+ ctn;
			}
		}
		}

		else if (text.contains("bill") && text.contains("big")) {
			

			
			String ctn = RestServiceHelper.getUserData(uniqueId, "CTN");
			if (RestServiceHelper.isAuthenticated(ctn, "")) {
			String response = RestServiceHelper.whyBillIsHigh();
			
			
			
			msg = "I don't know, are you a PELICAN? only kidding!  " + response ;
			msg += " Would you like me to email you a copy of your Bill?";
			
			
			
			
		} else {
			
			
			if (ctn == null || ctn.equalsIgnoreCase("null"))
			{
				msg = "Your are not authenticated please provide your mobile number !";
			}
			else
			{
			RestServiceHelper.saveOtac(uniqueId, otac);
			msg = "\nYour are not authenticated please enter the otac send to your mobile number"
					+ ctn;
			}
			
			
		}
			
			
			
		} else if (text.contains("bill") && text.contains("due")) {
			if (RestServiceHelper.isAuthenticated(uniqueId, "")) {
				msg = RestServiceHelper.getBillDueDate("123615269");

				msg += "\n \nWould you like me to email you a copy of your Bill?";
			} else {
				RestServiceHelper.saveOtac(uniqueId, otac);
				msg = "\nYou are NOT authenticated!! \nPlease enter the otac send to you.";
			}
		} else if (text.contains("yes")) {
			msg = "I have sent it to puneet.srivastava@hpe.com \n \n Is there anything else I can help you with - 2?";

		} else if (text.contains("no") && text.contains("thank")) {
			msg = "Is there anything else I can help you with?";
		} else if (text.contains("no")) {
			msg = "Thank you!";

		}

		fbMsg = getMsg(msg, metaData);
		String fbReply = getJsonReply(senderId, fbMsg);
		replies.add(fbReply);

		return replies;
	}
	
	
	
	public List<String> getCustomReplies(String senderId, String text, String metaData) {
		List<String> replies = new ArrayList<String>();
		
		Message fbMsg = getMsg(text, metaData);
		String fbReply = getJsonReply(senderId, fbMsg);
		replies.add(fbReply);

		return replies;
	}
	
	
	
	
	
	
	
	private String getOtac()
	{
		String otacStr = "1234";
		
		return otacStr;
	}

	private Message getMsg(String msg, String metaData) {
		Message message = new Message();
		message.setText(msg);
		if (metaData != null)
                message.setMetadata(metaData);
		return message;
	}

	private Message getProductQuestion() {

		String questionStr = "Which product would you like to see?";
		List<Button> buttons = getOptionButtons();

		Payload payload = new Payload();
		payload.setText(questionStr);
		payload.setButtons(buttons);
		payload.setTemplateType("button");

		return getQuestion(payload);
	}

	private List<Button> getOptionButtons() {
		List<Button> buttons = new ArrayList<Button>();

		Button strollupBot = new Button();
		buttons.add(strollupBot);
		strollupBot.setType("web_url");
		strollupBot.setTitle("StrollUp Bot");
		strollupBot.setUrl("https://www.strollup.in/#!/newchat");

		Button option = new Button();
		buttons.add(option);
		option.setType("postback");
		option.setTitle(vr);
		option.setPayload(vr);

		Button showDetail = new Button();
		buttons.add(showDetail);
		showDetail.setType("web_url");
		showDetail.setTitle("Fb Project");
		showDetail.setUrl("https://github.com/thekosmix/Java-FbChatBot");

		return buttons;
	}

	private Message getQuestion(Payload payload) {
		Attachment attachment = new Attachment();
		attachment.setPayload(payload);
		attachment.setType("template");
                

		Message message = new Message();
		message.setAttachment(attachment);
		return message;
	}

	/**
	 * final body which will be sent to fb messenger api through a post call
	 * 
	 * @see WebHookServlet#FB_MSG_URL
	 * @param senderId
	 * @param message
	 * @return
	 */
	private String getJsonReply(String senderId, Message message) {
		Recipient recipient = new Recipient();
		recipient.setId(senderId);
		Messaging reply = new Messaging();
		reply.setRecipient(recipient);
		reply.setMessage(message);

		String jsonReply = new Gson().toJson(reply);
		return jsonReply;
	}

	private Message getVRCards() {
		List<Element> elements = getElements();
		Payload payload = new Payload();
		payload.setElements(elements);
		payload.setTemplateType("generic");

		return getQuestion(payload);
	}

	private List<Element> getElements() {
		List<Element> elements = new ArrayList<Element>();

		for (TitleSubTitle image : vrImageUrls) {
			Element element = new Element();
			elements.add(element);

			List<Button> buttons = getButtons(image.title, image.url);
			element.setButtons(buttons);

			element.setTitle(image.title);
			element.setSubtitle(image.subTitle);
			element.setImageUrl(image.url);

		}
		return elements;
	}

	private List<Button> getButtons(String title, String url) {
		List<Button> buttons = new ArrayList<Button>();
		Button showDetail = new Button();
		buttons.add(showDetail);
		showDetail.setType("web_url");
		showDetail.setTitle("Show more detail");
		showDetail.setUrl("https://www.strollup.in/#!/newchat");

		Button book = new Button();
		buttons.add(book);
		book.setType("postback");
		book.setTitle("What's this?");
		book.setPayload(vr);

		return buttons;
	}

	/**
	 * Returns object of type clazz from an json api link
	 * 
	 * @param link
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private <T> T getObjectFromUrl(String link, Class<T> clazz) {
		T t = null;
		URL url;
		String jsonString = "";
		try {
			url = new URL(link);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				jsonString = jsonString + inputLine;
			} 
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!StringUtils.isEmpty(jsonString)) {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, clazz);
		}
		return t;
	}
        
        
    public static boolean isNumeric(String str)  
            {  
              try  
              {  
                double d = Double.parseDouble(str);  
              }  
              catch(NumberFormatException nfe)  
              {  
                return false;  
              }  
              return true;  
            }
    
    private String getNumberValueFromText(String response)
            {
                    String ctnString = "";
                    
                    String[] strings = response.split(" ");
                    
                    if (strings != null && strings.length > 0)
                    {
                            for (String str : strings)
                            {
                                    if (isNumeric(str))
                                    {
                                            ctnString = str;
                                            break;
                                    }
                            }
                    }
                    return ctnString;
            } 
}
