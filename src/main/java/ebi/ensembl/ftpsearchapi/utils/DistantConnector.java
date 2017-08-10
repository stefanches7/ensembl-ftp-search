package ebi.ensembl.ftpsearchapi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that's responsible for performing the operations that require an Internet connection.
 */
public class DistantConnector {

    private static final String metadataDBUrl = "jdbc:mysql://mysql-eg-publicsql.ebi.ac.uk:4157/";
    private static final String metadataDBUser = "anonymous";
    private static final String metadataDBPassword = "";
    static Logger logger = LoggerFactory.getLogger(DistantConnector.class);

    public static List<String> getChildOrganismsReleaseNames(final Integer parentId) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        final List<String> childOrganismNames = new LinkedList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(metadataDBUrl, metadataDBUser, metadataDBPassword);
            st = conn.prepareStatement("SELECT o.name FROM ncbi_taxonomy.ncbi_taxa_node p JOIN " +
                    "ncbi_taxonomy.ncbi_taxa_node c ON " +
                    "(c.left_index " +
                    "BETWEEN p.left_index AND p.right_index) JOIN ensembl_metadata.organism o ON (o" +
                    ".taxonomy_id=c" +
                    ".taxon_id)" +
                    " WHERE p.taxon_id=? AND p.taxon_id!=c.taxon_id;");
            st.setInt(1, parentId);
            rs = st.executeQuery();
            while (rs.next()) {
                childOrganismNames.add(rs.getString("name"));
            }
        } catch (final SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (final SQLException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
        logger.info("Got organism name by the taxa id: " + childOrganismNames);
        return childOrganismNames;
    }
}
