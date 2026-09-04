# VeriBoost: A General Acceleration Approach for Fault-Tolerance Verification in Wide-Area Networks

by [Ning Kang](https://xjtu-netverify.github.io/people/nkang/), [Peng Zhang](https://xjtu-netverify.github.io/people/pzhang/), [Hao Li](https://haolis.com/), [Jianyuan Zhang](https://xjtu-netverify.github.io/people/jyzhang/)

![Java](https://img.shields.io/badge/Java-8-007396?logo=java&logoColor=white) ![Tests](https://img.shields.io/badge/tests-passing-brightgreen?logo=java) ![Paper](https://img.shields.io/badge/paper-TSE2026-orange) ![License](https://img.shields.io/badge/license-Apache--2.0-green)

## Overview

*VeriBoost* is a tool designed to accelerate fault-tolerance verification for wide-area networks (WANs). It is verifier-agnostic and is provided as a Maven JAR package that can be integrated with various types of network verifiers, including SMT-based, simulation-based, and graph-based verifiers.

*VeriBoost* leverages the topology characteristics of WANs to reduce the failure-scenario space, thereby improving the scalability of network verification. Specifically, *VeriBoost* reduces the failure-scenario space by:

* **Pruning** links whose failures are irrelevant to the property.
* **Compressing** links whose failures have an equivalent impact on the property.

This repository provides instructions for compiling *VeriBoost* and introduces its APIs. To demonstrate how to integrate *VeriBoost* with network verifiers, we provide an example of applying *VeriBoost* to an SMT-based verifier, Minesweeper [SIGCOMM'17]. See the [repository](https://github.com/XJTU-NetVerify/veriboost) for details.

---

## Requirements

- Linux Ubuntu 22.04 LTS
- Java JDK >= 8
- Maven 3.6.3
  
## Packaging and Installing VeriBoost Locally

To build the *VeriBoost* project and install the generated JAR file into your local Maven repository, follow the steps below:

* Package the project:

```bash
   mvn clean package
```

This will generate a JAR file in the `target/` directory, for example: `veriboost-core-1.0.jar`.

* Test whether the jar package is generated correctly.

```bash
java -jar target/veriboost-core-1.0.jar src/test/resources panamattcity raleigh
```

* Install the JAR into your local Maven repository:

```bash
mvn install:install-file \
    -Dfile=target/veriboost-core-1.0.jar \
    -DgroupId=org.ants \
    -DartifactId=veriboost-core \
    -Dversion=1.0 \
    -Dpackaging=jar \
    -DgeneratePom=true
```

Add the following dependency into the pom.xml of verifiers.

```java
<dependency>
    <groupId>org.ants</groupId>
    <artifactId>veriboost-core</artifactId>
    <version>1.0</version>
</dependency>
```

## Using VeriBoost

For a given property, *VeriBoost* classifies links into three categories:

- `down links`: Links whose status is set to *down*. These links are irrelevant to the property and do not need to be considered during verification.
- `up links`: Links whose status is set to *up*. These links are equivalent with respect to the property and their failures have same impact on the verification result.
- `symbolic links`: Links whose status remains *symbolic*. These links may affect the property and are considered during verification.

The link statuses are pre-assigned before verification. The resulting topology with these predefined link statuses is then provided to existing verifiers, which only need to enumerate failure scenarios over the `symbolic links` instead of all links.

### Example

Below is a basic example to demonstrate the usage of *VeriBoost* in your Java application:

```Java
package org.ants;
import org.ants.VeriBoostUtil.Link;
import javafx.util.Pair;
import java.util.HashSet;

public class Example {
    public static void main(String[] args) {
        VeriBoost veriBoost = new VeriBoost();

        // Step 1: Load the network topology from a file or use the addLinks method.
        veriBoost.readTopologyFromFile("dataset/uscarrier/topology.txt");

        // Step 2: Build internal structures
        veriBoost.buildComponent();

        // Mimics three properties.
        HashSet<Pair<String, String>> properties = new HashSet<>();
        properties.add(new Pair<String,String>("blueridge", "sylva"));
        properties.add(new Pair<String,String>("atlanta", "greensboro"));
	    properties.add(new Pair<String,String>("kingsport", "marion"));

        for(Pair<String, String> property : properties) {
            // Step 3: Calculate constraints between source node and desination node.
            String srcNode = property.getKey();
            String dstNode = property.getValue();
            veriBoost.calculateLinkStatus(srcNode, dstNode);
     
            // Step 4: Query constraints by link type
            HashSet<Link> upLinks = veriBoost.getUpLinks();
            HashSet<Link> downLinks = veriBoost.getDownLinks();
            HashSet<Link> symbolicLinks = veriBoost.getSymbolicLinks();

            // Step 5: According to the type of verifiers, apply VeriBoost. 
            // For example, SMT-based, simulation-based, hybrid-based, or graph-based verifiers.
            System.out.println("property: " + srcNode + " -> " + dstNode
                + ", symbolicinks: " + (symbolicLinks == null ? 0 : symbolicLinks.size() / 2)
                + ", downLinks: " + (downLinks == null ? 0 : downLinks.size() / 2)
                + ", upLinks: " + (upLinks == null ? 0 : upLinks.size() / 2)) ;
        }
    }
}
```

Run the following command to automatically execute the above example:
```java
java -cp target/veriboost-core-1.0.jar org.ants.Example
```

The command will produce the following output:
```log
property: kingsport -> marion, symbolicinks: 3, downLinks: 200, upLinks: 2
property: atlanta -> greensboro, symbolicinks: 61, downLinks: 79, upLinks: 65
property: blueridge -> sylva, symbolicinks: 5, downLinks: 192, upLinks: 8
```

### API of VeriBoost

The `VeriBoost` class provides the following functions:

- `addLink(Link)` adds a link to the network topology maintained by *VeriBoost*. A `Link` is represented as a quadruple `(source device, source port, destination device, destination port)`.

- `readTopologyFromFile(File)` initializes the network topology by loading links from a file. Internally, this function repeatedly invokes `addLink(Link)` to construct the topology.

- `buildComponent()` preprocesses the topology and constructs point biconnected components, avoiding repeated computations of point biconnected components.

- `calLinkStatus(Source, Destination)` enables *VeriBoost* to compute the status of links between two endpoints and classify them into three categories: **down**, **up**, and **symbolic**.

- `getSymbolicLinks()`, `getUpLinks()`, and `getDownLinks()` return the corresponding link sets. These results can be directly used by different types of verifiers to reduce the failure-scenario space during verification.

## Developer

Ning Kang (XJTU | kangning2018@foxmail.com)