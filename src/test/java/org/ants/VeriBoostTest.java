package org.ants;

import org.ants.VeriBoostUtil.LinkType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

public class VeriBoostTest {

    private VeriBoost veriBoost;

    @Before
    public void setUp() throws Exception {
        // Step1. read topology and build

        veriBoost = new VeriBoost();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("topology.txt");
        if (inputStream == null) {
            throw new RuntimeException("topology.txt not found in resources");
        }
        
        veriBoost.readTopologyFromStream(inputStream);
        veriBoost.buildEdge();
        veriBoost.buildComponent();
    }

    @Test
    public void testVeriBoost1() {
        // Step2. calculate the constraint
        veriBoost.calculateLinkStatus("panamattcity", "raleigh");

        // Step3. query the corresponding constraint
        assertEquals(61, veriBoost.calculateSimpleLinkStatus(LinkType.symbolic_link).size());
        assertEquals(65, veriBoost.calculateSimpleLinkStatus(LinkType.up_link).size());
        assertEquals(63, veriBoost.calculateSimpleLinkStatus(LinkType.down_link).size());
    }

    @Test
    public void testVeriBoost2() {
        veriBoost.calculateLinkStatus("sylva", "ellijay");
        assertEquals(6, veriBoost.calculateSimpleLinkStatus(LinkType.symbolic_link).size());
        assertEquals(8, veriBoost.calculateSimpleLinkStatus(LinkType.up_link).size());
        assertEquals(175, veriBoost.calculateSimpleLinkStatus(LinkType.down_link).size());
    }
    @Test
     public void testVeriBoost3() {
        // Step2. calculate the constraint
        veriBoost.calculateLinkStatus("greensboro", "atlanta");

        // Step3. query the corresponding constraint
        assertEquals(61, veriBoost.calculateSimpleLinkStatus(LinkType.symbolic_link).size());
        assertEquals(65, veriBoost.calculateSimpleLinkStatus(LinkType.up_link).size());
        assertEquals(63, veriBoost.calculateSimpleLinkStatus(LinkType.down_link).size());

        System.out.println(veriBoost.calculateSimpleLinkStatus(LinkType.symbolic_link).size());
    }
}
