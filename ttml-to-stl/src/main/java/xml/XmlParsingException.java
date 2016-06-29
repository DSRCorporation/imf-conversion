package xml;

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
            this.errors = new ArrayList<>();
        }

        if (this.errors.isEmpty()) {
            this.errors.add(e.getLocalizedMessage());
        }
    }

    public XmlParsingException(List<String> errors) {
        super();
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new ArrayList<>();
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

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (String error : errors) {
            sb.append(error);
            sb.append(" ");
        }
        return sb.toString();
    }

}
