package stl;

import ttml.TimedTextObject;

/**
 * Created by Alexander on 6/23/2016.
 */
public interface IGsiStrategy {

    int GSI_BLOCK_SIZE = 1024;

    void fillAttributes(TimedTextObject tto, byte[] ttiBlocks);

    byte[] build(TimedTextObject tto);

}
