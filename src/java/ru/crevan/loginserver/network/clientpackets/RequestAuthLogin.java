package java.ru.crevan.loginserver.network.clientpackets;

public class RequestAuthLogin {

    private final String user;
    private final String password;

    public RequestAuthLogin(final byte[] rowPacket) {
        this.user = new String(rowPacket, 1, 14).trim().toLowerCase();
        this.password = new String(rowPacket, 15, 14).trim();
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
