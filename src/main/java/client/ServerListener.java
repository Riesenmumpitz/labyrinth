package client;

import generated.MazeCom;
import network.XmlInStream;
import network.XmlOutStream;

import javax.xml.bind.UnmarshalException;
import java.io.IOException;
import java.net.Socket;

public class ServerListener extends Thread {

    private Socket socket;
    private static XmlInStream fromServer;

    public ServerListener(Socket socket) throws IOException {
        this.socket = socket;
        fromServer = new XmlInStream(this.socket.getInputStream());
    }

    @Override
    public void run() {
        while (GameClient.isRunning()) {
            MazeCom message = waitForMessage();
            processReceivedMessage(message);
        }
    }

    //TODO remove
    private XmlOutStream systemOut = new XmlOutStream(System.out);

    public void processReceivedMessage(MazeCom msg) {
        if (msg == null) {
            return;
        }

        systemOut.write(msg);

        switch (msg.getMcType()) {

            case LOGINREPLY:
                System.out.println("Server let us log in");
                GameClient.setPlayerID(msg.getLoginReplyMessage().getNewID());
                break;
            case AWAITMOVE:
                System.out.println("Server awaits move");
                GameClient.awaitMoveCallBack(msg);
                break;
            case ACCEPT:
                System.out.println("Server says ok. :o)");
                break;
            case WIN:
                System.out.println("Somebody has won. Exiting...");
                System.exit(0);
                break;
            case DISCONNECT:
                System.out.println("Lost Connection to the server. Exiting...");
                GameClient.exitCallBack();
                break;
        }
    }

    public MazeCom waitForMessage() {
        try {
            return fromServer.readMazeCom();
        } catch (UnmarshalException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}