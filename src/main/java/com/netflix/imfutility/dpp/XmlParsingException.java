package com.netflix.imfutility.dpp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandr on 5/10/2016.
 * <p>
 * An Exception class to wrap xml parsing errors.
 */
public class XmlParsingException extends Exception {

    private List<String> errors;

    public XmlParsingException(Exception e, List<String> errors) {
        super(e);
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new ArrayList<String>();
        }

        if (this.errors.size() == 0) {
            this.errors.add(e.getLocalizedMessage());
        }
    }

    public XmlParsingException(List<String> errors) {
        super();
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new ArrayList<String>();
        }
    }

    /**
     * Returns all parsing errors occurred during loading and validating of xml file.
     *
     * @return a collection with all found errors.
     */
    public List<String> getErrors() {
        return this.errors;
    }
}
