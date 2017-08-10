package ebi.ensembl.ftpsearchapi;

import ebi.ensembl.ftpsearchapi.utils.DistantConnector;
import ebi.ensembl.ftpsearchapi.utils.InvalidFilterException;

import java.util.LinkedList;
import java.util.List;

/**
 * Service class for taxonomy branch specification.
 */
public class TaxaSearchFilterContainer {

    private final Integer taxaId;
    private final List<SearchFilter> childrenNamesSearchFilters;

    public TaxaSearchFilterContainer(final Integer value) throws InvalidFilterException {
        taxaId = value;
        final List<String> taxaChildrenReleaseNames = DistantConnector.getChildOrganismsReleaseNames(value);
        childrenNamesSearchFilters = new LinkedList<>();
        for (final String releaseName : taxaChildrenReleaseNames) {
            childrenNamesSearchFilters.add(new SearchFilter("organismName",releaseName));
        }
    }

    public List<SearchFilter> getChildrenNamesSearchFilters() {
        return childrenNamesSearchFilters;
    }

    public Integer getTaxaId() {
        return taxaId;
    }
}
