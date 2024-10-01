package org.ys.game.core.image;

public interface Recyclable {

    void recycle();

    boolean isRecycled();

    ImageWrapper setOneShot(boolean b);

    default ImageWrapper oneShot() {
        return setOneShot(true);
    }

    void shoot();

}
