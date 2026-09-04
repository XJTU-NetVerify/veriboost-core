package org.ants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.ants.VeriBoostUtil.SimpleLink;

//
// Created by Charlie on 2024/3/19.
// XJTU | 916267142@qq.com
//
// Step1. We first find all 'point bi-connected component' and 'cut points'
// And then use then to construct a new graph.
// Step2. We traverse the new graph to find the relevant components, and add all links of these components
//

public class SimplePathCore {
    Tarjan tarjan;

    BCTree BCTree;

    // the graph node is the string format, and thus map 'String' to 'int' format for Tarjan class
    Integer id;
    HashMap<String, Integer> name_to_id;  // device_name -> device_id
    HashMap<Integer, String> id_to_name;  // device_id -> device_name

    // the middle information
    HashMap<Integer, HashSet<String>> tarjan_connected_components; // component_id -> {nodes}
    HashSet<String> tarjan_cut_points;
    HashSet<SimpleLink> links;
    HashSet<String> nodes;

    public SimplePathCore() {
        this.tarjan = new Tarjan();
        this.BCTree = new BCTree();
        this.name_to_id = new HashMap<>();
        this.id_to_name = new HashMap<>();
        this.nodes = new HashSet<>();
        this.links = new HashSet<>();
        this.tarjan_connected_components = new HashMap<>();
        this.id = 1; // The node id of tarjan algorithm is started from 1
    }

    Integer addNode(String node) {
        Integer id;
        if(!this.name_to_id.containsKey(node)) {
            id = this.id;
            name_to_id.put(node, this.id);
            id_to_name.put(this.id++, node);
        } else {
            id = name_to_id.get(node);
        }
        return id;
    }

    void addEdge(String src, String dst) {
        this.nodes.add(src);
        this.nodes.add(dst);
        this.links.add(new SimpleLink(src, dst));
    }

    // Step1. Construct new BC graph.
    void constructBCTree() {
        tarjan.setNodeNum(this.nodes.size());
        tarjan.setLinkNum(this.links.size());
        tarjan.resize();

        this.links.forEach(link -> {
            tarjan.addEdge(addNode(link.src_name), addNode(link.dst_name));
            tarjan.addEdge(addNode(link.dst_name), addNode(link.src_name));
        });

        // run tarjan find all 'point bi-connected components' and 'cut points'
        tarjan.tarjanRun();

        // map node id (int) to device name (string)
        tarjan_cut_points = (HashSet<String>) tarjan.getCutPoints()
                .stream()
                .map(key -> id_to_name.get(key))
                .collect(Collectors.toSet());
        tarjan_connected_components = new HashMap<>();
        tarjan.getConnectComponents().forEach((key, value) ->
                tarjan_connected_components.put(key,
                        (HashSet<String>) value
                                .stream()
                                .map(node -> id_to_name.get(node))
                                .collect(Collectors.toSet())));

        // construct a new graph composed by 'point bi-connected components' and 'cut points'
        BCTree.constructBCTree(this.tarjan_connected_components, this.tarjan_cut_points);

        // update the links in each 'components'
        BCTree.updateBCTreeLinks(this.links);
    }

    //Step2. query relevant links.
    HashSet<SimpleLink> getRelevantLinks(String src, String dst) {
        return BCTree.getRelevantLinks(src, dst);
    }

    void display() {
        System.out.println("the graph is : ");
        System.out.println(this.links);

        System.out.println("the cut points are : ");
        System.out.println(this.tarjan_cut_points);

        System.out.println("the connected components are : ");
        System.out.println(this.tarjan_connected_components);
    }
}
