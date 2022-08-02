package java.ru.crevan.loginserver.data;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.ru.crevan.Config;
import java.ru.crevan.loginserver.network.exception.HackingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        logger.config("Auto creating new accounts: " + autoCreateAccounts);
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

    public boolean loginValid(final String login, final String password, final InetAddress inetAddress) throws HackingException {
        boolean result;
        final Integer failedConnects = hackProtection.get(inetAddress.getHostAddress());
        if (failedConnects != null && failedConnects > 2) {
            logger.warning("Hacking detection from ip: " + inetAddress.getHostAddress() + ".. adding IP to banList.");
            throw new HackingException(inetAddress.getHostAddress());
        }
        try {
            final MessageDigest md = MessageDigest.getInstance(SHA);
            final byte[] raw = password.getBytes(StandardCharsets.UTF_8);
            final byte[] hash = md.digest(raw);
            final byte[] expected = logPass.get(login);
            if (expected == null) {
                if (Config.getAutoCreateAccounts()) {
                    logPass.put(login, hash);
                    accessLevels.put(login, 0);
                    logger.info("Created new account for " + login);
                    saveToDisc();
                    return true;
                }
                logger.warning("Account missing for login: " + login);
                return false;
            }
            result = true;
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] == hash[i]) {
                    continue;
                }
                result = false;
                break;
            }
        } catch (NoSuchAlgorithmException algorithmException) {
            logger.warning("[ERROR] no such algorithm");
            result = false;
        } catch (Exception ex) {
            logger.warning("Could not check password: " + ex.getMessage());
            result = false;
        }
        if (!result) {
            int failedCount = 1;
            if (failedConnects != null) {
                failedCount = failedConnects + 1;
            }
            hackProtection.put(inetAddress.getHostAddress(), failedCount);
        } else {
            hackProtection.remove(inetAddress.getHostAddress());
        }
        return result;
    }

    private void readFromDisc(final File file) throws IOException {
        logPass.clear();
        int i = 0;
        String line;
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

    private void saveToDisc() {
        try {
            final FileWriter fileWriter = new FileWriter("data/accounts.txt");
            for (Map.Entry<String, byte[]> entry : logPass.entrySet()) {
                final String name = entry.getKey();
                fileWriter.write(name);
                fileWriter.write("\t");
                fileWriter.write(Base64.getEncoder().encodeToString(entry.getValue()));
                fileWriter.write("\t");
                fileWriter.write("" + accessLevels.get(name));
                fileWriter.write("\r\n");
            }
            fileWriter.close();
        } catch (IOException ioe) {
            logger.warning("Could not store accounts file. " + ioe.getMessage());
        }
    }
}
