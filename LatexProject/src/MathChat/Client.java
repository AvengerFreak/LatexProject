package MathChat;

/** Connects a user to a server in order to join a Multi User chat.
 * @author Sha�na N. Mu�oz
 * @version 1.2
 * @since 1.2
*/

// TODO - better grafics with this tutorial: https://github.com/vkhanhqui/chat-web-app

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

// 

public class Client {

    BufferedReader in;
    PrintWriter out;
    String name;
    String address;
    JFrame frame = new JFrame("Math Chat");
    JTextField textField = new JTextField(30);
    JTextPane messageArea = new JTextPane();
    JScrollPane scrollPane = new JScrollPane(messageArea);
    DefaultCaret defaultCaret = (DefaultCaret) messageArea.getCaret();
    AudioInputStream sendAudioInputStream, recieveAudioInputStream;
    Clip sendAudio, recieveAudio;
    StyledDocument doc = messageArea.getStyledDocument();


    /** 
     * initializes all parameters to create the chat window, connects user to the
     * server and joins the user to a chat group by the ip adress 
     */
    
    public Client() {

        textField.setEditable(false);
        messageArea.setEditable(false);
        defaultCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        frame.getContentPane().add(textField, "South");
        frame.getContentPane().add(scrollPane, "Center");        
        frame.pack();
        
        try {
            
            URL sendUrl = this.getClass().getClassLoader().getResource("MathChat/sounds/send.wav");
            URL recieveUrl = this.getClass().getClassLoader().getResource("MathChat/sounds/receive.wav");

        	sendAudioInputStream = AudioSystem.getAudioInputStream(sendUrl);
            recieveAudioInputStream = AudioSystem.getAudioInputStream(recieveUrl);
        	
            recieveAudio = AudioSystem.getClip();
            recieveAudio.open(recieveAudioInputStream);

            sendAudio = AudioSystem.getClip();
        	sendAudio.open(sendAudioInputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}

        textField.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
            	if (!textField.getText().isEmpty()) {
            		out.println(textField.getText());
            	}
                textField.setText("");
            }
        });
    }
    
    /** 
     * Asks user for an ip address and validates said address is valid 
     */

    private String getServerAddress() {
    	
        try{
            InetAddress IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        }

        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    	
    }

    /** 
     * Asks User to enter a single word as a User Name 
     * @throws IOException
     */
    
    private String getName() throws IOException {
    	
        name = JOptionPane.showInputDialog(
                frame,
                "Choose a valid display name:",
                "Display name",
                JOptionPane.PLAIN_MESSAGE);

        String regex = "^[a-zA-Z0-9]{1,12}+$";
        
        Pattern pattern = Pattern.compile(regex);
        
        Matcher matcher = pattern.matcher(name);
        
    	while (name.isEmpty() || !matcher.matches()) {

			JOptionPane.showMessageDialog(frame, "Please enter a valid display name.");
			
    		name = JOptionPane.showInputDialog(
                    frame,
                    "Choose a valid display name:",
                    "Display name",
                    JOptionPane.PLAIN_MESSAGE);
    		
    		pattern = Pattern.compile(regex);
    		
    		matcher = pattern.matcher(name);
		}
    	
    	return name;
    }
    
    /** 
     * Execute Client Thread, listen to server tags and display messages on window
     * @ throws IOException
     */
 
    private void run() throws IOException {

        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        while (true) {
        	
            String line = in.readLine();
            
            if (line.startsWith("SUBMIT")) {
                out.println(getName());
            } 
            
            else if (line.startsWith("JOIN")) {
                textField.setEditable(true);
                textField.requestFocusInWindow();
            } 
    
            else if (line.startsWith("MESSAGE")) {
            	
            	try
				{
                    String sender = line.substring(8, line.indexOf(":"));

                // you are sending message
                if(sender.equals(name)){
                    sendAudio.setFramePosition(0);
                    sendAudio.start();
                }

                // you are recieving message
                else{
                    recieveAudio.setFramePosition(0);
                    recieveAudio.start();
                }

				    doc.insertString(doc.getLength(), line.substring(8) + "\n", null );
				    //doc.insertString(doc.getLength(), "\nEnd of text", keyWord );
				}
				catch(Exception e) { System.out.println(e); }
            }
            
            
            // extract LaTex string from input, create TextIcon and append it to window
            else if(line.startsWith("FORMULA")){

                String sender = line.substring(7, line.indexOf(":"));

                // you are sending message
                if(sender.equals(name)){
                    sendAudio.setFramePosition(0);
                    sendAudio.start();
                }

                // you are recieving message
                else{
                    recieveAudio.setFramePosition(0);
                    recieveAudio.start();
                }
        		
            	String first = line.substring(0, line.indexOf("formula:")) + "\n\n";
				String math = line.substring(line.indexOf("formula:") + 8, line.length());

				try
				{
				    doc.insertString(doc.getLength(), first, null ); 
				    //doc.insertString(doc.getLength(), "\nEnd of text", keyWord );
				}
				catch(Exception e) { System.out.println(e); }
				
				TeXFormula formula = new TeXFormula(math);
				TeXIcon ti = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15);
				BufferedImage b = new BufferedImage(ti.getIconWidth(), ti.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				ti.paintIcon(new JLabel(), b.getGraphics(), 0, 0);
				messageArea.insertIcon(ti);
				
				try {
					doc.insertString(doc.getLength(), "\n\n", null);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

        	}

        }
    }

    /** 
     * Creates a thread for client
     * @throws Exception 
     */
    
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.frame.setLocationRelativeTo(null);
        client.run();
    }
}