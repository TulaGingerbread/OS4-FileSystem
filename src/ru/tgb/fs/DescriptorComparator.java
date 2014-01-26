package ru.tgb.fs;

import java.util.Comparator;

class DescriptorComparator implements Comparator<Descriptor> {

    @Override
    public int compare(Descriptor o1, Descriptor o2) {
        if (o1.getId() == o2.getId()) return 0;
        if (!(o1.isDirectory() ^ o2.isDirectory())) {
            return o1.getName().compareTo(o2.getName());
        }
        else {
            return o1.isDirectory() ? -1 : 1;
        }
    }
}
