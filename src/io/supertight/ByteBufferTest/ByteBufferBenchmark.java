package io.supertight.ByteBufferTest;

import java.nio.ByteBuffer;
import java.util.Random;

public class ByteBufferBenchmark {

    public static void main(String[] args) {
        int runCount = 10;
        for (int i = 0; i < runCount; i++) {
            runBenchmark(1000 * (i + 1), 100000 * (i + 1));
        }
    }

    private static void runBenchmark(int runCount, int byteLength) {
        // Store results in array.
        long[] loopResults = new long[runCount];
        long[] bulkResults = new long[runCount];

        // Generate buffer of random bytes.
        ByteBuffer buf = randomBuffer(byteLength);

        int position = 20;
        int length = (int)Math.floor(byteLength * 0.8);
        byte[] bytesToWrite = randomBytes(length);

        for (int i = 0; i < runCount; i++) {
            loopResults[i] = runLoopBenchmark(buf, bytesToWrite, position, length);
            bulkResults[i] = runBulkBenchmark(buf, bytesToWrite, position);
        }

        // Generate average results.
        long avgLoopResult = average(loopResults);
        long avgBulkResult = average(bulkResults);
        float avgSpeedFactor = (float) avgLoopResult / avgBulkResult;
        System.out.println("Modified "+length+" bytes. Showing average of "+runCount+" runs:");
        System.out.println("Average loop result: "+avgLoopResult);
        System.out.println("Average bulk result: "+ avgBulkResult);
        System.out.println("Bulk method was "+avgSpeedFactor+"x faster than the loop method");
        System.out.println("========================================");
    }

    private static ByteBuffer randomBuffer(int length) {
        return ByteBuffer.wrap(randomBytes(length));
    }

    private static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        new Random().nextBytes(bytes);
        return bytes;
    }

    private static long runLoopBenchmark(ByteBuffer buf, byte[] bytesToWrite, int position, int length) {
        // Run loop benchmark.
        long start = System.nanoTime();
        for (int i = 0; i < length; i++) {
            buf.put(position + i, bytesToWrite[i]);
        }
        // Return duration.
        return System.nanoTime() - start;
    }

    private static long runBulkBenchmark(ByteBuffer buf, byte[] bytesToWrite, int position) {
        // Run bulk benchmark.
        long start = System.nanoTime();
        ByteBuffer copy = buf.duplicate();
        copy.position(position);
        copy.put(bytesToWrite);
        // Return duration.
        return System.nanoTime() - start;
    }

    private static long average(long[] src) {
        long sum = 0;
        for (int i = 0; i < src.length; i++) {
            sum += src[i];
        }

        return sum / src.length;
    }
}
