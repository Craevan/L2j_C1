package java.ru.crevan.loginserver.network.serverpackets;

public class LoginOk extends ServerBasePacket {
    @Override
    public void writeImpl() {
        writeC(0x03);
        writeD(1431655765);
        writeD(1145324612);
        writeD(0);
        writeD(0);
        writeD(1002);
        writeD(0);
        writeD(0);
        writeD(2);
    }
}
