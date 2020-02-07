package io.molr.mole.jdebug.spawner;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;

public class ConnectorInspector {


    public static void println(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();

        vmm.allConnectors().forEach(c -> {
            println(c.name() + ":");
            println("  " + c.description());
            c.defaultArguments().forEach((k, v) -> {
                println("    " + v.name());
                println("      label:    " + v.label());
                println("      required: " + v.mustSpecify());
                println("      default:  " + v.value());
                println("      desc:     " + v.description());
            });
            println("");
        });

        println("Default Connector:  " + vmm.defaultConnector().name());
    }
}

