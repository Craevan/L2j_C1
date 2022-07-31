package java.ru.crevan.loginserver;

import java.net.Socket;
import java.ru.crevan.loginserver.network.Connection;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    private final Map<String, Integer> logins = new HashMap<>();
    private final Map<String, Connection> accountsInGameServer = new HashMap<>();
    private final Map<String, Socket> accountsInLoginServer;
    private final Map<String, Integer> accessLvl;
    private int maxAllowedPlayers;

    private LoginController() {
        this.accountsInLoginServer = new HashMap<>();
        this.accessLvl = new HashMap<>();
    }

    public static LoginController getInstance() {
        return LoginControllerHolder.HOLDER_INSTANCE;
    }

    private static class LoginControllerHolder {
        private static final LoginController HOLDER_INSTANCE = new LoginController();
    }

    public int assignSessionKeyToLogin(final String account, final int accessLvl, final Socket socket) {
        int key = (int) System.currentTimeMillis() & 0xFFFFFF;
        logins.put(account, key);
        accountsInLoginServer.put(account, socket);
        this.accessLvl.put(account, accessLvl);
        return key;
    }

    public void addGameServerLogin(final String account, final Connection connection) {
        accountsInGameServer.put(account, connection);
    }

    public void removeGameServerLogin(final String account) {
        if (account != null) {
            logins.remove(account);
            accountsInGameServer.remove(account);
        }
    }

    public void removeLoginServerLogin(final String account) {
        if (account != null) {
            accountsInLoginServer.remove(account);
        }
    }

    public boolean isAccountInLoginServer(final String account) {
        return accountsInLoginServer.containsKey(account);
    }

    public boolean isAccountInGameServer(final String account) {
        return accountsInGameServer.containsKey(account);
    }

    public int getKeyForAccount(final String account) {
        final Integer result = logins.get(account);
        return result != null ? result : 0;
    }

    public int getOnlinePlayerCount() {
        return accountsInGameServer.size();
    }

    public int getMaxAllowedOnlinePlayers() {
        return maxAllowedPlayers;
    }

    public void setMaxAllowedPlayers(final int maxAllowedPlayers) {
        this.maxAllowedPlayers = maxAllowedPlayers;
    }

    public boolean isLoginPossible(final int accessLvl) {
        return (accountsInGameServer.size() < maxAllowedPlayers) || (accessLvl >= 50);
    }

    public int getGmAccessLevel(final String login) {
        return accessLvl.get(login);
    }

    public Connection getClientConnection(final String login) {
        return accountsInGameServer.get(login);
    }

    public Socket getLoginServerConnection(final String login) {
        return accountsInLoginServer.get(login);
    }
}
