package client;

import generated.LoginMessageType;
import generated.MazeCom;
import generated.MazeComType;
import generated.ObjectFactory;
import ki.Ki;
import network.XmlOutStream;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public final class GameClient {

    private static final String GROUPNAME = "4 und ein Keks Destroyer";
    private static XmlOutStream outToServer;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean running = true;
    private static Socket socket;
    private static ObjectFactory objectFactory = new ObjectFactory();

    private GameClient(Socket socket) throws IOException {
        GameClient.socket = socket;
        outToServer = new XmlOutStream(GameClient.socket.getOutputStream());
    }

    public static void main(String[] args) throws Throwable {
    	String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);

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

    private void login() {
        MazeCom mc_login = objectFactory.createMazeCom();
        LoginMessageType login = objectFactory.createLoginMessageType();
        mc_login.setMcType(MazeComType.LOGIN);
        login.setName(GROUPNAME);
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