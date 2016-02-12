/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector;

/**
 * A class that can be used for testing the use of an inspectable class.
 */
public class TestInspectable {

    public void run() {
        System.out.println("Test");
    }

    public static void main(String[] args) {
        new TestInspectable().run();
    }

}
