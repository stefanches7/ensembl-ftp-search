import ebi.ensembl.ftpsearchapi.Link;
import ebi.ensembl.ftpsearchapi.LinkRepository;
import net.minidev.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 *  Creates new record with all the parsed information given an FTP file link.
 */
public class FTPFileLinkToRecordTransformer {

    private LinkRepository linkRepository;

    public void transformToLinkRecordAndSave(final JSONObject fileLinkInfo) {
        final Link newRow = new Link();
        for (final String key: fileLinkInfo.keySet()) {
            switch (key) {
                case "organism_name" : newRow.setOrganismName(fileLinkInfo.getAsString(key)); break;
                case "file_type" : newRow.setFileType(fileLinkInfo.getAsString(key)); break;
                case "link_url" :
                    try {
                        newRow.setLinkUrl(new URL(fileLinkInfo.getAsString(key)));
                    } catch (final MalformedURLException e) {
                        Logger.getLogger("RecordTransformer").warning("URL parsed from JSON is invalid!");
                    }
                    break;
                default: break;
            }
        }
        //linkRepository.save();
    }
}
