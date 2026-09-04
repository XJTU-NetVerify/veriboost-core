package org.ants;

import org.ants.VeriBoostUtil.SimpleLink;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;

public class SimplePathCoreTest {

    // Test a simple graph: triangle plus a dangling node,
    // cut point should be the connecting node
    @Test
    public void testCutPointsAndComponents_simpleGraph() {
        SimplePathCore core = new SimplePathCore();
        core.addEdge("1", "2");
        core.addEdge("2", "3");
        core.addEdge("3", "1");
        core.addEdge("3", "4");

        core.constructBCTree();

        assertTrue(core.tarjan_cut_points.contains("3"));
        assertEquals(2, core.tarjan_connected_components.size());

        for (HashSet<String> comp : core.tarjan_connected_components.values()) {
            assertTrue(comp.contains("3"));
        }
    }

    // Test a chain graph without any cut points
    @Test
    public void testNoCutPoints_chainGraph() {
        SimplePathCore core = new SimplePathCore();
        core.addEdge("1", "2");
        core.addEdge("2", "3");
        core.addEdge("3", "4");
        core.addEdge("4", "1");

        core.constructBCTree();

        assertTrue(core.tarjan_cut_points.isEmpty());
        assertEquals(1, core.tarjan_connected_components.size());

        HashSet<String> comp = core.tarjan_connected_components.values().iterator().next();
        assertEquals(4, comp.size());
    }

    // Test a complex graph with multiple cut points
    @Test
    public void testMultipleCutPoints_complexGraph() {
        SimplePathCore core = new SimplePathCore();

        core.addEdge("A", "B");
        core.addEdge("B", "C");
        core.addEdge("C", "D");
        core.addEdge("B", "E");
        core.addEdge("E", "F");
        core.addEdge("E", "G");
        core.addEdge("G", "H");
        core.addEdge("H", "E"); // cycle

        core.constructBCTree();

        assertTrue(core.tarjan_cut_points.contains("B"));
        assertTrue(core.tarjan_cut_points.contains("E"));
        assertTrue(core.tarjan_connected_components.size() >= 3);
    }

    // Test getRelevantLinks returns edges on a path between two nodes
    @Test
    public void testGetRelevantLinks_pathEdges() {
        SimplePathCore core = new SimplePathCore();

        core.addEdge("1", "2");
        core.addEdge("2", "3");
        core.addEdge("3", "4");
        core.addEdge("4", "5");
        core.addEdge("3", "6");

        core.constructBCTree();

        HashSet<SimpleLink> pathLinks = core.getRelevantLinks("1", "5");
        assertNotNull(pathLinks);
        assertTrue(pathLinks.stream().anyMatch(link -> 
            (link.src_name.equals("2") && link.dst_name.equals("3")) || 
            (link.src_name.equals("3") && link.dst_name.equals("4"))
        ));
    }

    // Test each cut point belongs to at least two biconnected components
    @Test
    public void testCutPointBelongsToMultipleComponents() {
        SimplePathCore core = new SimplePathCore();

        core.addEdge("1", "2");
        core.addEdge("2", "3");
        core.addEdge("3", "1");
        core.addEdge("2", "4");
        core.addEdge("4", "5");
        core.addEdge("5", "2");

        core.constructBCTree();

        assertTrue(core.tarjan_cut_points.contains("2"));

        Map<Integer, HashSet<String>> comps = core.tarjan_connected_components;
        int count = 0;
        for (HashSet<String> comp : comps.values()) {
            if (comp.contains("2")) {
                count++;
            }
        }
        assertTrue(count >= 2);
    }
}
