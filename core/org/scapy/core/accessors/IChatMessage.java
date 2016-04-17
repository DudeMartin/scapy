package org.scapy.core.accessors;

public interface IChatMessage extends ICacheableNode {

    int getChannel();

    String getMessage();

    String getSender();
}