package ebi.ensembl.ftpsearchapi;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that unies LinkSpecifications to produce end-specification.
 */
public class LinkSpecificationsUnifier {

    private final List<SearchFilter> searchFilters;

    public LinkSpecificationsUnifier() {
        searchFilters = new LinkedList<SearchFilter>();
    }

    public LinkSpecificationsUnifier with(final SearchFilter searchFilter) {
        searchFilters.add(searchFilter);
        return this;
    }

    public Specification<Link> produce() {
        if (searchFilters.size() == 0) { return null;}
        final List<Specification<Link>> specs = new ArrayList<Specification<Link>>();
        for (final SearchFilter searchFilter : searchFilters) {
            specs.add(new LinkSpecification(searchFilter));
        }

        Specification<Link> intersection = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
            intersection = Specifications.where(intersection).or(specs.get(i));
        }
        return intersection;
    }
}
