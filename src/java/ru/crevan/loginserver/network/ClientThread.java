package java.ru.crevan.loginserver.network;

import java.net.Socket;
import java.ru.crevan.loginserver.data.AccountData;
import java.util.logging.Logger;

public class ClientThread extends Thread {
    //stub
    private static final Logger logger = Logger.getLogger(ClientThread.class.getName());

    public static void addBannedIp(final String line) {
        //todo
    }

    public ClientThread(final Socket socket, final AccountData accounts, final String host, final int port) {
    }

    @Override
    public void run() {
        //TODO
    }
}
