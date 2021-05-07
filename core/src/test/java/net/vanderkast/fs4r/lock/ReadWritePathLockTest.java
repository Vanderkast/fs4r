package net.vanderkast.fs4r.lock;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReadWritePathLockTest {
    private static final long WAIT_THRESHOLD = 20;
    private final ReadWritePathLock lock = new ReadWritePathLock();

    @Test
    void readerReaderConcurrent() throws InterruptedException {
        // given
        final Path sharedPath = mock(Path.class);
        AtomicInteger readersCount = new AtomicInteger();
        AtomicBoolean parallel = new AtomicBoolean();
        Runnable work = () -> {
            var lock = this.lock.forRead(sharedPath);
            try {
                lock.lockInterruptibly();
                var count = readersCount.incrementAndGet();
                if (count == 2)
                    parallel.set(true);
                Thread.sleep(WAIT_THRESHOLD);
            } catch (InterruptedException e) {
                fail(e);
            } finally {
                readersCount.decrementAndGet();
                lock.unlock();
            }
        };
        var reader1 = new Thread(work);
        var reader2 = new Thread(work);

        // when
        reader1.start();
        reader2.start();
        reader2.join();
        reader1.join();

        // then
        assertTrue(parallel.get());
    }

    @Test
    void writerWriterSerialized() throws InterruptedException {
        // given
        final Path sharedPath = mock(Path.class);
        AtomicInteger concurrentWriters = new AtomicInteger();
        StringBuilder order = new StringBuilder();
        var deleter = new Thread(() -> {
            var writeLock = lock.forDelete(sharedPath);
            try {
                writeLock.lockInterruptibly();
                concurrentWriters.incrementAndGet();
                Thread.sleep(WAIT_THRESHOLD);
                order.append("d");
            } catch (InterruptedException e) {
                fail(e);
            } finally {
                var count = concurrentWriters.decrementAndGet();
                if (count > 0)
                    fail("Some other writer works concurrently");
                writeLock.unlock();
            }
        });

        // when
        var moveLock = lock.forMove(sharedPath, mock(Path.class), false);
        deleter.start();
        while (concurrentWriters.get() < 1) // wait until deleter gets lock
            Thread.onSpinWait();

        moveLock.lockInterruptibly();
        var count = concurrentWriters.incrementAndGet();
        if(count > 1)
            fail("Some other writer works concurrently");
        order.append("m");
        moveLock.unlock();
        deleter.join();

        // then
        assertEquals("dm", order.toString());
    }

    @Test
    void writerWaitsReader() throws InterruptedException {
        // given
        final Path sharedPath = mock(Path.class);
        AtomicBoolean readerFlag = new AtomicBoolean();
        StringBuilder order = new StringBuilder();
        var reader = new Thread(() -> {
            var readLock = lock.forRead(sharedPath);
            try {
                readLock.lockInterruptibly();
                readerFlag.set(true);
                Thread.sleep(WAIT_THRESHOLD);
                order.append("r");
            } catch (InterruptedException e) {
                fail(e);
            } finally {
                readLock.unlock();
            }
        });
        var writeLock = lock.forDelete(sharedPath);

        // when
        reader.start();
        while (!readerFlag.get()) // wait until reader gets lock
            Thread.onSpinWait();
        writeLock.lockInterruptibly();
        order.append("w");
        writeLock.unlock();
        reader.join();

        // then
        assertEquals("rw", order.toString());
    }

    @Test
    void readerWaitsWriter() throws InterruptedException {
        // given
        final Path sharedPath = mock(Path.class);
        AtomicBoolean readerFlag = new AtomicBoolean();
        StringBuilder out = new StringBuilder();
        var reader = new Thread(() -> {
            var readLock = lock.forDelete(sharedPath);
            try {
                readLock.lockInterruptibly();
                readerFlag.set(true);
                Thread.sleep(WAIT_THRESHOLD);
                out.append("w");
            } catch (InterruptedException e) {
                fail(e);
            } finally {
                readLock.unlock();
            }
        });
        var writeLock = lock.forRead(sharedPath);

        // when
        reader.start();
        while (!readerFlag.get()) // wait until writer gets lock
            Thread.onSpinWait();
        writeLock.lockInterruptibly();
        out.append("r");
        writeLock.unlock();
        reader.join();

        // then
        assertEquals("wr", out.toString());
    }
}
