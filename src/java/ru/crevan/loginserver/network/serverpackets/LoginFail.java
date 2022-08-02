package java.ru.crevan.loginserver.network.serverpackets;

public class LoginFail extends ServerBasePacket {
    public static int REASON_ACCOUNT_BANNED = 9;
    public static int REASON_ACCOUNT_IN_USE = 7;
    public static int REASON_ACCESS_FAILED = 4;
    public static int REASON_USER_OR_PASS_WRONG = 3;
    public static int REASON_PASS_WRONG = 2;
    public static int REASON_SYSTEM_ERROR = 1;

    private final int reason;

    public LoginFail(int reason) {
        this.reason = reason;
    }

    @Override
    public void writeImpl() {
        writeC(0x01);
        writeD(reason);
    }
}
