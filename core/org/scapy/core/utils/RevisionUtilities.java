package org.scapy.core.utils;

import org.scapy.utils.RandomUtilities;
import org.scapy.utils.WorldUtilities;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

public final class RevisionUtilities {

    private RevisionUtilities() {

    }

    public static boolean checkRevision(int revision) throws IOException {
        String host = new URL(WorldUtilities.getAddress(RandomUtilities.randomElement(WorldUtilities.getWorlds()).number)).getHost();
        try (Socket socket = new Socket(host, 43594)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(15);
            out.writeInt(revision);
            return socket.getInputStream().read() == 0;
        }
    }
}