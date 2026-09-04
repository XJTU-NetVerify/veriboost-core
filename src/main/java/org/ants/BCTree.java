package org.ants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.ants.VeriBoostUtil.NodeType;
import org.ants.VeriBoostUtil.BCComponent;
import org.ants.VeriBoostUtil.BCCut;
import org.ants.VeriBoostUtil.BCNode;
import org.ants.VeriBoostUtil.SimpleLink;

public class BCTree {
    HashMap<Integer, BCComponent> block_cut_tree_components; // component_id -> connected_components
    HashMap<String, BCCut> block_cut_tree_cuts;  // node_name -> cut_points

    BCTree() {
        block_cut_tree_components = new HashMap<>();
        block_cut_tree_cuts = new HashMap<>();
    }

    BCCut getBCCut(String node) {
        if(!this.block_cut_tree_cuts.containsKey(node)) {
            block_cut_tree_cuts.put(node, new BCCut(node));
        }
        return block_cut_tree_cuts.get(node);
    }

    BCComponent getBCComponent(Integer id) {
        if(!this.block_cut_tree_components.containsKey(id)) {
            block_cut_tree_components.put(id, new BCComponent(id));
        }
        return block_cut_tree_components.get(id);
    }

    void constructBCTree(HashMap<Integer, HashSet<String>> connected_components, HashSet<String> cut_points) {
        connected_components.forEach((ct_id, nodes) -> {
            BCComponent curr_block_cut_tree_component = getBCComponent(ct_id);
            nodes.forEach(node -> {
                curr_block_cut_tree_component.addNode(node);
                getBCCut(node).addBCComponent(curr_block_cut_tree_component);
                if(cut_points.contains(node)) {
                    curr_block_cut_tree_component.addBCCut(getBCCut(node));
                }
            });
        });
    }

    void updateBCTreeLinks(HashSet<SimpleLink> links) {
        links.forEach(link -> {
            String src = link.src_name;
            String dst = link.dst_name;
            Set<Integer> src_sets =  getBCCut(src).adj_block_cut_tree_components.stream()
                    .map(graph -> graph.id).collect(Collectors.toSet());
            Set<Integer> dst_sets =  getBCCut(dst).adj_block_cut_tree_components.stream()
                    .map(graph -> graph.id).collect(Collectors.toSet());
            src_sets.retainAll(dst_sets);
            assert (src_sets.size() == 1);
            int ct_id = src_sets.iterator().next();
            getBCComponent(ct_id).addLink(link);
        });
    }

    void dfsRelevantBCNodes(BCNode cur_node, String dst_node,
                                     HashSet<Integer> ans_components, ArrayList<BCNode> path,
                                     HashSet<BCNode> visit) {
        // Note that 'BC graph' have two types of node
        if(cur_node.type == NodeType.BC_cut) {
            for(BCComponent node : cur_node.BCCut.adj_block_cut_tree_components) {
                if(!ans_components.isEmpty()) {
                    return ;
                }
                BCNode bsc_node = new BCNode(node);
                if(!visit.contains(bsc_node)) {
                    visit.add(bsc_node);
                    path.add(bsc_node);
                    dfsRelevantBCNodes(new BCNode(node), dst_node, ans_components, path, visit);
                    path.remove(path.size() - 1);
                }
            }
        }
        if(cur_node.type == NodeType.BC_component) {
            // end dfs when a component contain 'dst_node'
            if(cur_node.BCComponent.nodes.contains(dst_node)) {
                ans_components.addAll(path.stream()
                        .filter(bsc_node -> bsc_node.type == NodeType.BC_component)
                        .map(bsc_node -> bsc_node.BCComponent.id).collect(Collectors.toSet()));
                return ;
            }
            for(BCCut node : cur_node.BCComponent.adj_block_cut_tree_cuts) {
                if(!ans_components.isEmpty()) {
                    return ;
                }
                BCNode bsc_node = new BCNode(node);
                if(!visit.contains(bsc_node)) {
                    visit.add(bsc_node);
                    path.add(bsc_node);
                    dfsRelevantBCNodes(new BCNode(node), dst_node, ans_components, path, visit);
                    path.remove(path.size() - 1);
                }
            }

        }
    }

    HashSet<SimpleLink> getRelevantLinks(String src, String dst) {
        // Step1. find the src node in 'BC graph'
        HashSet<SimpleLink> links = new HashSet<>();
        BCCut src_node = getBCCut(src);

        // Step2. use dfs to traverse until find a component contain 'dst'
        HashSet<Integer> ans_components = new HashSet<>();
        ArrayList<BCNode> path = new ArrayList<>();
        HashSet<BCNode> visit = new HashSet<>();
        BCNode bsc_node = new BCNode(src_node);
        visit.add(bsc_node);
        dfsRelevantBCNodes(bsc_node, dst, ans_components, path, visit);

        // Step3. add all links in relevant components
        ans_components.forEach(id -> links.addAll(getBCComponent(id).links));
        return links;
    }
}
