package client;

import generated.LoginMessageType;
import generated.MazeCom;
import generated.MazeComType;
import generated.ObjectFactory;
import network.XmlOutStream;

import javax.net.SocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public final class GameClient {

    private static final String GROUPNAME = "4 und ein Keks Destroyer";
    private XmlOutStream outToServer;
    private static Scanner scanner = new Scanner(System.in);
    private BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    private static boolean running = true;
    private static Socket socket;
    private static ObjectFactory objectFactory = new ObjectFactory();
    private static int playerID;
    private static String PATH_TO_PROPERTIES = "/config.properties";

    private GameClient(Socket socket) throws IOException {
        GameClient.socket = socket;
        outToServer = new XmlOutStream(GameClient.socket.getOutputStream());
    }

    public static void main(String[] args) throws Throwable {

        // Load properties
//        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/truststore.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "pwgen");
        Properties properties = new Properties();
        properties.load(GameClient.class.getResourceAsStream(PATH_TO_PROPERTIES));
        String serverIP = properties.getProperty("SERVER_IP");
        int serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));

        // Build socket
        SocketFactory Socketfactory = SocketFactory.getDefault();
        Socket socket = Socketfactory.createSocket(serverIP, serverPort);

        // Start client
        GameClient client = new GameClient(socket);
        ServerListener clientThread = new ServerListener(socket);
        clientThread.start();
        client.start();

        scanner.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void start() {
        login();

        while (isRunning()) {
            // keeps client alive
        }
    }

    static void setPlayerID(int playerID) {
        GameClient.playerID = playerID;
    }

    private void login() {
        MazeCom mc_login = objectFactory.createMazeCom();
        LoginMessageType login = objectFactory.createLoginMessageType();
        mc_login.setMcType(MazeComType.LOGIN);
        login.setName(GROUPNAME);
        mc_login.setLoginMessage(login);
        outToServer.write(mc_login);
    }

    static void awaitMoveCallBack(MazeCom oldSituation) {
        // TODO
    }


    static void exitCallBack() {
        System.exit(0);
    }

    static boolean isRunning() {
        return running;
    }
}