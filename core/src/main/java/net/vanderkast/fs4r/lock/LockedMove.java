package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentMove;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;
import net.vanderkast.fs4r.dto.MoveDto;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

/**
 * <p>Move operation that uses {@link PathLock} to provide synchronization on Paths.</p>
 * <p>{@link LockedMove#readLock} is used to support concurrency on copy origin file operations.</p>
 * <p>In case origin path equals to target, concurrent methods just returns {@link VoidOk#OK} to prevent deadlock if {@link java.util.concurrent.locks.ReadWriteLock} is used</p>
 */
public class LockedMove implements ConcurrentMove {
    private final PathLock readLock;
    private final PathLock editLock;
    private final Move move;

    public LockedMove(Move move, PathLock readLock, PathLock editLock) {
        this.readLock = readLock;
        this.move = move;
        this.editLock = editLock;
    }

    @Override
    public void move(MoveDto dto) throws IOException {
        move.move(dto);
    }

    @Override
    public VoidOk interruptibly(MoveDto dto) throws IOException, InterruptedException {
        if(dto.getOrigin().equals(dto.getTarget()))
            return VoidOk.OK;
        var lock = computePathLocks(dto);
        try {
            lock.lockInterruptibly();
            move.move(dto);
        } finally {
            lock.unlock();
        }
        return VoidOk.OK;
    }

    @Override
    public Optional<VoidOk> tryNow(MoveDto dto) throws IOException {
        if(dto.getOrigin().equals(dto.getTarget()))
            return Optional.of(VoidOk.OK);
        var lock = computePathLocks(dto);
        try {
            if (lock.tryLock())
                move.move(dto);
        } finally {
            lock.unlock();
        }
        return Optional.of(VoidOk.OK);
    }

    private FirstSecondLock computePathLocks(MoveDto dto) {
        var origin = dto.getOrigin();
        var target = dto.getTarget();

        Lock originLock = dto.isCopy() ? readLock.on(dto.getOrigin()) : editLock.on(dto.getOrigin());
        Lock targetLock = editLock.on(dto.getTarget());

        if (origin.compareTo(target) <= 0)
            return new FirstSecondLock(originLock, targetLock);
        return new FirstSecondLock(targetLock, originLock);
    }

    static class FirstSecondLock {
        private final Lock first;
        private final Lock second;

        FirstSecondLock(Lock first, Lock second) {
            this.first = first;
            this.second = second;
        }

        void lockInterruptibly() throws InterruptedException {
            first.lockInterruptibly();
            second.lockInterruptibly();
        }

        boolean tryLock() {
            return first.tryLock() && second.tryLock();
        }

        void unlock() {
            second.unlock();
            first.unlock();
        }
    }
}
