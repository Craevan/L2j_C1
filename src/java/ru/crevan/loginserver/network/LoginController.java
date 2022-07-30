package java.ru.crevan.loginserver.network;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    private final Map<String, Integer> logins = new HashMap<>();
    private final Map<String, Connection> accountinGameServer = new HashMap<>();
    private final Map<String, Socket> accountsinloginServer;
    private final Map<String, Integer> accessLvl;
    private int maxAllowedPlayers;

    private LoginController() {
        this.accountsinloginServer = new HashMap<>();
        this.accessLvl = new HashMap<>();
    }

    public static LoginController getInstance() {
        return LoginControllerHolder.HOLDER_INSTANCE;
    }

    private static class LoginControllerHolder {
        private static final LoginController HOLDER_INSTANCE = new LoginController();
    }
}
