package stl;

import ttml.Caption;

import java.util.List;

/**
 * Created by Alexander on 6/24/2016.
 */
public class StlSubtitle {

    private final List<Caption> captions;
    private final int captionNum;
    private final Caption caption;
    private final int linesCount;
    private final byte[][] extensionBlocks;

    public StlSubtitle(List<Caption> captions, Caption caption, int captionNum, int linesCount, byte[][] extensionBlocks) {
        this.captions = captions;
        this.caption = caption;
        this.captionNum = captionNum;
        this.linesCount = linesCount;
        this.extensionBlocks = extensionBlocks;
    }

    public byte[][] getExtensionBlocks() {
        return extensionBlocks;
    }

    public Caption getCaption() {
        return caption;
    }

    public int getLinesCount() {
        return linesCount;
    }

    public List<Caption> getCaptions() {
        return captions;
    }

    public int getCaptionNum() {
        return captionNum;
    }
}
