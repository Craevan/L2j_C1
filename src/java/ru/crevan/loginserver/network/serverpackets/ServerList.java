package java.ru.crevan.loginserver.network.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ServerList extends ServerBasePacket {

    private final List<ServerData> servers = new ArrayList<>();

    public void addServer(final String ip, final int port, final boolean pvp,
                          final boolean testServer, final int currentPlayer, final int maxPlayer) {
        servers.add(new ServerData(ip, port, pvp, currentPlayer, maxPlayer, testServer));
    }

    @Override
    public void writeImpl() {
        writeC(0x04);
        writeC(servers.size());
        writeC(0);
        for (int i = 0; i < servers.size(); ++i) {
            final ServerData server = servers.get(i);
            writeC(i + 1);
            try {
                final InetAddress i4 = InetAddress.getByName(server.ip);
                final byte[] raw = i4.getAddress();
                writeC(raw[0] & 0xFF);
                writeC(raw[1] & 0xFF);
                writeC(raw[2] & 0xFF);
                writeC(raw[3] & 0xFF);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                writeC(127);
                writeC(0);
                writeC(0);
                writeC(1);
            }
            writeD(server.port);
            writeC(15);
            if (server.pvp) {
                writeC(1);
            } else {
                writeC(0);
            }
            writeH(server.currentPlayers);
            writeH(server.maxPlayers);
            writeC(1);
            if (server.testServer) {
                writeD(4);
                continue;
            }
            writeD(0);
        }
    }

    private record ServerData(String ip, int port, boolean pvp, int currentPlayers, int maxPlayers, boolean testServer) {
    }
}
