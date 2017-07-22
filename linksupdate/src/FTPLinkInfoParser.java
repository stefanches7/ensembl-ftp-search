import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Gets the information it is possible to get/which is needed from the file link.
 */
public class FTPLinkInfoParser {

    //values are the right pattern strings to get the desired info out of the FTP link. NB! Please use the
    // target-capturing patterns, so that matcher's groups consist only of the values that exactly are the infos to
    // create a database row.
    private final Map<String, String> currentFTPPatternsMap;

    public FTPLinkInfoParser() {
        this.currentFTPPatternsMap = new HashMap<>();
        this.currentFTPPatternsMap.put("organism_name", "");
        this.currentFTPPatternsMap.put("file_type","");
    }

    public Set<JSONObject> parseInfo(final String fileLink) {
        final Set<JSONObject> parsedData = new HashSet<>();
        final Pattern pattern = Pattern.compile(this.currentFTPPatternsMap.get("organism_name"));
        final Matcher matcher = pattern.matcher(fileLink);
        //since file can correspond to many organism names (e.g. compara files), create JSON object for each organism
        // name we may find
        for (int i = 1; i <= matcher.groupCount(); i++) {
            final JSONObject linkRow = new JSONObject();
            linkRow.put("organism_name", matcher.group(i));
            linkRow.put("link_url", fileLink);
            final Pattern pattern1 = Pattern.compile(this.currentFTPPatternsMap.get("link_url"));
            final Matcher matcher1 = pattern1.matcher(fileLink);
            linkRow.put("file_type", matcher1.group(1));
            parsedData.add(linkRow);
        }
        return parsedData;
    }
}
