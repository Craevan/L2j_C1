package java.ru.crevan.loginserver;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(LoginServer.class.getName());
    public static void main(String[] args) throws IOException {
        LoginServer loginServer = new LoginServer();
        logger.config("LoginServer listening on port 2106");
        loginServer.start();
    }
}
