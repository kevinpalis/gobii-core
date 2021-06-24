package org.gobiiproject.gobiiprocess.digester.HelperFunctions;

import static org.gobiiproject.gobiiprocess.digester.utils.TransposeMatrix.transposeMatrix;

/**
 * A class representing a transformation that is not 'in place'. This transformation may take several parameters, but the
 * transformative step requires a file to operate on and a place for it to place the output file.
 */

public abstract class MobileTransform {
    /**
     * Perform transformation from 'from' location to 'to' location optionally making use of the error path provided
     *
     * @param fromFileLocation String representation of the from location on the filesystem
     * @param toFileLocation   String representation of the to location on the filesystem
     * @param errorPath        place to use for temporary error files
     */
    public abstract void transform(String fromFileLocation, String toFileLocation, String errorPath);

    public static final MobileTransform PGArray = new MobileTransform() {

        public void transform(String fromFile, String toFile, String errorPath) {
            new PGArray(fromFile, toFile, "alts").process();
        }
    };

    /**
     * @param dest Name of a non-existent temporary folder to store temporary files in
     */
    public static MobileTransform getTransposeMatrix(String dest) {
        return new MobileTransform() {
            public void transform(String fromFile, String toFile, String errorPath) {
                transposeMatrix("tab", fromFile, toFile, dest);
            }
        };
    }
}

