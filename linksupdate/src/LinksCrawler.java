import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.logging.Logger;


/**
 * Used to collect the desired links.
 */
public class LinksCrawler {

    public static HashMap<String, ResourceType> investigateTreeDown(final String serverAddr, final
    AbstractCollection<String>
            entryPointsRelPaths) throws
            IOException {
        final HashMap<String, ResourceType> linksToResourceType = new HashMap<>();
        for(final String entryPoint : entryPointsRelPaths) {
            linksToResourceType.putAll(walkEntryPoint(entryPoint, serverAddr));
        }
        return linksToResourceType;
    }

    private static HashMap<String, ResourceType> walkEntryPoint(final String entryPoint, final String serverAddr) throws
            IOException {
        final FTPLinkInfoParser linkInfoParser = new FTPLinkInfoParser();
        final FTPClient ftp = new FTPClient();
        final HashMap<String, ResourceType> urlResourceTypeHashMap = new HashMap<>();
        ftp.connect(serverAddr);
        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            Logger.getLogger("LinksCrawler").warning("Couldn't connect to the FTP server!");
        }
        urlResourceTypeHashMap.putAll(listDirectory(ftp, entryPoint, "", serverAddr, linkInfoParser));
        return null;
    }

    private static HashMap<String, ResourceType> listDirectory(final FTPClient ftpClient, final String parentDir, final
    String subDir, final String serverPrefix, final FTPLinkInfoParser linkInfoParser) throws IOException {
        final HashMap<String, ResourceType> urlResourceTypeHashMap = new HashMap<>();
        String dirToList = parentDir;
        if (!"".equals(subDir)) {
            dirToList += "/" + subDir;
        }
        final FTPFile[] filesInDir = ftpClient.listFiles(dirToList);
        for (final FTPFile fileFound : filesInDir) {
            //urlResourceTypeHashMap.put(serverPrefix + dirToList + fileFound.getName(), ResourceType.FILE);
            FTPFileLinkToRecordTransformer.transformToLinkRecordAndSave(linkInfoParser.parseInfo(serverPrefix +
                    dirToList + fileFound.getName()));
        }
        final FTPFile[] subdirsInDir = ftpClient.listDirectories(dirToList);
        for (final FTPFile subDirFound : subdirsInDir) {
            //continue recursively diving in the tree structure
            //urlResourceTypeHashMap.put(serverPrefix + dirToList + subDirFound.getName(), ResourceType.DIRECTORY);
            urlResourceTypeHashMap.putAll(listDirectory(ftpClient, dirToList, subDirFound.getName(), serverPrefix, linkInfoParser));
        }
        return urlResourceTypeHashMap;
    }
}
