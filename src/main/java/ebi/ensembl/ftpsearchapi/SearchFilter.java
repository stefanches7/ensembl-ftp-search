package ebi.ensembl.ftpsearchapi;

import ebi.ensembl.ftpsearchapi.utils.InvalidFilterException;
import ebi.ensembl.ftpsearchapi.utils.ParamsHelper;

/**
 * Filter object for searching the links database. Constructed only over supported filters.
 */
public class SearchFilter {


    private final String param;
    private final String value;

    public SearchFilter(final String camelCasifiedParam, final String value) throws InvalidFilterException {
        if (!ParamsHelper.isValidFilterKey(camelCasifiedParam)) {
            throw new InvalidFilterException(camelCasifiedParam);
        }
        this.param = camelCasifiedParam;
        this.value = value;
    }

    public String getParam() {
        return param;
    }

    public String getValue() {
        return value;
    }
}
