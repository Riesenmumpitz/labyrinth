package client;

import generated.LoginMessageType;
import generated.MazeCom;
import generated.MazeComType;
import generated.ObjectFactory;
import ki.Ki;
import network.XmlOutStream;

import javax.net.SocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
public final class GameClient {

    private static final String GROUPNAME = "4 und ein Keks Destroyer";
    private static XmlOutStream outToServer;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = true;
    private static Socket socket;
    private static ObjectFactory objectFactory = new ObjectFactory();
    private static String PATH_TO_PROPERTIES = "/config.properties";

    private GameClient(Socket socket) throws IOException {
        GameClient.socket = socket;
        outToServer = new XmlOutStream(GameClient.socket.getOutputStream());
    }

    public static void main(String[] args) throws Throwable {
//    	String serverIP = args[0]; // quasi also dann erster Parameter
    	String serverIP = "localhost";
    	int serverPort = 5123;
//    	int serverPort = Integer.parseInt(args[1]);
    	// keine Ahnung, von eclipse aus gehts, aus der Konsole bekommt er den XML stream nicht hin.. 
    	// weiß ich ehrlich gesagt nix.. könntest du nochmal fragen mache ich schnell
    	
    	// liegt an den abhängigkeiten die nicht ordentlich in die jar packt, muss über resources da iwie mit reingepackt werden
    	// was muss da reingepackt werden? die abhängigkeiten.  A7b prperties wir haben keine properties mehr, ja aber egal meite er...
    	 // /a/lso nicht die config prperties
    	// also er meinte das einfach übernehmen, aber ka ob der das jetzt mit in die jar gepackt hat :/ 
    	// sollte er eigentlich schon, aber ich hab eigentlich alle Zugriffe auf diese komische properties aus der Board-klasse rausgeworfen..
    	
    	
    	// Load properties
//        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/truststore.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "pwgen");
//        Properties properties = new Properties();
        //properties.load(GameClient.class.getResourceAsStream(PATH_TO_PROPERTIES)); // hier ist das Problem
        //String serverIP = properties.getProperty("SERVER_IP");
        
        // Build socket
        SocketFactory Socketfactory = SocketFactory.getDefault();
        Socket socket = Socketfactory.createSocket(serverIP, serverPort);

        // hm also hier gehts weiterhin.. ich würde jetzt normalerweise versuchen, auf die Anwendung,
        // die aus der Konsole gestartet wurde, zuzugreifen, aber ich weiß nicht 100%tig wie das geht
        // ich probier s mal
        // habe jetzt numerik übung, bin also jetzt abgelenkt :/
        // ahso ok, du könntest das auch kurz pushen, dann probier ichs hier mal
        //mache ich 
        
        // Start client
        GameClient client = new GameClient(socket);
        ServerListener clientThread = new ServerListener(socket); 
        clientThread.start();
        client.start();

        scanner.close();
    }

    // hast du ne ahnung?
    // seltsam..
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void start() {
        login();

        while (isRunning()) {
            // keeps client alive
        }
    }

    private void login() {
        MazeCom mc_login = objectFactory.createMazeCom();
        LoginMessageType login = objectFactory.createLoginMessageType();
        mc_login.setMcType(MazeComType.LOGIN);
//        login.setName(GROUPNAME);
        String groupName = UUID.randomUUID().toString();
        System.out.println("Playing as " + groupName);
        login.setName(groupName);
        mc_login.setLoginMessage(login);
        outToServer.write(mc_login);
    }

    static void awaitMoveCallBack(MazeCom oldSituation) {
        outToServer.write(Ki.calculateTurn(oldSituation));
    }


    static void exitCallBack() {
        System.exit(0);
    }

    static boolean isRunning() {
        return running;
    }
}