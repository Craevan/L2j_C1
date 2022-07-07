package java.ru.crevan;

import java.ru.crevan.utils.PropertiesParser;

public class Config {

    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static final String SERVER_CONFIG_FILE = "./config/server.ini";

    private static String loginHostName;
    private static String externalHostName;
    private static String internalHostName;
    private static boolean autoCreateAccounts;

    public static void load() {
        final PropertiesParser serverConfig = new PropertiesParser(SERVER_CONFIG_FILE);
        loginHostName = serverConfig.getString("LoginServerHostName", "*");
        externalHostName = serverConfig.getString("ExternalServerHostName", "127.0.0.1");
        if (externalHostName == null) {
            externalHostName = "localhost";
        }
        internalHostName = serverConfig.getString("InternalServerHostName", "127.0.0.1");
        if (internalHostName == null) {
            internalHostName = "localhost";
        }
        autoCreateAccounts = serverConfig.getBoolean("AutoCreateAccounts", true);
    }

    public static String getLoginHostName() {
        return loginHostName;
    }

    public static String getExternalHostName() {
        return externalHostName;
    }

    public static String getInternalHostName() {
        return internalHostName;
    }

    public static boolean getAutoCreateAccounts() {
        return autoCreateAccounts;
    }

    public static int getServerPort() {
        //todo
        return 0;
    }
}
