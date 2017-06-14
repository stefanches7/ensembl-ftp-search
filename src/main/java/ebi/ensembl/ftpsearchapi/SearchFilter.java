package ebi.ensembl.ftpsearchapi;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Filter for searching the links database. Constructed only over supported filters.
 */
public class SearchFilter {

    private final String column;
    private final String value;

    //FIXME: check if filter is available, map search_param to column_name if needed
    @Autowired
    public SearchFilter(final String column, final String value) {
        this.column = column;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }
}
