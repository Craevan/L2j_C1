package java.ru.crevan.loginserver.network;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.ru.crevan.loginserver.LoginController;
import java.ru.crevan.loginserver.data.AccountData;
import java.ru.crevan.loginserver.network.clientpackets.RequestAuthLogin;
import java.ru.crevan.loginserver.network.exception.HackingException;
import java.ru.crevan.loginserver.network.serverpackets.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ClientThread extends Thread {

    private static final Logger logger = Logger.getLogger(ClientThread.class.getName());
    private final InputStream in;
    private final OutputStream out;
    private final NewCrypt crypt;
    private final AccountData logins;
    private final Socket socket;
    private final String gameServerHost;
    private final int gameServerPort;
    private static List<String> bannedIPs = new ArrayList<>();

    public ClientThread(final Socket client, final AccountData logins, final String host, final int port) throws IOException {
        this.socket = client;
        final String ip = client.getInetAddress().getHostAddress();
        if (bannedIPs.contains(ip)) {
            throw new IOException("banned IP");
        }
        this.in = client.getInputStream();
        this.out = new BufferedOutputStream(client.getOutputStream());
        this.crypt = new NewCrypt("[;'.]94-31==-%&@!^+]\u0000");
        this.logins = logins;
        this.gameServerHost = host;
        this.gameServerPort = port;
        start();
    }

    public static void addBannedIp(final String ip) {
        bannedIPs.add(ip);
    }

    private void sendPacket(final ServerBasePacket packet) throws IOException {
        packet.writeImpl();
        byte[] data = packet.getBytes();
        crypt.checksum(data);
        data = crypt.crypt(data);
        final int length = data.length + 2;
        out.write(length & 0xFF);
        out.write((length >> 8) & 0xFF);
        out.write(data);
        out.flush();
    }

    @Override
    public void run() {
        int lengthHi = 0;
        int lengthLo = 0;
        int length = 0;

        @SuppressWarnings("unused")
        boolean checksumOk = false;
        int sessionKey = -1;
        String account = null;
        String gameServerIp = null;
        try {
            final InetAddress address = InetAddress.getByName(gameServerHost);
            gameServerIp = address.getHostAddress();
            final Init startPacket = new Init();
            out.write(startPacket.getLength() & 255);
            out.write((startPacket.getLength() >> 8) & 255);
            out.write(startPacket.getContent());
            out.flush();
            do {
                lengthLo = in.read();
                lengthHi = in.read();
                length = (lengthHi * 256) + lengthLo;
                if (lengthHi < 0) {
                    break;
                }
                final byte[] incomming = new byte[length];
                incomming[0] = (byte) lengthLo;
                incomming[1] = (byte) lengthHi;
                int newBytes = 0;
                int receivedBytes;
                for (receivedBytes = 0; (newBytes != -1) && (receivedBytes < (length - 2)); receivedBytes += newBytes) {
                    newBytes = in.read(incomming, 2, length - 2);
                }
                if (receivedBytes != length - 2) {
                    logger.warning("Incomplete Packet is sent to the server, closing connection.");
                    break;
                }
                byte[] decrypt = new byte[length - 2];
                System.arraycopy(incomming, 2, decrypt, 0, decrypt.length);
                decrypt = crypt.decrypt(decrypt);
                checksumOk = crypt.checksum(decrypt);
                final int packetType = decrypt[0] & 255;
                switch (packetType) {
                    case 0x00: {
                        final RequestAuthLogin ral = new RequestAuthLogin(decrypt);
                        account = ral.getUser().toLowerCase();
                        final LoginController loginController = LoginController.getInstance();
                        if (logins.loginValid(account, ral.getPassword(), socket.getInetAddress())) {
                            if (!loginController.isAccountInGameServer(account) && !loginController.isAccountInLoginServer(account)) {
                                final int accessLevel = logins.getAccessLevel(account);
                                if (accessLevel < 0) {
                                    final LoginFail lok = new LoginFail(LoginFail.REASON_ACCOUNT_BANNED);
                                    sendPacket(lok);
                                    break;
                                }
                                sessionKey = loginController.assignSessionKeyToLogin(account, accessLevel, socket);
                                final LoginOk lok = new LoginOk();
                                sendPacket(lok);
                                break;
                            }
                            if (loginController.isAccountInLoginServer(account)) {
                                // _log.warning("Account is in use on Login server (kicking off):" + account);
                                loginController.getLoginServerConnection(account).close();
                                loginController.removeLoginServerLogin(account);
                            }
                            if (loginController.isAccountInGameServer(account)) {
                                // _log.warning("Account is in use on Game server (kicking off):" + account);
                                loginController.getClientConnection(account).sendPacket(new LeaveWorld());
                                loginController.getClientConnection(account).close();
                                loginController.removeGameServerLogin(account);
                            }
                            final LoginFail lok = new LoginFail(LoginFail.REASON_ACCOUNT_IN_USE);
                            sendPacket(lok);
                            break;
                        }
                        final LoginFail lok = new LoginFail(LoginFail.REASON_USER_OR_PASS_WRONG);
                        sendPacket(lok);
                        break;
                    }
                    case 0x02: {
                        // RequestServerLogin rsl = new RequestServerLogin(decrypt);
                        final PlayOk po = new PlayOk(sessionKey);
                        sendPacket(po);
                        break;
                    }
                    case 0x05: {
                        // RequestServerList rsl = new RequestServerList(decrypt);
                        final ServerList sl = new ServerList();
                        final int current = LoginController.getInstance().getOnlinePlayerCount();
                        final int max = LoginController.getInstance().getMaxAllowedOnlinePlayers();
                        sl.addServer(gameServerIp, gameServerPort, true, false, current, max);
                        sendPacket(sl);
                        break;
                    }
                }
            }
            while (true);
            try {
                socket.close();
            } catch (IOException e) {
                logger.warning("Exception when socket closing");
            }
            LoginController.getInstance().removeLoginServerLogin(account);
        } catch (HackingException hackingException) {
            ClientThread.bannedIPs.add(hackingException.getIp());
            try {
                socket.close();
            } catch (IOException e) {
                logger.warning("Exception when socket closing");
            }
            LoginController.getInstance().removeLoginServerLogin(account);
        } catch (Exception ex) {
            try {
                socket.close();
            } catch (Exception ignore) {
            }
            LoginController.getInstance().removeLoginServerLogin(account);
        }
    }
}
