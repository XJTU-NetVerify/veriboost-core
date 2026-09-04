package org.ants;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TarjanTest {

    // Helper method to compare components regardless of their order
    private boolean componentsEqual(HashMap<Integer, HashSet<Integer>> actual, 
                                  Set<Set<Integer>> expected) {
        if (actual.size() != expected.size()) return false;
        
        Set<Set<Integer>> actualComponents = new HashSet<>();
        for (HashSet<Integer> component : actual.values()) {
            actualComponents.add(new HashSet<>(component));
        }
        
        return actualComponents.equals(expected);
    }

    @Test
    public void testExample1() {
        /* Graph for Example 1:
              1
             / \
            2   3
           / \ /
          4   5
           \
            6
        */
        Tarjan tarjan = new Tarjan();
        tarjan.setNodeNum(6);
        tarjan.setLinkNum(5);
        tarjan.resize();
        
        // Build graph
        tarjan.addEdge(1, 3);
        tarjan.addEdge(3, 1);
        tarjan.addEdge(2, 4);
        tarjan.addEdge(4, 2);
        tarjan.addEdge(1, 2);
        tarjan.addEdge(2, 1);
        tarjan.addEdge(4, 6);
        tarjan.addEdge(6, 4);
        tarjan.addEdge(2, 3);
        tarjan.addEdge(3, 2);
        
        tarjan.tarjanRun();
        
        // Expected components
        Set<Set<Integer>> expectedComponents = new HashSet<>();
        expectedComponents.add(new HashSet<>(Arrays.asList(4, 6)));
        expectedComponents.add(new HashSet<>(Arrays.asList(2, 4)));
        expectedComponents.add(new HashSet<>(Arrays.asList(1, 2, 3)));
        expectedComponents.add(new HashSet<>(Collections.singletonList(5)));
        
        // Verify
        assertEquals(4, tarjan.bcc);
        assertTrue(componentsEqual(tarjan.getConnectComponents(), expectedComponents));
        
        // Verify cut points
        Set<Integer> expectedCutPoints = new HashSet<>(Arrays.asList(2, 4, 5));
        assertEquals(expectedCutPoints, tarjan.getCutPoints());
    }

    @Test
    public void testExample2() {
        /* Graph for Example 2:
              1
             /
            3-----6
           / \   /
          5---2-4
               \
                7
        */
        Tarjan tarjan = new Tarjan();
        tarjan.setNodeNum(7);
        tarjan.setLinkNum(8);
        tarjan.resize();
        
        // Build graph
        tarjan.addEdge(1, 3);
        tarjan.addEdge(3, 1);
        tarjan.addEdge(2, 4);
        tarjan.addEdge(4, 2);
        tarjan.addEdge(3, 5);
        tarjan.addEdge(5, 3);
        tarjan.addEdge(2, 5);
        tarjan.addEdge(5, 2);
        tarjan.addEdge(6, 4);
        tarjan.addEdge(4, 6);
        tarjan.addEdge(6, 3);
        tarjan.addEdge(3, 6);
        tarjan.addEdge(2, 7);
        tarjan.addEdge(7, 2);
        
        tarjan.tarjanRun();
        
        // Expected components
        Set<Set<Integer>> expectedComponents = new HashSet<>();
        expectedComponents.add(new HashSet<>(Arrays.asList(2, 7)));
        expectedComponents.add(new HashSet<>(Arrays.asList(2, 3, 4, 5, 6)));
        expectedComponents.add(new HashSet<>(Arrays.asList(1, 3)));
        
        // Verify
        assertEquals(3, tarjan.bcc);
        assertTrue(componentsEqual(tarjan.getConnectComponents(), expectedComponents));
        
        // Verify cut points
        Set<Integer> expectedCutPoints = new HashSet<>(Arrays.asList(2, 3));
        assertEquals(expectedCutPoints, tarjan.getCutPoints());
    }

    @Test
    public void testExample3() {
        /* Graph for Example 3:
              1
             / \
            2---3
            4   5
            (4 and 5 are isolated)
        */
        Tarjan tarjan = new Tarjan();
        tarjan.setNodeNum(5);
        tarjan.setLinkNum(3);
        tarjan.resize();
        
        // Build graph
        tarjan.addEdge(1, 2);
        tarjan.addEdge(2, 1);
        tarjan.addEdge(2, 3);
        tarjan.addEdge(3, 2);
        tarjan.addEdge(1, 3);
        tarjan.addEdge(3, 1);
        
        tarjan.tarjanRun();
        
        // Expected components
        Set<Set<Integer>> expectedComponents = new HashSet<>();
        expectedComponents.add(new HashSet<>(Collections.singletonList(4)));
        expectedComponents.add(new HashSet<>(Collections.singletonList(5)));
        expectedComponents.add(new HashSet<>(Arrays.asList(1, 2, 3)));
        
        // Verify
        assertEquals(3, tarjan.bcc);
        assertTrue(componentsEqual(tarjan.getConnectComponents(), expectedComponents));
    }

    @Test
    public void testEmptyGraph() {
        Tarjan tarjan = new Tarjan();
        tarjan.setNodeNum(0);
        tarjan.setLinkNum(0);
        tarjan.resize();
        tarjan.tarjanRun();
        
        assertEquals(0, tarjan.bcc);
        assertTrue(tarjan.getConnectComponents().isEmpty());
        assertTrue(tarjan.getCutPoints().isEmpty());
    }

    @Test
    public void testSingleNode() {
        Tarjan tarjan = new Tarjan();
        tarjan.setNodeNum(1);
        tarjan.setLinkNum(0);
        tarjan.resize();
        tarjan.tarjanRun();
        
        assertEquals(1, tarjan.bcc);
        assertEquals(1, tarjan.getConnectComponents().size());
        assertTrue(tarjan.getConnectComponents().get(1).contains(1));
        assertFalse(tarjan.getCutPoints().isEmpty());
    }

    @Test
    public void testTwoConnectedNodes() {
        Tarjan tarjan = new Tarjan();
        tarjan.setNodeNum(2);
        tarjan.setLinkNum(1);
        tarjan.resize();
        
        tarjan.addEdge(1, 2);
        tarjan.addEdge(2, 1);
        
        tarjan.tarjanRun();
        
        assertEquals(1, tarjan.bcc);
        assertEquals(1, tarjan.getConnectComponents().size());
        assertTrue(tarjan.getConnectComponents().get(1).containsAll(Arrays.asList(1, 2)));
        assertTrue(tarjan.getCutPoints().isEmpty());
    }

    @Test
    public void testBridgeGraph() {
        Tarjan tarjan = new Tarjan();
        tarjan.setNodeNum(4);
        tarjan.setLinkNum(3);
        tarjan.resize();
        
        // Graph: 1-2-3-4
        tarjan.addEdge(1, 2);
        tarjan.addEdge(2, 1);
        tarjan.addEdge(2, 3);
        tarjan.addEdge(3, 2);
        tarjan.addEdge(3, 4);
        tarjan.addEdge(4, 3);
        
        tarjan.tarjanRun();
        
        // Expected components (each edge is a separate component)
        Set<Set<Integer>> expectedComponents = new HashSet<>();
        expectedComponents.add(new HashSet<>(Arrays.asList(1, 2)));
        expectedComponents.add(new HashSet<>(Arrays.asList(2, 3)));
        expectedComponents.add(new HashSet<>(Arrays.asList(3, 4)));
        
        // Verify
        assertEquals(3, tarjan.bcc);
        assertTrue(componentsEqual(tarjan.getConnectComponents(), expectedComponents));
        
        // Verify cut points (2 and 3)
        Set<Integer> expectedCutPoints = new HashSet<>(Arrays.asList(2, 3));
        assertEquals(expectedCutPoints, tarjan.getCutPoints());
    }

}