package java.ru.crevan.loginserver.network.serverpackets;

public class PlayOk extends ServerBasePacket {
    private final int sessionKey;

    public PlayOk(int sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public void writeImpl() {
        writeC(0x07);
        writeD(sessionKey);
        writeD(1432778632);
    }
}
