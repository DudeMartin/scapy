package org.scapy.core.accessors;

public interface ICacheableNode extends INode {

    ICacheableNode getNext();

    ICacheableNode getPrevious();
}