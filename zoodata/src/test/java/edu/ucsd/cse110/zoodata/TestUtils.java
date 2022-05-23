package edu.ucsd.cse110.zoodata;

import java.io.InputStreamReader;
import java.io.Reader;

import edu.ucsd.cse110.zoodata.ZooEdgeTests;

public class TestUtils {
    /**
     * @param path path to file in src/main/test/resources
     * @return a reader for the file at the path provided
     */
    public static Reader resourceReader(String path) {
        if (!path.startsWith("/")) {
            path = String.format("/%s", path);
        }
        var stream = ZooEdgeTests.class.getResourceAsStream(path);
        return new InputStreamReader(stream);
    }
}
