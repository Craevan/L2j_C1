package java.ru.crevan.loginserver.data;

import java.io.*;
import java.net.InetAddress;
import java.ru.crevan.Config;
import java.util.Base64;
import java.util.Map;
import java.util.StringTokenizer;
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
        logPass.clear();
        int i = 0;
        String line = null;
        final LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file)));
        while ((line = lnr.readLine()) != null) {
            final StringTokenizer tokenizer = new StringTokenizer(line, "\t\n\r");
            if (!tokenizer.hasMoreTokens()) {
                continue;
            }
            final String name = tokenizer.nextToken().toLowerCase();
            final String password = tokenizer.nextToken();
            logPass.put(name, Base64.getDecoder().decode(password));
            if (tokenizer.hasMoreTokens()) {
                final String access = tokenizer.nextToken();
                final Integer level = Integer.parseInt(access);
                accessLevels.put(access, level);
            } else {
                accessLevels.put(name, 0);
            }
            ++i;
        }
        lnr.close();
        logger.config("Found " + i + " accounts on disk.");
    }
}
