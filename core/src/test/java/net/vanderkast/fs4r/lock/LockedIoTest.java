package net.vanderkast.fs4r.lock;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static net.vanderkast.fs4r.lock.LockedIoTest.StubLockedIo2.verifyInterruptiblyLockedCorrectly;
import static net.vanderkast.fs4r.lock.LockedIoTest.StubLockedIo2.verifyTryLockedCorrectly;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class LockedIoTest {
    @Test
    void interruptiblyNormal() throws IOException, InterruptedException {
        // given
        var in = new Object();
        var expected = new Object();
        var stub = StubLockedIo2.normal(in, expected);

        // when
        var actual = stub.interruptibly(in);

        // then
        assertSame(expected, actual);
        verifyInterruptiblyLockedCorrectly(stub);
    }

    @Test
    void interruptiblyThrowIo() throws IOException, InterruptedException {
        // given
        var in = new Object();
        var stub = StubLockedIo2.throwOn(in);
        boolean caughtIo;

        // when
        try {
            stub.interruptibly(in);
            caughtIo = false;
        } catch (IOException e) {
            caughtIo = true;
        }

        // then
        assertTrue(caughtIo);
        verifyInterruptiblyLockedCorrectly(stub);
    }

    @Test
    void tryNow_Unlocked_Normal() throws IOException {
        // given
        var in = new Object();
        var expected = new Object();
        var stub = StubLockedIo2.normal(in, expected);
        stub.setOpen(true);

        // when
        var actual = stub.tryNow(in);

        // then
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
        verifyTryLockedCorrectly(stub);
    }

    @Test
    void tryNow_Unlocked_ThrowIo() throws IOException {
        // given
        var in = new Object();
        var stub = StubLockedIo2.throwOn(in);
        stub.setOpen(true);
        boolean caughtIo;

        // when
        try {
            stub.tryNow(in);
            caughtIo = false;
        } catch (IOException e) {
            caughtIo = true;
        }

        // then
        assertTrue(caughtIo);
        verifyTryLockedCorrectly(stub);
    }

    @Test
    void tryNow_Locked() throws IOException {
        // given
        var in = new Object();
        var stub = StubLockedIo2.throwOn(in);
        stub.setOpen(false);
        boolean caughtIo;

        // when
        Optional<?> actual = Optional.of(new Object());
        try {
            actual = stub.tryNow(in);
            caughtIo = false;
        } catch (IOException ignored) {
            caughtIo = true;
        }

        // then
        assertFalse(caughtIo);
        assertTrue(actual.isEmpty());
    }

    static class StubLockedIo2<T, R> extends LockedIo<T, R> {
        private final Lock lock = mock(Lock.class);
        private final LockedIo<T, R> handler;

        StubLockedIo2(LockedIo<T, R> handler) {
            this.handler = handler;
        }

        static <T, R> StubLockedIo2<T, R> throwOn(T input) throws IOException {
            LockedIo<T, R> handler = mock(LockedIo.class);
            doThrow(IOException.class).when(handler).handle(input);
            return new StubLockedIo2<>(handler);
        }

        static <T, R> StubLockedIo2<T, R> normal(T input, R output) throws IOException {
            LockedIo<T, R> handler = mock(LockedIo.class);
            doReturn(output).when(handler).handle(input);
            return new StubLockedIo2<>(handler);
        }

        @Override
        protected Lock getLock(T data) {
            return lock;
        }

        @Override
        protected R handle(T data) throws IOException {
            return handler.handle(data);
        }

        void setOpen(boolean open) {
            doReturn(open).when(lock).tryLock();
        }

        static void verifyInterruptiblyLockedCorrectly(StubLockedIo2<?, ?> stub)
                throws InterruptedException, IOException {
            var order = inOrder(stub.lock, stub.handler, stub.lock);
            order.verify(stub.lock).lockInterruptibly();
            order.verify(stub.handler, atLeast(0)).handle(any());
            order.verify(stub.lock).unlock();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        static void verifyTryLockedCorrectly(StubLockedIo2<?, ?> stub) throws IOException {
            var order = inOrder(stub.lock, stub.handler, stub.lock);
            order.verify(stub.lock).tryLock();
            order.verify(stub.handler, atLeast(0)).handle(any());
            order.verify(stub.lock).unlock();
        }
    }
}
