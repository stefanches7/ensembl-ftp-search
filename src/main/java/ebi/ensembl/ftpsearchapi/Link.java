package ebi.ensembl.ftpsearchapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Link object representing the link table records from MySQL database.
 */
@Entity
@Table(name="link")
public class Link {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    private String linkUrl;

    private String organismName;

    //FIXME:write enum for the file types!
    private String fileType;

    public long getId() {
        return id;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(final String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getOrganismName() {
        return organismName;
    }

    public void setOrganismName(final String organismName) {
        this.organismName = organismName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(final String fileType) {
        this.fileType = fileType;
    }
}
