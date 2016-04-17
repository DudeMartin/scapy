package org.scapy.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class StreamUtilities {

    private static final int BUFFER_SIZE = 4096;

    private StreamUtilities() {

    }

    public static byte[] readFully(InputStream in) throws IOException {
        byte[] transport = new byte[BUFFER_SIZE];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead;
        while ((bytesRead = in.read(transport)) >= 0) {
            out.write(transport, 0, bytesRead);
        }
        return out.toByteArray();
    }
}