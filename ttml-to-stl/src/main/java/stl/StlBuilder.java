package stl;

import ttml.TimedTextObject;

import java.io.IOException;

/**
 * Created by Alexander on 6/23/2016.
 */
public class StlBuilder {

    public byte[] build(TimedTextObject tto, IGsiStrategy gsiStrategy, ITtiStrategy ttiStrategy) throws IOException {
        //first we check if the TimedTextObject had been built, otherwise...
        if (!tto.built) {
            return null;
        }

        // build tti
        byte[] tti = ttiStrategy.build(tto);

        // build gsi
        gsiStrategy.fillAttributes(tto, tti);
        byte[] gsi = gsiStrategy.build(tto);


        // build result
        byte[] result = new byte[gsi.length + tti.length];
        System.arraycopy(gsi, 0, result, 0, gsi.length);
        System.arraycopy(tti, 0, result, gsi.length, tti.length);
        return result;
    }

}
