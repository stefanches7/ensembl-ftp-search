package ebi.ensembl.ftpsearchapi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Class that provides utils for working with parameter strings.
 */
public class ParamsHelper {

    public static boolean isValidFilterKey(final String param) {
        if (!SupportedFilters.contains(param)) {
            return false;
        }
        return true;
    }

    /**
     * Converts params with underscore word delimiters to camel-case ones matching the Spring Data aliases. I.e.,
     * <b>organism_name</b> will be converted to <b>organismName</b>.
     *
     * @param paramStringWithUnderscores any parameter
     * @return camel-cased parameter
     */
    public static String camelCasify(final String paramStringWithUnderscores) {
        final Matcher m = Pattern.compile("_[a-zA-Z]").matcher(paramStringWithUnderscores);

        final StringBuilder sb = new StringBuilder();
        int last = 0;
        while (m.find()) {
            sb.append(paramStringWithUnderscores.substring(last, m.start()));
            sb.append(m.group().substring(1).toUpperCase());
            last = m.end();
        }
        sb.append(paramStringWithUnderscores.substring(last));
        return sb.toString();
    }

}
