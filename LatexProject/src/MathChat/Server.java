package MathChat;

/** Creates a Window and displays a sample formula using LaTex.
 * @author Shaína N. Muñoz
 * @version 1.0
 * @since 1.0
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Server {

    private static final int PORT = 9001;

    private static HashSet<String> names = new HashSet<String>();

    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    /** 
     * Executes the server thread
     * @throws Exception
     */
    
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is now running...");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    
    /** 
     * Sever Handler appends a tag to the user array of commands in order to 
     * organize each action by category and link it to the corresponding user  
     */
    
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        /** 
         * Creates a Server thread  
         */
        
        public Handler(Socket socket) {
            this.socket = socket;
        }

        
        /** 
         * Execute the server thread and listens to actions performed by client 
         */
        
        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMIT");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    
                    // if user was not  added to user array, then add
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                // when user enters for the first time
                out.println("JOIN");
                System.out.println(name + " has joined the chat.");
                writers.add(out);
                
                // when action is to send a message
                while (true) { 
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    
                    // if user enters "formula:" + a LaTex string 
                    for (PrintWriter writer : writers) {
                
                    	if(input.contains("formula:")){
                    		writer.println("FORMULA " + name + ": " + input);
                    		continue;
                    		}
                    	
                    	// regular message
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
                
                // when user closes window
            } catch (IOException e) {
                System.out.println(name + " has exited the chat.");
            } finally {
            	
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}