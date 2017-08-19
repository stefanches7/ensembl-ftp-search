package ebi.ensembl.ftpsearchapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Suggestion for the organism_name value of link entity.
 */
@Entity
@Table(name="organism_name_suggestion")
public class OrganismNameSuggestion {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    private String organismName;

    public String getOrganismName() {
        return organismName;
    }

    public void setOrganismName(final String organismName) {
        this.organismName = organismName;
    }

}
