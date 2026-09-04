package org.ants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.ants.VeriBoostUtil.NetworkGraph;
import org.ants.VeriBoostUtil.SimpleLink;

//
// Created by Charlie on 2024/3/18.
// XJTU | 916267142@qq.com
//
// This function is used to group link by degree,
// Step1. if two link are connected by one node and the degree is 2, and we think the two link are same group.
// Step2. give src and dst, select the links that are directly connected to src and dst.
// Step3. select one link for each rest link group.

public class TrivialPathCore extends NetworkGraph {

    // The following information is used to group the redundancy links
    int group_id;

    private final HashMap<SimpleLink, Integer> link_to_id;

    private final HashMap<String, Integer> device_to_id;

    private final HashMap<Integer, HashSet<SimpleLink>> id_to_links;


    TrivialPathCore() {
        group_id = 0;
        link_to_id = new HashMap<>();
        device_to_id = new HashMap<>();
        id_to_links = new HashMap<>();
    }

    public NetworkGraph getGraphByDegreePrune(NetworkGraph networkGraph, String src, String dst) {
        // Step1. group links and nodes
        networkGraph.getLinks().forEach(this::addBidirectionalEdge);
        groupLinks();

        // Step2. add the link that are directly connected to src node and dst node
        NetworkGraph new_network_graph = new NetworkGraph();
        HashSet<Integer> visit_id = new HashSet<>();
        getGraph().forEach((key, value) -> value.forEach((key1, link) -> {
            SimpleLink simple_link = new SimpleLink(link);
            Integer group_id = this.link_to_id.get(simple_link);
            if (link.contain(src) || link.contain(dst)) {
                new_network_graph.addBidirectionalEdge(link);
                visit_id.add(group_id);
            }
        }));

        // Step3. add the link from each group
        getGraph().forEach((key, value) -> value.forEach((key1, link) -> {
            SimpleLink simple_link = new SimpleLink(link);
            Integer group_id = this.link_to_id.get(simple_link);
            if (!visit_id.contains(group_id)) {
                new_network_graph.addBidirectionalEdge(link);
                visit_id.add(group_id);
            }
        }));
        return new_network_graph;
    }

    public void groupLinks() {
        HashSet<String> visit_device = new HashSet<>();
        // traverse each device, if the degree of the device is 2, then dfs the neighbor node.
        this.devices.forEach((device_name, device_object) -> {
            if (visit_device.contains(device_name)) {
                return;
            }
            if (device_object.get_degree() == 1 || device_object.get_degree() == 2) {
                HashSet<String> device_group = new HashSet<>();
                HashSet<SimpleLink> link_group = new HashSet<>();
                dfsGraphByDegree(device_name, visit_device, device_group, link_group);
                this.group_id++;
                device_group.forEach(device -> this.device_to_id.put(device, this.group_id));
                link_group.forEach(link -> this.link_to_id.put(link, this.group_id));
                this.id_to_links.put(this.group_id, link_group);
            }
        });
        // add the rest link.
        this.graph.forEach((key, value) -> value.forEach((key1, link) -> {
            SimpleLink simple_link = new SimpleLink(link);
            if (!link_to_id.containsKey(simple_link)) {
                this.group_id++;
                this.link_to_id.put(simple_link, this.group_id);
                this.id_to_links.put(this.group_id, new HashSet<>());
                this.id_to_links.get(group_id).add(simple_link);
            }
        }));
    }

    void dfsGraphByDegree(String device_name, HashSet<String> visit_device,
                          HashSet<String> device_group, HashSet<SimpleLink> link_group) {
        if (visit_device.contains(device_name)) {
            return;
        }
        visit_device.add(device_name);
        VeriBoostUtil.Device device_object = this.devices.get(device_name);
        if (device_object.get_degree() != 1 && device_object.get_degree() != 2) {
            return;
        }
        device_group.add(device_name);
        this.graph.get(device_name).forEach((next_device_name, simple_link) -> {
            link_group.add(new SimpleLink(simple_link.from_interface.device_name,
                    simple_link.to_interface.device_name));
            dfsGraphByDegree(next_device_name, visit_device, device_group, link_group);
        });
    }

    void displayGroupByDegreeResults() {
        System.out.println(device_to_id);
        System.out.println(id_to_links);
        for (Map.Entry<Integer, HashSet<SimpleLink>> entry : id_to_links.entrySet()) {
            System.out.print(entry.getKey() + " ");
            System.out.println(entry.getValue());
        }
    }
}
