/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import util.MailSender;

/**
 *
 * @author Peter Hagane
 */
public class MailFrame {

    String userSender = null;
    String userSenderAddress = null;
    String recipient = null;
    MailSender mailInstance;

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton sendMailButton;
    private JLabel contentLabel, titleLabel;
    private final JFrame mailFrame;

    
    
    public MailFrame(String recipient, String userSender, String userSenderAddress) {
        this.recipient = recipient;
        this.userSender = userSender;
        this.userSenderAddress = userSenderAddress;
        mailFrame = new JFrame("Send en e-post til " + recipient);
        mailFrame.setSize(500, 450);
        mailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        placeComponents(mailFrame);
        mailFrame.setVisible(true);
    }

    private void placeComponents(JFrame mailFrame) {
        mailFrame.setLayout(null);

        titleLabel = new JLabel("Tittel");
        titleLabel.setBounds(10, 3, 80, 25);
        mailFrame.add(titleLabel);

        contentLabel = new JLabel("Innhold");
        contentLabel.setBounds(10, 45, 80, 25);
        mailFrame.add(contentLabel);

        sendMailButton = new JButton("Send");
        sendMailButton.setBounds(10, 380, 80, 25);
        mailFrame.add(sendMailButton);
        sendMailButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                sendMail();
                mailFrame.dispose();
            } catch (MessagingException ex) {
                Logger.getLogger(MailFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        });

        titleField = new JTextField(100);
        titleField.setBounds(5, 25, 472, 25);
        mailFrame.add(titleField);

        contentArea = new JTextArea();
        contentArea.setBounds(5, 70, 472, 300);
        mailFrame.add(contentArea);
        contentArea.setEditable(true);
    }

    public void sendMail() throws MessagingException {
        //setter tittel, innhold og mottaker til avsender
        String subjectTitle = getSubjectTitle();
        String messageContent = getMessageContent();

        //lager ny instanse av MailSender
        this.mailInstance = new MailSender();
        try {
            //sender mailinnholdet
            mailInstance.sendMail(subjectTitle, messageContent, recipient);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        //lukker instansen av MailSender
        closeInstance();
    }

    public String getSubjectTitle() {
        return titleField.getText();
    }

    public String getMessageContent() {
        String contentString = contentArea.getText();
        return "Denne e-posten er fra " + getUserSender() + ". Send svar til " + getUserSenderAddress() + ". \n\n" + contentString;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getUserSender() {
        return userSender;
    }

    public String getUserSenderAddress() {
        return userSenderAddress;
    }

    public void closeInstance() {
        this.mailInstance = null;
    }
}
