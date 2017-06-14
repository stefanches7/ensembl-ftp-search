package ebi.ensembl.ftpsearchapi;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Filter for searching the links database. Constructed only over supported filters.
 */
public class SearchFilter {

    public String column;
    public String value;

    //FIXME: check if filter is available, map search_param to column_name if needed
    @Autowired
    public SearchFilter(final String column, final String value) {
        this.column = column;
        this.value = value;
    }
}
