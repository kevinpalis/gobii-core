package org.gobiiproject.gobiimodel.utils.email;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MailMessage{
	
	private String user;
	private String subject;
	private String body;
	private String header;
	private String footer;
	private String confidentialityMessage;
	private String img = "GobiiWebImg.png"; // Note: This is full sized logo from the main page
	private List<String> fileAttachments = new ArrayList<>();
	
	MailMessage(){
		header = "<b><i>"+getHeaderSalutation()+"</i></b><br><br> Here is a summary of your transaction <br> <br>";
		footer = "<br><br>"+getFooterSalutation()+", <br> <a href=\"http://www.gobiiproject.org\"><img src=\"cid:image\" width=\"300\"></a><br>";
	}
	private static String getHeaderSalutation(){
		int numChoices=10;//Several cases of Good Day
		int choice=(int)Math.floor(Math.random()*numChoices);
		switch(choice){
			case 0: return "G'day.";
			case 1: return "Greetings!";
			case 2:	return "Salutations!";
			case 3: return "Hope you're doing well.";
			default:
				return "Good day!";
		}
	}

	private static String getFooterSalutation(){
		int numChoices=5;//Several cases of Good Day
		int choice=(int)Math.floor(Math.random()*numChoices);
		switch(choice){
			case 0: return "Respectfully";
			case 1: return "Enjoy";
			case 2:	return "Thanks";
			case 3: return "Cheers";
			default:
				return "Best";
		}
	}
	
	public String getUser(){
		return user;
	};
	
	public MailMessage setUser(String user){
		this.user = user;
		return this;
	}
	
	public String getHeader(){
		return header;
	}
	
	public URL getImg(){
		return ClassLoader.getSystemClassLoader().getResource(img);
	}
	
	public String getFooter(){
		return footer;
	}

	public String getSubject(){
		return subject;
	}
	
	public MailMessage setSubject(String subject){
		this.subject = subject;
		return this;	
	}
	
	public String getBody(){
		return body;
	}
	
	public MailMessage setBody(String body){
		this.body = body;
		return this;
	}

	public List<String> getFileAttachments() { return fileAttachments;}

	public MailMessage setFileAttachments(List<String> fileAttachments) {
		this.fileAttachments = fileAttachments;
		return this;
	}

	public String getConfidentialityMessage() {
		return confidentialityMessage;
	}

	public MailMessage setConfidentialityMessage(String confidentialityMessage){
		this.confidentialityMessage = confidentialityMessage;
		return this;
	}
}