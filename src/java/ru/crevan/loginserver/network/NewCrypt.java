package java.ru.crevan.loginserver.network;

import java.io.IOException;

public class NewCrypt {

    BlowfishEngine crypt;
    BlowfishEngine decrypt;

    public NewCrypt(final String key) {
        final byte[] keyBytes = key.getBytes();
        this.crypt = new BlowfishEngine();
        this.decrypt = new BlowfishEngine();
        this.crypt.init(true, keyBytes);
        this.decrypt.init(false, keyBytes);
    }

    public boolean checksum(final byte[] raw) {
        long ecx;
        long checkSum = 0L;
        final int count = raw.length - 8;
        int i;
        for (i = 0; i < count; i++) {
            ecx = raw[i] & 0xFF;
            ecx |= (raw[i + 1] << 8) & 0xFF00;
            ecx |= (raw[i + 2] << 16) & 0xFF0000;
            checkSum ^= (ecx |= (raw[i + 3] << 24) & 0xFF000000);
        }
        ecx = raw[i] & 0xFF;
        ecx |= (raw[i + 1] << 8) & 0xFF00;
        ecx |= (raw[i + 2] << 16) & 0xFF0000;
        raw[i] = (byte) (checkSum & 0xFFL);
        raw[i + 1] = (byte) ((checkSum >> 8) & 0xFFL);
        raw[i + 2] = (byte) ((checkSum >> 16) & 0xFFL);
        raw[i + 3] = (byte) ((checkSum >> 24) & 0xFFL);
        return (ecx |= (raw[i + 3] << 24) & 0xFF000000) == checkSum;
    }

    public byte[] decrypt(final byte[] raw) throws IOException {
        final byte[] result = new byte[raw.length];
        final int count = raw.length / 8;
        for (int i = 0; i < count; ++i) {
            this.decrypt.processBlock(raw, i * 8, result, i * 8);
        }
        return result;
    }

    public byte[] crypt(final byte[] raw) throws IOException {
        final int count = raw.length / 8;
        final byte[] result = new byte[raw.length];
        for (int i = 0; i < count; ++i) {
            this.crypt.processBlock(raw, i * 8, result, i * 8);
        }
        return result;
    }
}
