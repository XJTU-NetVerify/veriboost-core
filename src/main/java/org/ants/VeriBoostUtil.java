package org.ants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//
// Created by Charlie on 2024/3/18.
// XJTU | 916267142@qq.com
//
// Some common data structure for other algorithm.
//

public class VeriBoostUtil {

    // The following class is maintaining the global topology information of network.
    public static class NetworkGraph {
        public int link_size;

        public HashMap<String, Device> devices;

        public HashSet<Interface> interfaces;

        public HashMap<String, HashMap<String, Link>> graph;

        NetworkGraph() {
            devices = new HashMap<>();
            interfaces = new HashSet<>();
            graph = new HashMap<>();
            link_size = 0;
        }

        HashSet<Link> getLinks() {
            HashSet<Link> links = new HashSet<>();
            graph.forEach((src, dst) -> dst.forEach((node, link) -> links.add(link)));
            return links;
        }

        void addBidirectionalEdge(Link link) {
            addEdge(link);
            addEdge(new Link(link.to_interface, link.from_interface));
        }

        public boolean containEdge(String src, String dst) {
            return graph.containsKey(src) && graph.containsKey(dst);
        }

        void addEdge(Link link) {
            Interface from_interface = link.from_interface;
            Interface to_interface = link.to_interface;
            if (from_interface == to_interface) {
                return;
            }
            this.interfaces.add(from_interface);
            this.interfaces.add(to_interface);

            String from_device_name = from_interface.device_name;
            String to_device_name = to_interface.device_name;
            if (!devices.containsKey(from_device_name)) {
                devices.put(from_device_name, new Device(from_device_name));
            }
            if (!devices.containsKey(to_device_name)) {
                devices.put(to_device_name, new Device(to_device_name));
            }
            devices.get(from_device_name).add_link(link);

            if (!graph.containsKey(from_device_name)) {
                graph.put(from_device_name, new HashMap<>());
            }
            if (!graph.get(from_device_name).containsKey(to_device_name)) {
                graph.get(from_device_name).put(to_device_name, link);
                link_size++;
            }

        }
        public void setGraph(HashMap<String, HashMap<String, Link>> graph) {
            this.graph = graph;
        }

        public HashMap<String, HashMap<String, Link>> getGraph() {
            return graph;
        }

        public String displayDegree() {
            int count = 0;
            StringBuilder display = new StringBuilder();
            display.append("Device size : ").append(devices.size()).append("\n");
            for (HashMap.Entry<String, Device> entry : devices.entrySet()) {
                display.append(entry.getValue()).append("\n");
                if (entry.getValue().get_degree() != 2) {
                    count++;
                }
            }
            System.out.println(count);
            return display.toString();
        }

        public String toString() {
            StringBuilder display = new StringBuilder();
            display.append("Interface size : ").append(interfaces.size()).append("\n");
            display.append("link size : ").append(link_size).append("\n");
            for (String from_device : graph.keySet()) {
                for (String to_device : graph.get(from_device).keySet()) {
                    display.append(graph.get(from_device).get(to_device)).append("\n");
                }
            }
            return display.toString();
        }
    }

    public static class Device {

        String device_name;

        public HashMap<Interface, Link> interfaces;

        Device(String device_name) {
            this.device_name = device_name;
            this.interfaces = new HashMap<>();
        }

        void add_link(Link link) {
            if (link.from_interface.device_name.equals(device_name)) {
                this.interfaces.put(link.from_interface, link);
            }
        }

        Integer get_degree() {
            return interfaces.size();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Device device = (Device) o;

            return Objects.equals(device_name, device.device_name);
        }

        @Override
        public int hashCode() {
            return device_name != null ? device_name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Device{" +
                "device_name='" + device_name + '\'' +
                "node_degree='" + interfaces.size() + '\'' +
                ", interfaces=" + interfaces +
                '}';
        }
    }

    public static class Interface {

        String device_name;
        String interface_name;
        Device device;
        Link link;

        public Interface(String device_name) {
            this.device_name = device_name;
            this.interface_name = "";
        }

        public Interface(String device_name, String interface_name) {
            this.device_name = device_name;
            this.interface_name = interface_name;
        }

        public void set_link(Link link) {
            this.link = link;
        }

        public void set_device(Device device) {
            this.device = device;
        }

        @Override
        public String toString() {
            return "[" + this.device_name + " " + this.interface_name + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Interface Interface = (Interface) o;

            if (!Objects.equals(device_name, Interface.device_name)) {
                return false;
            }
            return Objects.equals(interface_name, Interface.interface_name);
        }

        @Override
        public int hashCode() {
            int result = device_name != null ? device_name.hashCode() : 0;
            result = 31 * result + (interface_name != null ? interface_name.hashCode() : 0);
            return result;
        }
    }

    public static class Link {

        Interface from_interface;
        Interface to_interface;

        public Link(Interface from_interface, Interface to_interface) {
            this.from_interface = from_interface;
            this.to_interface = to_interface;
        }

        public Link(SimpleLink simpleLink) {
            this.from_interface = new Interface(simpleLink.src_name);
            this.to_interface = new Interface(simpleLink.dst_name);
        }

        public boolean contain(String device) {
            return this.from_interface.device_name.equals(device)
                || this.to_interface.device_name.equals(device);
        }

        public String toString() {
            return this.from_interface + "->" + this.to_interface;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Link link = (Link) o;

            if (!Objects.equals(from_interface, link.from_interface)) {
                return false;
            }
            return Objects.equals(to_interface, link.to_interface);
        }

        @Override
        public int hashCode() {
            int result = from_interface != null ? from_interface.hashCode() : 0;
            result = 31 * result + (to_interface != null ? to_interface.hashCode() : 0);
            return result;
        }
    }

    public static class SimpleLink {

        public String src_name;
        public String dst_name;

        public SimpleLink(String src_name, String dst_name) {
            if (src_name.compareTo(dst_name) < 0) {
                this.src_name = src_name;
                this.dst_name = dst_name;
            } else {
                this.src_name = dst_name;
                this.dst_name = src_name;
            }
        }

        public String getFirst() {
            return src_name;
        }

        public String getSecond() {
            return dst_name;
        }

        SimpleLink(Link link) {
            if (link.from_interface.device_name.compareTo(link.to_interface.device_name) < 0) {
                this.src_name = link.from_interface.device_name;
                this.dst_name = link.to_interface.device_name;
            } else {
                this.src_name = link.to_interface.device_name;
                this.dst_name = link.from_interface.device_name;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SimpleLink that = (SimpleLink) o;

            if (!Objects.equals(src_name, that.src_name)) {
                return false;
            }
            return Objects.equals(dst_name, that.dst_name);
        }

        @Override
        public int hashCode() {
            int result = src_name != null ? src_name.hashCode() : 0;
            result = 31 * result + (dst_name != null ? dst_name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "SimpleLink{" +
                "src_name='" + src_name + '\'' +
                ", dst_name='" + dst_name + '\'' +
                '}';
        }
    }

    public enum LinkType {
        symbolic_link,
        down_link,
        up_link
    }

    // The following data structure is for CompoPrune Algorithm.
    // The BC graph of the original graph has two types of points.
    // One type is the cut-point (device in network),
    // and another type is point bi-direction components.

    public enum NodeType {
        BC_cut,
        BC_component
    }

    static class BCNode {
        NodeType type;

        BCCut BCCut;

        BCComponent BCComponent;

        BCNode(BCCut BCCut) {
            this.BCCut = BCCut;
            this.type = NodeType.BC_cut;
        }

        BCNode(BCComponent BCComponent) {
            this.BCComponent = BCComponent;
            this.type = NodeType.BC_component;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BCNode BCNode = (BCNode) o;
            if(this.type.equals(NodeType.BC_cut)) {
                return Objects.equals(BCCut, BCNode.BCCut);
            } else {
                return Objects.equals(BCComponent, BCNode.BCComponent);
            }
        }

        @Override
        public int hashCode() {
            if(this.type.equals(NodeType.BC_cut)) {
                return Objects.hash(BCCut);
            } else {
                return Objects.hash(BCComponent);
            }
        }
    }

    static class BCCut {
        String name;

        HashSet<BCComponent> adj_block_cut_tree_components;

        BCCut(String name) {
            this.name = name;
            this.adj_block_cut_tree_components = new HashSet<>();
        }

        void addBCComponent(BCComponent graph) {
            this.adj_block_cut_tree_components.add(graph);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BCCut that = (BCCut) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "ComponentNode{" +
                    "name='" + name +
                    '}';
        }
    }

    static class BCComponent {
        Integer id;

        Set<BCCut> adj_block_cut_tree_cuts;

        HashSet<String> nodes;
        HashSet<SimpleLink> links;

        BCComponent(Integer id) {
            this.id = id;
            this.nodes = new HashSet<>();
            this.links = new HashSet<>();
            this.adj_block_cut_tree_cuts = new HashSet<>();
        }
        void addBCCut(BCCut node) {
            this.adj_block_cut_tree_cuts.add(node);
            node.addBCComponent(this);
        }

        void addNode(String node) {
            this.nodes.add(node);
        }

        void addLink(SimpleLink link) {
            this.links.add(link);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BCComponent that = (BCComponent) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "ComponentGraph{" +
                    "id=" + id +
                    ", adj_component_nodes=" + adj_block_cut_tree_cuts +
                    ", nodes=" + nodes +
                    ", links=" + links +
                    '}'+'\n';
        }
    }
}
