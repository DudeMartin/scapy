package org.scapy.core.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public final class RevisionChecker {

    private RevisionChecker() {

    }

    public static boolean check(int world, int revision) throws IOException {
        try (Socket socket = new Socket("oldschool" + world + ".runescape.com", 43594)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(15);
            out.writeInt(revision);
            return socket.getInputStream().read() == 0;
        }
    }
}