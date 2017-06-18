package ebi.ensembl.ftpsearchapi.utils;

/**
 * Exception
 */
public class InvalidFilterException extends Exception {
    private final String paramName;

    public InvalidFilterException(final String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
