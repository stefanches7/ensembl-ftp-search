package ebi.ensembl.ftpsearchapi;

import ebi.ensembl.ftpsearchapi.utils.InvalidFilterException;
import ebi.ensembl.ftpsearchapi.utils.ParamsHelper;

/**
 * Filter object for searching the links database. Constructed only over supported filters.
 */
public class SearchFilter {


    private final String param;
    private final String value;

    public SearchFilter(final String param, final String value) throws InvalidFilterException {
        final String camelCaseParam = ParamsHelper.camelCasify(param);
        if (!ParamsHelper.isValidFilterKey(camelCaseParam)) {
            throw new InvalidFilterException(camelCaseParam);
        }
        this.param = camelCaseParam;
        this.value = value;
    }

    public String getParam() {
        return param;
    }

    public String getValue() {
        return value;
    }
}
