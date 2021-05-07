package net.vanderkast.fs4r.simple;

import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.dto.MoveDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JustMoveTest {
    private final Move move = new JustMove();

    @Test
    void originNotExists(@TempDir Path tmp) {
        // given
        Path origin = Path.of(tmp.toString(), JustMove.class.getSimpleName(), "origin");
        assertFalse(Files.exists(origin));
        Path target = Path.of(JustMove.class.getSimpleName() + 2);

        // when
        try {
            move.move(new MoveDto(origin, target, false, false));
            fail();
        } catch (IOException e) {
            // then
            // okay
        }
    }

    @Test
    void copyFailOnTargetExists(@TempDir Path tmp) throws IOException {
        // given
        Path origin = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "origin");
        assertTrue(Files.exists(origin));
        Path target = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "target");
        assertTrue(Files.exists(target));
        boolean caught = false;

        // when
        try {
            move.move(new MoveDto(origin, target, true, true));
        } catch (IOException e) {
            // then
            caught = true;
        }

        assertTrue(caught);
    }

    @Test
    void copyOverwriteOnTargetExists(@TempDir Path tmp) throws IOException {
        // given
        Path origin = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "origin");
        assertTrue(Files.exists(origin));
        Path target = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "target");
        assertTrue(Files.exists(target));

        // when
        move.move(new MoveDto(origin, target, true, false));

        // then
        assertTrue(Files.exists(origin));
        assertTrue(Files.exists(target));
    }

    @Test
    void copyTargetNotExists(@TempDir Path tmp) throws IOException {
        // given
        Path origin = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "origin");
        assertTrue(Files.exists(origin));
        Path target = Path.of(tmp.toString(), "COPEEEE");
        assertFalse(Files.exists(target));

        // when
        move.move(new MoveDto(origin, target, true, true));

        // then
        assertTrue(Files.exists(origin));
        assertTrue(Files.exists(target));
    }

    @Test
    void moveTargetNotExists(@TempDir Path tmp) throws IOException {
        // given
        Path origin = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "origin");
        assertTrue(Files.exists(origin));
        Path target = Path.of(tmp.toString(), "target");
        assertFalse(Files.exists(target));

        // when
        move.move(new MoveDto(origin, target, false, true));

        // then
        assertFalse(Files.exists(origin));
        assertTrue(Files.exists(target));
    }

    @Test
    void moveReplaceOnTargetExists(@TempDir Path tmp) throws IOException {
        // given
        Path origin = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "origin");
        assertTrue(Files.exists(origin));
        Path target = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "target");
        assertTrue(Files.exists(target));

        // when
        move.move(new MoveDto(origin, target, false, false));

        // then
        assertFalse(Files.exists(origin));
        assertTrue(Files.exists(target));
    }

    @Test
    void moveFailOnTargetExists(@TempDir Path tmp) throws IOException {
        // given
        Path origin = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "origin");
        assertTrue(Files.exists(origin));
        Path target = Files.createTempFile(tmp, JustMoveTest.class.getSimpleName(), "target");
        assertTrue(Files.exists(target));
        boolean caught = false;

        // when
        try {
            move.move(new MoveDto(origin, target, false, true));
        } catch (IOException e) {
            // then
            caught = true;
        }

        assertTrue(caught);
    }
}
