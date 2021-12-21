package ir.salmanian.io;

/**
 * Exporter interface which is used for exporting projects to different file types.
 * Tow implementation of this interface are {@link WordExporter} and {@link XMLExporter}.
 */
public interface Exporter {
    void exportToFile();
}
