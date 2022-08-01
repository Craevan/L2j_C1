package java.ru.crevan.loginserver.data;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.ru.crevan.Config;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class AccountData {

    private static final Logger logger = Logger.getLogger(AccountData.class.getName());

    private static final Map<String, Integer> accessLevels = new ConcurrentHashMap<>();
    private static final Map<String, Integer> hackProtection = new ConcurrentHashMap<>();
    private static final Map<String, byte[]> logPass = new ConcurrentHashMap<>();
    private static final String SHA = "SHA";

    public AccountData(final boolean autoCreateAccounts) {
        logger.config("Auto creating new accounts: " + Config.getAutoCreateAccounts());
        final File loginFile = new File("data/accounts.txt");
        if (loginFile.exists()) {
            try {
                readFromDisc(loginFile);
            } catch (IOException ioe) {
                logger.warning("[ERROR] reading data/accounts.txt file " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }
    }

    public int getAccessLevel(final String account) {
        return accessLevels.get(account);
    }


    public boolean loginValid(final String account, final String password, final InetAddress inetAddress) {
        // TODO
        return false;
    }

    private void readFromDisc(final File file) throws IOException {
        //TODO
    }
}
