package java.ru.crevan.loginserver.data;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountData {

    private static final Map<String, Integer> accessLevels = new ConcurrentHashMap<>();
    //stub
    public AccountData(final boolean autoCreateAccounts) {
    }

    public int getAccessLevel(final String account) {
        return accessLevels.get(account);
    }


    public boolean loginValid(final String account, final String password, final InetAddress inetAddress) {
        // TODO
        return false;
    }
}
