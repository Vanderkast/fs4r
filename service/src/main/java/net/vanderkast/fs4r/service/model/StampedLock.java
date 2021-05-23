package net.vanderkast.fs4r.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StampedLock<T> {
    private final T stamp;
    private final Long expires; // utc millis
}
