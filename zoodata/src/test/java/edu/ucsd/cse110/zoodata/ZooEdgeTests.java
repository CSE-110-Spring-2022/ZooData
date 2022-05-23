package edu.ucsd.cse110.zoodata;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ZooEdgeTests {
    Context ctx;

    @Test
    public void fromJSONs_works() {
        var graphReader = TestUtils.resourceReader("foobar_graph.json");
        var infoReader = TestUtils.resourceReader("foobar_edge_info.json");

        var edges = Trails.fromJson(graphReader, infoReader);
        assertEquals(1, edges.size());
    }
}