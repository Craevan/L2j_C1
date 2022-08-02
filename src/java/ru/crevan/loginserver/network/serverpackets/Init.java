package java.ru.crevan.loginserver.network.serverpackets;

public class Init {
    private static final byte[] CONTENT = new byte[]{
            0, -100, 119, -19, 3, 90, 120, 0, 0
    };

    public byte[] getContent() {
        return CONTENT;
    }

    public int getLength() {
        return CONTENT.length + 2;
    }
}
