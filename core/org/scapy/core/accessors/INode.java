package org.scapy.core.accessors;

public interface INode {

    INode getNext();

    INode getPrevious();

    long getUid();
}