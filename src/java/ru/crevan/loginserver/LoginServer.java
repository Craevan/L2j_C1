package java.ru.crevan.loginserver;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.ru.crevan.Config;
import java.ru.crevan.loginserver.data.AccountData;
import java.ru.crevan.loginserver.network.ClientThread;
import java.util.logging.Logger;

public class LoginServer extends Thread {

    private final static Logger logger = Logger.getLogger(LoginServer.class.getName());

    private final ServerSocket serverSocket;
    private String ip;
    private final AccountData accounts;
    private final int gamePort;

    public LoginServer() throws IOException {
        super("LoginServer");
        if (!"*".equals(Config.getLoginHostName())) {
            final InetAddress address = InetAddress.getByName(Config.getLoginHostName());
            this.ip = address.getHostAddress();
            LoginServer.logger.config("LoginServer listening on IP: " + this.ip + " port [2106]");
            this.serverSocket = new ServerSocket(2106, 50, address);
        } else {
            LoginServer.logger.config("LoginServer listening on all available IPs on port [2106]");
            this.serverSocket = new ServerSocket(2106);
        }
        LoginServer.logger.config("Hostname for external connections is: " + Config.getExternalHostName());
        LoginServer.logger.config("Hostname for internal connections is: " + Config.getInternalHostName());
        this.accounts = new AccountData(Config.getAutoCreateAccounts());
        this.gamePort = Config.getServerPort();
        try {
            final File bannedUsers = new File("banned_ip.cfg");
            if (bannedUsers.exists() && bannedUsers.isFile()) {
                int count = 0;
                final LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(bannedUsers)));
                String line;
                while ((line = lnr.readLine()) != null) {
                    if ((line = line.trim()).length() <= 0) {
                        continue;
                    }
                    ++count;
                    ClientThread.addBannedIp(line);
                }
                lnr.close();
                LoginServer.logger.info(count + " banned IPs defined");
            } else {
                LoginServer.logger.info("banned_ip.cfg not found");
            }
        } catch (Exception ex) {
            LoginServer.logger.warning("Error while reading banned_ip.cfg file: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        do {
            try {
                do {
                    final Socket connection = serverSocket.accept();
                    final String connectedIp = connection.getInetAddress().getHostAddress();
                    if (connectedIp.startsWith("192.168") || connectedIp.startsWith("10.")) {
                        new ClientThread(connection, accounts, Config.getInternalHostName(), gamePort);
                        continue;
                    }
                    new ClientThread(connection, accounts, Config.getExternalHostName(), gamePort);
                }
                while (true);
            } catch (IOException ioe) {
                LoginServer.logger.warning(ioe.getMessage());
            }
        }
        while (true);
    }
}
