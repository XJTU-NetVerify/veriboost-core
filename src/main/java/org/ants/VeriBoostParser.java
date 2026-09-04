package org.ants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.ants.VeriBoostUtil.Link;

//
// Created by Charlie on 2024/3/18.
// XJTU | 916267142@qq.com
//

public class VeriBoostParser {
    public HashSet<Link> links;

    public VeriBoostParser() {
        links = new HashSet<>();
    }

    public HashSet<Link> getLinks() {
        return links;
    }

    public void setLinks(HashSet<Link> links) {
        this.links = links;
    }

    /**
     * Reads topology from file with support for multiple formats:
     * Format 1 (original): <src_device:src_interface, dst_device:dst_interface>
     *   Example: <atlanta:FastEthernet0/1, birmingham:FastEthernet0/0>
     * Format 2 (new): src_device src_interface dst_device dst_interface
     *   Example: atlanta FastEthernet0/1 birmingham FastEthernet0/0
     * 
     * @param file_path The path to the topology file
     */
    public void readTopologyFromFile(String file_path) {
        try {
            File file = new File(file_path);
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    // Remove extra whitespace
                    line = line.trim();

                    // Skip empty lines
                    if (line.isEmpty()) {
                        continue;
                    }

                    // Check which format is being used
                    if (line.startsWith("<") && line.contains(":") && line.contains(",")) {
                        // Original format: <src_device:src_interface, dst_device:dst_interface>
                        parseOriginalFormat(line);
                    } else if (!line.startsWith("<") && !line.contains(",") && 
                            !line.contains(":") && line.split("\\s+").length >= 4) {
                        // New format: src_device src_interface dst_device dst_interface
                        parseNewFormat(line);
                    } else {
                        System.out.println("Unrecognized format in line: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

   /**
     * Reads topology from an InputStream with support for multiple formats:
     * Format 1 (original): <src_device:src_interface, dst_device:dst_interface>
     *   Example: <atlanta:FastEthernet0/1, birmingham:FastEthernet0/0>
     * Format 2 (new): src_device src_interface dst_device dst_interface
     *   Example: atlanta FastEthernet0/1 birmingham FastEthernet0/0
     * @param inputStream The input stream containing topology data
     */
    public void readTopologyFromStream(InputStream inputStream) {
        try (Scanner sc = new Scanner(inputStream)) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                // Remove extra whitespace
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Check which format is being used
                if (line.startsWith("<") && line.contains(":") && line.contains(",")) {
                    // Original format: <src_device:src_interface, dst_device:dst_interface>
                    parseOriginalFormat(line);
                } else if (!line.startsWith("<") && !line.contains(",") && 
                        !line.contains(":") && line.split("\\s+").length >= 4) {
                    // New format: src_device src_interface dst_device dst_interface
                    parseNewFormat(line);
                } else {
                    System.out.println("Unrecognized format in line: " + line);
                }
            }
        }
    }

    /**
     * Parses the original format: <src_device:src_interface, dst_device:dst_interface>
     * @param line The line to parse
     */
    private void parseOriginalFormat(String line) {
        // Remove angle brackets
        line = line.replace("<", "").replace(">", "").trim();
        
        // Split by comma
        StringTokenizer str = new StringTokenizer(line, ",", false);
        if (str.countTokens() != 2) {
            System.out.println("Exception reading line: " + line);
            return;
        }
        
        String from = str.nextToken().trim();
        String to = str.nextToken().trim();
        
        StringTokenizer from_str = new StringTokenizer(from, ":", false);
        StringTokenizer to_str = new StringTokenizer(to, ":", false);
        
        if (from_str.countTokens() != 2 || to_str.countTokens() != 2) {
            System.out.println("Exception reading line: " + line);
            return;
        }
        
        // VeriBoostUtil.Interface from_Interface = new VeriBoostUtil.Interface(
        //     from_str.nextToken().trim(), from_str.nextToken().trim());
        // VeriBoostUtil.Interface to_Interface = new VeriBoostUtil.Interface(
        //     to_str.nextToken().trim(), to_str.nextToken().trim());
            
        // VeriBoostUtil.Link link = new VeriBoostUtil.Link(from_Interface, to_Interface);
        // this.links.add(link);

        // this.addLinks(from_str.nextToken().trim(), from_str.nextToken().trim(), to_str.nextToken().trim(), to_str.nextToken().trim());
        // this.addLinks(from_str.nextToken().trim(), "empty", to_str.nextToken().trim(), "empty");
        this.addLinks(from_str.nextToken().trim(), from_str.nextToken().trim(), to_str.nextToken().trim(), to_str.nextToken().trim());
    }

    /**
     * Parses the new format: src_device src_interface dst_device dst_interface
     * @param line The line to parse
     */
    private void parseNewFormat(String line) {
        StringTokenizer str = new StringTokenizer(line, " ", false);
        
        // Should have at least 4 tokens (src_device, src_interface, dst_device, dst_interface)
        // Extra tokens are ignored
        if (str.countTokens() < 4) {
            System.out.println("Exception reading line: " + line);
            return;
        }
        
        String srcDevice = str.nextToken();
        String srcInterface = str.nextToken();
        String dstDevice = str.nextToken();
        String dstInterface = str.nextToken();
        
        // VeriBoostUtil.Interface from_Interface = new VeriBoostUtil.Interface(
        //     srcDevice, srcInterface);
        // VeriBoostUtil.Interface to_Interface = new VeriBoostUtil.Interface(
        //     dstDevice, dstInterface);
            
        // VeriBoostUtil.Link link = new VeriBoostUtil.Link(from_Interface, to_Interface);
        // this.links.add(link);

        this.addLinks(srcDevice, srcInterface, dstDevice, dstInterface);
    }
    // /**
    //  * Reads topology from an InputStream with the same format as readTopologyFromFile
    //  * @param inputStream The input stream containing topology data
    //  */
    // public void readTopologyFromStream(InputStream inputStream) {
    //     try (Scanner sc = new Scanner(inputStream)) {
    //         while (sc.hasNext()) {
    //             String line = sc.nextLine();
    //             // Remove special characters and whitespace
    //             line = line.replace("<", "")
    //                     .replace(">", "")
    //                     .replace(" ", "");

    //             // Parse the line into from and to interfaces
    //             StringTokenizer str = new StringTokenizer(line, ",", false);
    //             if (str.countTokens() != 2) {
    //                 System.out.println("exception of reading lines");
    //                 continue;
    //             }
                
    //             String from = str.nextToken();
    //             String to = str.nextToken();
                
    //             StringTokenizer from_str = new StringTokenizer(from, ":", false);
    //             StringTokenizer to_str = new StringTokenizer(to, ":", false);
                
    //             VeriBoostUtil.Interface from_Interface = new VeriBoostUtil.Interface(
    //                 from_str.nextToken(),
    //                 from_str.nextToken()
    //             );
                
    //             VeriBoostUtil.Interface to_Interface = new VeriBoostUtil.Interface(
    //                 to_str.nextToken(),
    //                 to_str.nextToken()
    //             );
                
    //             VeriBoostUtil.Link link = new VeriBoostUtil.Link(from_Interface, to_Interface);
    //             this.links.add(link);
    //         }
    //     }
    // }
    
    public void addLinks(String srcDevice, String dstDevice) {
        this.links.add(new VeriBoostUtil.Link(new VeriBoostUtil.Interface(srcDevice, "none"), new VeriBoostUtil.Interface(dstDevice, "none")));
    }

    public void addLinks(String srcDevice, String srcInterface, String dstDevice, String dstInterface) {
        this.links.add(new VeriBoostUtil.Link(new VeriBoostUtil.Interface(srcDevice, srcInterface), new VeriBoostUtil.Interface(dstDevice, dstInterface)));
    }
}
