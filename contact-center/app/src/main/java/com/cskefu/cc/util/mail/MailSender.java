/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.util.mail;

import org.apache.commons.lang.StringUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

public class MailSender {
	/**
	 * 发送邮件的props文件
	 */
	private final transient Properties props = System.getProperties();
	/**
	 * 邮件服务器登录验证
	 */
	private transient MailAuthenticator authenticator;
	
	/**
	 * 发送邮箱
	 */
	private String fromEmail;
	/**
	 * 邮箱session
	 */
	private transient Session session;

	/**
	 * 初始化邮件发送器
	 * 
	 * @param smtpHostName
	 *            SMTP邮件服务器地址
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            发送邮件的密码
	 */
	public MailSender(final String smtpHostName, final String username,
			final String password,final String seclev,String sslport) {
		init(username, password, smtpHostName,seclev,sslport);
	}
	
	/**
	 * 初始化邮件发送器
	 * 
	 * @param smtpHostName
	 *            SMTP邮件服务器地址
	 * @param fromEmail
	 * 			  邮件发送地址
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            发送邮件的密码
	 * @param seclev
	 *            是否开启ssl       
	 */
	public MailSender(final String smtpHostName,final String fromEmail, final String username,
			final String password,final String seclev,String sslport) {
		this.fromEmail = fromEmail;
		init(username, password, smtpHostName,seclev,sslport);
	}

	/**
	 * 初始化邮件发送器
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)，并以此解析SMTP服务器地址
	 * @param password
	 *            发送邮件的密码
	 */
	public MailSender(final String username, final String password,final String seclev,String sslport) {
		// 通过邮箱地址解析出smtp服务器，对大多数邮箱都管用
		final String smtpHostName = "smtp." + username.split("@")[1];
		init(username, password, smtpHostName,seclev,sslport);

	}

	/**
	 * 初始化
	 * 
	 * @param username
	 *            发送邮件的用户名(地址)
	 * @param password
	 *            密码
	 * @param smtpHostName
	 *            SMTP主机地址
	 */
	@SuppressWarnings("restriction")
	private void init(String username, String password, String smtpHostName,String seclev,String sslport) {
		// 初始化props
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", smtpHostName);
		//ssl
		if(!StringUtils.isBlank(seclev)&&seclev.equals("true")) {
//			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());  
			final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";  
			props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
			props.put("mail.smtp.socketFactory.fallback", "false");
			props.put("mail.smtp.port", sslport);
			props.put("mail.smtp.socketFactory.port", sslport);
		}
		// 验证
		authenticator = new MailAuthenticator(username, password);
		// 创建session
		session = Session.getInstance(props, authenticator);
	}

	/**
	 * 发送邮件
	 * 
	 * @param recipient
	 *            收件人邮箱地址
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	public void send(String recipient, String subject, Object content)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		// 创建mime类型邮件
		final MimeMessage message = new MimeMessage(session);
		if(authenticator!=null && authenticator.getUsername().indexOf("@") >0){
			// 设置发信人
			message.setFrom(new InternetAddress(authenticator.getUsername()));
		}
		if(this.fromEmail!=null){
			message.setFrom(new InternetAddress(this.fromEmail));
		}
		// 设置收件人
		String[] toUsers = recipient.split("[\n,;]") ;
		InternetAddress[] internetAddress = new InternetAddress[toUsers.length] ;
		for(int i=0 ; i<toUsers.length ; i++){
			internetAddress[i] = new InternetAddress(toUsers[i]) ;
		}
		message.setRecipients(RecipientType.TO, internetAddress);
		// 设置主题
//		message.setSubject(subject);
		message.setSubject(MimeUtility.encodeText(subject,"UTF-8","B"));
		// 设置邮件内容
		message.setContent(content.toString(), "text/html;charset=utf-8");
		// 发送
		Transport.send(message);
	}
	
	
	/**
	 * 附件发送
	 * @param recipient           接收人
	 * @param recipient_append    抄送人
	 * @param subject             邮件主题
	 * @param content             邮件正文
	 * @param filenames           附件列表
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void send(String recipient,String recipient_append, String subject, Object content,List<String> filenames)
			throws Exception {
		// 创建mime类型邮件
		final MimeMessage message = new MimeMessage(session);
		if(this.fromEmail!=null){
			message.setFrom(new InternetAddress(MimeUtility.encodeText(this.fromEmail, "UTF-8", "B"))+" <"+authenticator.getUsername()+">");
		}else if(authenticator!=null && authenticator.getUsername().indexOf("@") >0){
			// 设置发信人
			message.setFrom(new InternetAddress(MimeUtility.encodeText(authenticator.getUsername(), "UTF-8", "B")));
		}
		// 设置收件人
		String name = null;
		String addr = null;
		String[] toUsers = recipient.split("[\n,]") ;
		InternetAddress[] internetAddress = new InternetAddress[toUsers.length] ;
		for(int i=0 ; i<toUsers.length ; i++){
			if(toUsers[i].indexOf("<")>0&&toUsers[i].indexOf(">")>0){
				name = toUsers[i].substring(0, toUsers[i].indexOf("<"));
				addr = toUsers[i].substring(toUsers[i].indexOf("<")+1, toUsers[i].indexOf(">"));
				internetAddress[i] = new InternetAddress(addr,name,"utf-8") ;
			}else{
				internetAddress[i] = new InternetAddress(toUsers[i]) ;
			}
			
		}
		message.setRecipients(RecipientType.TO, internetAddress);
		
		// 设置收件人
		if(!StringUtils.isBlank(recipient_append)){
			String[] appendUsers = recipient_append.split("[\n,]") ;
			InternetAddress[] internetAddress_append = new InternetAddress[appendUsers.length] ;
			for(int i=0 ; i<appendUsers.length ; i++){
				if(appendUsers[i].indexOf("<")>0&&appendUsers[i].indexOf(">")>0){
					name = appendUsers[i].substring(0, appendUsers[i].indexOf("<"));
					addr = appendUsers[i].substring(appendUsers[i].indexOf("<")+1, appendUsers[i].indexOf(">"));
					internetAddress_append[i] = new InternetAddress(addr,name,"utf-8") ;
				}else{
					internetAddress_append[i] = new InternetAddress(appendUsers[i]) ;
				}
			}
			message.setRecipients(RecipientType.CC, internetAddress_append);
		}
		
		
		// 设置主题
//		message.setSubject(subject);
		message.setSubject(MimeUtility.encodeText(subject,"UTF-8","B"));
		// 设置邮件内容
		
		Multipart multipart = new MimeMultipart();  
		MimeBodyPart mbp = new MimeBodyPart();  
		mbp.setContent(content.toString(), "text/html;charset=utf-8");
		multipart.addBodyPart(mbp);
		if(filenames!=null&&filenames.size()>0){//有附件  
			for (String filename : filenames) {
				mbp=new MimeBodyPart();  
				FileDataSource fds=new FileDataSource(filename); 
				mbp.setDataHandler(new DataHandler(fds)); 
				mbp.setFileName(MimeUtility.encodeText(fds.getName(),"UTF-8","B"));  
				multipart.addBodyPart(mbp);  
			}     
        }   
		
		message.setContent(multipart); //Multipart加入到信件  
		// 发送
		Transport.send(message);
	}

	/**
	 * 群发邮件
	 * 
	 * @param recipients
	 *            收件人们
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	public void send(List<String> recipients, String subject, Object content)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		// 创建mime类型邮件
		final MimeMessage message = new MimeMessage(session);
		if(authenticator!=null && authenticator.getUsername().indexOf("@") >0){
			// 设置发信人
			message.setFrom(new InternetAddress(authenticator.getUsername()));
		}
		if(this.fromEmail!=null){
			message.setFrom(new InternetAddress(this.fromEmail));
		}
		// 设置收件人们
		final int num = recipients.size();
		InternetAddress[] addresses = new InternetAddress[num];
		for (int i = 0; i < num; i++) {
			addresses[i] = new InternetAddress(recipients.get(i));
		}
		message.setRecipients(RecipientType.TO, addresses);
		// 设置主题
//		message.setSubject(subject);
		
		//邮件标题处理，防止乱码
		message.setSubject(MimeUtility.encodeText(subject,"UTF-8","B"));
		// 设置邮件内容
		message.setContent(content.toString(), "text/html;charset=utf-8");
		// 发送
		Transport.send(message);
	}

	/**
	 * 发送邮件
	 * 
	 * @param recipient
	 *            收件人邮箱地址
	 * @param mail
	 *            邮件对象
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	public void send(String recipient, MailInfo mail) throws AddressException,
			MessagingException, UnsupportedEncodingException {
		send(recipient, mail.getSubject(), mail.getContent());
	}

	/**
	 * 群发邮件
	 * 
	 * @param recipients
	 *            收件人们
	 * @param mail
	 *            邮件对象
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	public void send(List<String> recipients, MailInfo mail)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		send(recipients, mail.getSubject(), mail.getContent());
	}

}
