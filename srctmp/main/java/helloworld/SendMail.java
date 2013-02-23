package helloworld;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	
	private Properties p = new Properties();
	private String message;
	
	
	public SendMail(){

		p.put("mail.smtp.user", "3th.bob.cloud@gmail.com"); 
		p.put("mail.smtp.host", "smtp.gmail.com");
		p.put("mail.smtp.port", "465");
		p.put("mail.smtp.starttls.enable", "true");
		p.put("mail.smtp.auth", "true");

		p.put("mail.smtp.debug", "true");
		p.put("mail.smtp.socketFactory.port", "465");
		p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback", "false");

		// Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	}
	
	public void setMessage(String _message){
		this.message = _message;
	}

	protected String getMessage(){
		return message;
	}
	
	public void MailSend() {

		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(p, auth);
			session.setDebug(true); 

			// session = Session.getDefaultInstance(p);
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject("당신의 서버에서 비정상 행위가 탐지되었습니다.");
			Address fromAddr = new InternetAddress("3th.bob.cloud@gmail.com"); 
			msg.setFrom(fromAddr);
			Address toAddr = new InternetAddress("3th.bob.cloud@gmail.com"); 
			msg.addRecipient(Message.RecipientType.TO, toAddr);
			msg.setContent(getMessage(), "text/plain;charset=KSC5601");
			//System.out.println("Message: " + msg.getContent());
			Transport.send(msg);
			//System.out.println("Gmail SMTP서버를 이용한 메일보내기 성공");
		} catch (Exception mex) { 
			System.out.println("Error SMTP!");
			mex.printStackTrace();
		}
	}

	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("3th.bob.cloud", "dlfdltkatk"); 
		}
	}
}
