package java.ru.crevan.loginserver.network.exception;

public class HackingException extends Exception {
    private final String ip;

    public HackingException(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
