/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 * @author Peter Hagane
 */
public class MailSender {

    //lager nytt message-objekt
    private String userNameFrom = null;                     //navnet på brukeren som sender eposten
    
    //følgende felter er IKKE sikre å ha med på klientsiden.
    private String mailUserName = "slit.beskjed";               
    private String mailAddressFrom = "slit.beskjed@gmail.com";  //epostadressen til avsender
    private String password = "universitetetiagder";            //passordet til denne kontoen
    private int port = 465;
    private String host = "smtp.gmail.com";
    
    Properties mailSettings;
    Session session;
    MimeMessage message;
    Transport transport;
    
    public void sendMail(String subjectTitle, String messageContent, String recipient) throws MessagingException {
        try {
            //definerer SMTP-instillinger
            setEmailSettings();
            //passerer innholdet som skal være i eposten videre til metoden som setter messageobjektet
            setMessageObject(subjectTitle, messageContent, recipient);
            //henter messageobjektet og sender eposten med innhold
            session = Session.getInstance(mailSettings, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailAddressFrom, password);
                }
            });
            
            
            transport = session.getTransport("smtps");
            transport.connect (host, port, mailAddressFrom, password);
            transport.send(message);
            transport.close();
            JOptionPane.showMessageDialog(null, "Mail sent.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setEmailSettings(){
        //definerer SMTP-instillinger -- i dette tilfellet, Googles SMTP-server som en placeholder
        mailSettings = new Properties();
        //mailSettings.put("mail.smtp.starttls.enable","true");
        mailSettings.put("mail.smtp.host", host);
        mailSettings.put("mail.smtp.user", mailAddressFrom);
        mailSettings.put("mail.smtp.password", password);
        //mailSettings.put("mail.smtp.socketFactory.fallback", "true");
        //mailSettings.put("mail.smtp.auth", "true");
        mailSettings.put("mail.smtp.debug", "true");
        mailSettings.put("mail.smtp.port", "465");
    }

    public void setMessageObject(String subjectTitle, String messageContent, String recipient) {
        try {
            //setter adressen til avsender(klienten), tittel, innhold, og mottaker
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailAddressFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subjectTitle);
            message.setText(messageContent);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
