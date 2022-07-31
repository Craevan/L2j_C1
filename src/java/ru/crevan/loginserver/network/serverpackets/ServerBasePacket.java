package java.ru.crevan.loginserver.network.serverpackets;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public abstract class ServerBasePacket {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public int getLength() {
        return baos.size() + 2;
    }

    public byte[] getBytes() {
        writeD(0);
        final int padding = baos.size() % 8;
        if (padding != 0) {
            for (int i = 0; i < 8; i++) {
                writeC(0);
            }
        }
        return baos.toByteArray();
    }

    public abstract void writeImpl();

    protected void writeC(final int value)
    {
        baos.write(value & 0xFF);
    }

    protected void writeD(final int value) {
        baos.write(value & 0xFF);
        baos.write((value >> 8) & 0xFF);
        baos.write((value >> 16) & 0xFF);
        baos.write((value >> 24) & 0xFF);
    }

    protected void writeH(final int value) {
        baos.write(value & 0xFF);
        baos.write((value >> 8) & 0xFF);
    }

    protected void writeF(final double org) {
        final long value = Double.doubleToRawLongBits(org);
        baos.write((int) (value & 0xFFL));
        baos.write((int) ((value >> 8) & 0xFFL));
        baos.write((int) ((value >> 16) & 0xFFL));
        baos.write((int) ((value >> 24) & 0xFFL));
        baos.write((int) ((value >> 32) & 0xFFL));
        baos.write((int) ((value >> 40) & 0xFFL));
        baos.write((int) ((value >> 48) & 0xFFL));
        baos.write((int) ((value >> 56) & 0xFFL));
    }

    protected void writeS(final String text) {
        try {
            if (text != null) {
                baos.write(text.getBytes(StandardCharsets.UTF_16LE));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        baos.write(0);
        baos.write(0);
    }
}
