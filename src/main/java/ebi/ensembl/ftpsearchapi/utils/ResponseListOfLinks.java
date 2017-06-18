package ebi.ensembl.ftpsearchapi.utils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Override of a list for the better output of link strings.
 * FIXME: it doesn't work on the return of a RestController!
 */
public class ResponseListOfLinks extends LinkedList<String> {

    @Override
    public String toString() {
        final Iterator var1 = this.iterator();
        if(!var1.hasNext()) {
            return "";
        } else {
            final StringBuilder var2 = new StringBuilder();

            while(true) {
                final Object var3 = var1.next();
                var2.append(var3 == this?"(this Collection)":var3);
                if(!var1.hasNext()) {
                    return var2.append('\n').toString();
                }

                var2.append('\n');
            }
        }
    }
}
