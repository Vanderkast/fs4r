package net.vanderkast.fs4r.service.delivery;

import java.nio.file.Path;

public abstract class Delivery {
    protected final String root;

    protected Delivery(Path root) {
        this.root = root.toString();
    }

    Path fromServiceRoot(String relative) {
        return Path.of(root, relative);
    }
}
