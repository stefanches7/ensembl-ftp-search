package ebi.ensembl.ftpsearchapi;

import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URL;

@Entity
public class Link {
    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    private URL linkUrl;

    private String organismName;

    //FIXME:write enum for the file types!
    private String fileType;

    public long getId() {
        return id;
    }

    public URL getLinkUrl() {
        return linkUrl;
    }

    public String getOrganismName() {
        return organismName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setLinkUrl(URL linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setOrganismName(String organismName) {
        this.organismName = organismName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
