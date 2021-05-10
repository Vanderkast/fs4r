package net.vanderkast.fs4r.domain.concurrent;

import java.io.IOException;
import java.util.Optional;

/**
 * Declares domain-level I/O operations constructed for concurrent execution.
 *
 * @param <T> input type
 * @param <R> output type
 */
public interface ConcurrentIo<T, R> {
    /**
     * Executes domain operation in interruptibly way.
     *
     * @param data to handle
     * @return output of domain operation
     * @throws IOException          if I/O exceptions occurs
     * @throws InterruptedException if was interrupted at execution
     */
    R interruptibly(T data) throws IOException, InterruptedException;

    /**
     * <p>Tries to execute domain at go if possible, otherwise returns {@link Optional#empty()}</p>
     * <p>
     * If domain returns no value, then concurrent implementation should return {@link VoidOk#OK}.
     * This contract is provides clients ability use {@link Optional#isPresent()} to check execute on success.
     * </p>
     *
     * @param data to handle
     * @return if possible execute domain and return {@link Optional#of(R)}, {@link Optional#empty()} otherwise.
     * @throws IOException if I/O exception occurs
     */
    Optional<R> tryNow(T data) throws IOException;
}
