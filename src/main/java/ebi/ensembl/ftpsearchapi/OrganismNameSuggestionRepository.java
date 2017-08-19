package ebi.ensembl.ftpsearchapi;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Table storing suggestions for the organism_name column of link table.
 */
public interface OrganismNameSuggestionRepository extends CrudRepository<OrganismNameSuggestion, Long> {


    @Query(value = "SELECT * FROM organism_name_suggestion WHERE organism_name like ?1 LIMIT 20",
            nativeQuery = true)
    List<OrganismNameSuggestion> findByOrganismNameLimit20(String value);
}
