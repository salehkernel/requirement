package ir.salmanian.io;

import java.io.File;

/**
 * Importer interface which is used for importing projects from different external files to the application database.
 * The main implementation of this interface is {@link XMLImporter}.
 */
public interface Importer {
    void importFromFile(File importFile);
}
