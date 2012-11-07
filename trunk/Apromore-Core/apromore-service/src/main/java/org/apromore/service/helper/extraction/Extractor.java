package org.apromore.service.helper.extraction;

import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Node;
import org.apromore.service.model.RFragment2;
import org.jbpt.algo.tree.tctree.TCType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class Extractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Extractor.class);

    /**
     * Removes the content of the f c from f considering the f combinations.
     * e.g. (f = S, c = P), (f = P, c = P), etc.
     * Replaces the content of c in f by a pocket, and returns the ID of the newly inserted
     * pocket. Both fragments f and c will be modified according the f types.
     *
     * @param f Parent f
     * @param c Child f
     * @return ID of the pocket inserted by replacing the child f
     */
    public static Node extractChildFragment(final RFragment2 f, final RFragment2 c, final Canonical g) {
        Node pocket = null;
        if (f.getType().equals(TCType.POLYGON)) {
            if (c.getType() != TCType.POLYGON) {
                LOGGER.debug("Processing FS CNS");
                pocket = FSCNSExtractor.extract(f, c, g);
                LOGGER.debug("Pocket Id: " + pocket.getId());
            }
            //TODO what if both are of type POLYGON??? potential NPE
        } else {
            if (c.getType().equals(TCType.POLYGON)) {
                LOGGER.debug("Processing FNS CS");
                pocket = FNSCSExtractor.extract(f, c, g);
                LOGGER.debug("Pocket Id: " + pocket.getId());
            } else {
                LOGGER.debug("Processing FNS CNS");
                pocket = FNSCNSExtractor.extract(f, c, g);
                LOGGER.debug("Pocket Id: " + pocket.getId());
            }
        }
        return pocket;
    }
}
