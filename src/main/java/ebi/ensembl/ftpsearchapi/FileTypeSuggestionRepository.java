package ebi.ensembl.ftpsearchapi;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *  Table storing suggestions for the organism_name column of link table.
 */
public interface FileTypeSuggestionRepository extends CrudRepository<FileTypeSuggestion, Long> {

    @Query(value = "SELECT * FROM file_type_suggestion WHERE file_type like ?1 LIMIT 20",
            nativeQuery = true)
    List<FileTypeSuggestion> findByFileTypeLimit20(String value);
}
