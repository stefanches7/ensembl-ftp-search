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
@Table(name="file_type_suggestion")
public class FileTypeSuggestion {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    private String fileType;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(final String fileType) {
        this.fileType = fileType;
    }
}
