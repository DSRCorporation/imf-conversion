package com.netflix.imfutility.dpp;
import java.util.Vector;

/**
 * Created by Alexandr on 5/10/2016.
 *
 * An Exception class to wrap Metadata.xml parsing errors.
 */
public class MetadataException extends Exception {

    private Vector<String> errors;

    public MetadataException(Exception e, Vector<String> errors) {
        super(e);
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new Vector<String>();
        }

        if (this.errors.size() == 0) {
            this.errors.add(e.getLocalizedMessage());
        }
    }

    public MetadataException(Vector<String> errors) {
        super();
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new Vector<String>();
        }
    }

    /**
     * Returns all parsing errors occurred during loading and validating of metadata.xml file.
     *
     * @return a collection with all found errors.
     */
    public Vector<String> getErrors() {
        return this.errors;
    }
}
