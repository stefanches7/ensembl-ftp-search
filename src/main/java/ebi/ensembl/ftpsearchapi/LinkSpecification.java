package ebi.ensembl.ftpsearchapi;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *  "Equals" criteria for the search filters.
 */
public class LinkSpecification implements Specification<Link> {

    private final SearchFilter searchFilter;

    public LinkSpecification(final SearchFilter searchFilter) {
        this.searchFilter = searchFilter;
    }

    @Override
    public Predicate toPredicate(final Root<Link> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {
        return cb.equal(root.get(searchFilter.getColumn()),searchFilter.getValue());
    }
}
