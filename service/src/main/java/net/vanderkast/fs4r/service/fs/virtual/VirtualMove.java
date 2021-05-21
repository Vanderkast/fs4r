package net.vanderkast.fs4r.service.fs.virtual;

import net.vanderkast.fs4r.domain.concurrent.ConcurrentMove;
import net.vanderkast.fs4r.domain.concurrent.VoidOk;
import net.vanderkast.fs4r.dto.MoveDto;
import net.vanderkast.fs4r.dto.impl.MoveDtoImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class VirtualMove implements ConcurrentMove {
    private final VirtualFileSystem fs;
    private final ConcurrentMove move;

    public VirtualMove(VirtualFileSystem fs, ConcurrentMove move) {
        this.fs = fs;
        this.move = move;
    }

    /**
     * <p>If operation is copy, then allows to read protected origin.</p>
     *
     * @param dto information about move operation
     * @throws IOException if I/O exception occurs
     */
    @Override
    public void move(MoveDto dto) throws IOException {
        verifyProtection(dto);
        move.move(realDto(dto));
    }

    private void verifyProtection(MoveDto virtual) throws ProtectedPathException {
        if (!virtual.isCopy())
            fs.verifyUnprotected(virtual.getOrigin());
        fs.verifyUnprotected(virtual.getTarget());
    }

    private MoveDto realDto(MoveDto virtual) throws FileNotFoundException {
        return new MoveDtoImpl(
                fs.mapOrThrow(virtual.getOrigin()),
                fs.mapOrThrow(virtual.getTarget()),
                virtual.isCopy(),
                virtual.isFailOnTargetExist());
    }

    @Override
    public VoidOk interruptibly(MoveDto virtual) throws IOException, InterruptedException {
        verifyProtection(virtual);
        return move.interruptibly(realDto(virtual));
    }

    @Override
    public Optional<VoidOk> tryNow(MoveDto virtual) throws IOException {
        verifyProtection(virtual);
        return move.tryNow(realDto(virtual));
    }
}
