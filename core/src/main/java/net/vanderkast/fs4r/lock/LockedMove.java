package net.vanderkast.fs4r.lock;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.domain.concurrent.ConcurrentMove;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;
import net.vanderkast.fs4r.domain.dto.MoveDto;

import java.io.IOException;
import java.util.Optional;

public class LockedMove implements ConcurrentMove {
    private final PathLock pathLock;
    private final Move move;

    public LockedMove(PathLock pathLock, Move move) {
        this.pathLock = pathLock;
        this.move = move;
    }

    @Override
    public void move(MoveDto dto) throws IOException {
        move.move(dto);
    }

    @Override
    public VoidOk interruptibly(MoveDto dto) throws IOException, InterruptedException {
        var originHigher = dto.getOrigin().compareTo(dto.getTarget());
        var from = pathLock.on(originHigher <= 0 ? dto.getOrigin() : dto.getTarget());
        var to = pathLock.on(originHigher <= 0 ? dto.getTarget() : dto.getOrigin());
        try {
            from.lockInterruptibly();
            to.lockInterruptibly();
            move.move(dto);
        }finally {
            to.unlock();
            from.unlock();
        }
        return VoidOk.OK;
    }

    @Override
    public Optional<VoidOk> tryNow(MoveDto dto) throws IOException {
        var originHigher = dto.getOrigin().compareTo(dto.getTarget());
        var from = pathLock.on(originHigher <= 0 ? dto.getOrigin() : dto.getTarget());
        var to = pathLock.on(originHigher <= 0 ? dto.getTarget() : dto.getOrigin());
        try {
            if(from.tryLock() && to.tryLock())
                move.move(dto);
        } finally {
            to.unlock();
            from.unlock();
        }
        return Optional.of(VoidOk.OK);
    }
}
