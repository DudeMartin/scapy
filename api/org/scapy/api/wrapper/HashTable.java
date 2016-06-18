package org.scapy.api.wrapper;

import org.scapy.core.accessors.IHashTable;
import org.scapy.core.accessors.INode;

public class HashTable extends AbstractWrapper<IHashTable> {

    private int index;
    private INode current;

    public HashTable(IHashTable accessor) {
        super(accessor);
    }

    public INode getNext() {
        INode[] buckets = accessor().getBuckets();
        if (index > 0 && buckets[index - 1] != current) {
            INode node = current;
            current = node.getPrevious();
            return node;
        }
        while (index < buckets.length) {
            INode node = buckets[index++].getPrevious();
            if (buckets[index - 1] != node) {
                current = node.getPrevious();
                return node;
            }
        }
        return null;
    }

    public INode getFirst() {
        index = 0;
        return getNext();
    }
}