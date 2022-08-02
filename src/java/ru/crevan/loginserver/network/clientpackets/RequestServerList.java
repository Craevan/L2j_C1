package java.ru.crevan.loginserver.network.clientpackets;

public class RequestServerList {

    private long data1;
    private long data2;
    private final int data3;

    public RequestServerList(final byte[] rawPacket) {
        this.data1 = rawPacket[1] & 0xFF;
        this.data1 |= (rawPacket[2] << 8) & 0xFF00;
        this.data1 |= (rawPacket[3] << 16) & 0xFF0000;
        this.data1 |= (rawPacket[4] << 24) & 0xFF000000;
        this.data2 = rawPacket[5] & 0xFF;
        this.data2 |= (rawPacket[6] << 8) & 0xFF00;
        this.data2 |= (rawPacket[7] << 16) & 0xFF0000;
        this.data2 |= (rawPacket[8] << 24) & 0xFF000000;
        this.data3 = rawPacket[9] & 0xFF;
    }

    public long getData1() {
        return data1;
    }

    public long getData2() {
        return data2;
    }

    public int getData3() {
        return data3;
    }
}
