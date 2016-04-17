package org.scapy.utils;

public final class Preconditions {

    private Preconditions() {

    }

    public static void check(boolean condition, String message, Class<? extends RuntimeException> type) {
        if (!condition) {
            RuntimeException ex;
            try {
                ex = type.getConstructor(String.class).newInstance(message);
            } catch (ReflectiveOperationException e) {
                try {
                    ex = type.newInstance();
                } catch (ReflectiveOperationException ignored) {
                    ex = new RuntimeException(message);
                }
            }
            StackTraceElement[] trace = ex.getStackTrace();
            int i;
            for (i = 0; i < trace.length; i++) {
                if (!trace[i].getMethodName().contains("newInstance")) {
                    break;
                }
            }
            StackTraceElement[] newTrace = new StackTraceElement[trace.length - i];
            System.arraycopy(trace, i, newTrace, 0, newTrace.length);
            ex.setStackTrace(newTrace);
            throw ex;
        }
    }
}