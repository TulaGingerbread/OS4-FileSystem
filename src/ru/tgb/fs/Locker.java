package ru.tgb.fs;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class Locker {
    private static AtomicIntegerArray ticket = new AtomicIntegerArray(1000);
    private static AtomicIntegerArray choosing = new AtomicIntegerArray(1000);

    private Locker() {}

    public static void lock(long t) {
        int tId = (int) t;
        choosing.set(tId, 1);
        ticket.set(tId, nextTicket());
        choosing.set(tId, 0);
        for (int i = 0; i < ticket.length(); i++) {
            if (i != tId) {
                while (choosing.get(i) == 1) { Thread.yield(); }
                while (ticket.get(i) != 0 &&
                        (ticket.get(tId) > ticket.get(i) ||
                                (ticket.get(tId) == ticket.get(i) && tId > i))) {
                    Thread.yield();
                }
            }
        }
    }

    public static void unlock(long tId) {
        ticket.set((int) tId, 0);
    }

    private static Integer nextTicket() {
        int max = 0;
        for (int i = 0; i < ticket.length(); i++) {
            if (ticket.get(i) > max) max = ticket.get(i);
        }
        return max + 1;
    }
}
