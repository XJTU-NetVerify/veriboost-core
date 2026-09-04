package org.ants;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.alg.flow.DinicMFImpl;

import java.util.HashMap;

//
// Created by Charlie on 2024/4/11.
// XJTU | 916267142@qq.com
//
// Using google guava to find min-cut.
//

public class TrimmingCore {
    Graph<String, DefaultEdge> g;

    public double time;

    public TrimmingCore() {
        g = new DefaultUndirectedGraph<>(DefaultEdge.class);
    }

    public void addEdge(String src, String dst) {
        g.addVertex(src);
        g.addVertex(dst);
        g.addEdge(src, dst);
    }

    public HashMap<Pair<String, String>, Integer> getCuts() {
        DinicMFImpl<String, DefaultEdge> minCut = new DinicMFImpl<>(g);
        HashMap<Pair<String, String>, Integer> nodeCuts = new HashMap<>();
        g.vertexSet().forEach(src -> g.vertexSet().forEach(dst -> {
            if(!src.equals(dst)) {
                minCut.calculateMinCut(src, dst);
                nodeCuts.put(new Pair<>(src, dst), minCut.getCutEdges().size());
            }
        }));
        return nodeCuts;
    }

    public Integer getCut(String src, String dst) {
        time = System.nanoTime();
        DinicMFImpl<String, DefaultEdge> minCut = new DinicMFImpl<>(g);
        minCut.calculateMinCut(src, dst);
        time = System.nanoTime() - time;
        return minCut.getCutEdges().size();
    }
}
