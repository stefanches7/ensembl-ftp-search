package ebi.ensembl.ftpsearchapi.utils;

/**
 * <p>Enum class for listing the filters currently available to apply to search results in the API.</p>
 */
enum SupportedFilters {

    ORGANISM_NAME("organismName"),
    FILE_TYPE("fileType");

    private final String value;

    SupportedFilters(final String value) {
        this.value = value;
    }

    public static boolean contains(final String value) {
        for (final SupportedFilters filter : values()) {
            if (filter.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

}
