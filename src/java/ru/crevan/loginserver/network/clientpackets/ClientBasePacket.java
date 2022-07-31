package java.ru.crevan.loginserver.network.clientpackets;

import java.nio.charset.StandardCharsets;

public class ClientBasePacket {

    private final byte[] decrypt;
    private int off;

    public ClientBasePacket(final byte[] decrypt) {
        this.decrypt = decrypt;
        this.off = 1;
    }

    public int readC() {
        return decrypt[off++] & 0xFF;
    }

    public int readD() {
        int result = decrypt[off++] & 0xFF;
        result |= (decrypt[off++] << 8) & 0xFF00;
        result |= (decrypt[off++] << 16) & 0xFF0000;
        return result |= (decrypt[off++] << 24) & 0xFF000000;
    }

    public int readH() {
        int result = decrypt[off++] & 0xFF;
        return result |= (decrypt[off++] << 8) & 0xFF00;
    }

    public double readF() {
        long result = decrypt[off++] & 0xFF;
        result |= (decrypt[off++] << 8) & 0xFF00;
        result |= (decrypt[off++] << 16) & 0xFF0000;
        result |= (decrypt[off++] << 24) & 0xFF000000;
        result |= (decrypt[off++] << 32) & 0xFF00000000L;
        result |= (decrypt[off++] << 40) & 0xFF0000000000L;
        result |= (decrypt[off++] << 48) & 0xFF000000000000L;
        return Double.longBitsToDouble(result |= (decrypt[off++] << 56) & 0xFF00000000000000L);
    }

    public String readS()
    {
        String result = null;
        try
        {
            result = new String(decrypt, off, decrypt.length - off, StandardCharsets.UTF_16LE);
            result = result.substring(0, result.indexOf(0));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (result != null)
        {
            off = (result.length() * 2) + 3;
        }
        return result;
    }
}
