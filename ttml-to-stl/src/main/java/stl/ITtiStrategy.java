package stl;

import ttml.TimedTextObject;

import java.io.IOException;

/**
 * Created by Alexander on 6/24/2016.
 */
public interface ITtiStrategy {

    public static final int TTI_BLOCK_SIZE = 128;
    public static final int TTI_TEXT_SIZE = 112;

    byte[] build(TimedTextObject tto) throws IOException;

}
