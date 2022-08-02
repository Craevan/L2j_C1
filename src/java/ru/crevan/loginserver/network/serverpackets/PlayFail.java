package java.ru.crevan.loginserver.network.serverpackets;

public class PlayFail extends ServerBasePacket {

    public static final int REASON_TOO_MANY_PLAYERS = 15;
    public static final int REASON1 = 1;
    public static final int REASON2 = 2;
    public static final int REASON3 = 3;
    public static final int REASON4 = 4;

    private final int reason;

    public PlayFail(int reason) {
        this.reason = reason;
    }

    @Override
    public void writeImpl() {
        writeC(0x06);
        writeC(reason);
    }
}
