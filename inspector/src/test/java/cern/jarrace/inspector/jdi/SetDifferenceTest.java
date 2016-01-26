/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.jdi;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static cern.jarrace.inspector.jdi.ClassInstantiationListener.differenceLeft;
import static org.junit.Assert.assertEquals;

public class SetDifferenceTest {

    @Test
    public void canFindDifferenceBetweenTwoEmptySets() {
        assertEquals(Collections.emptySet(),
                differenceLeft(Collections.emptySet(), Collections.emptySet()));
    }

    @Test
    public void canFindSetDifferenceWhenLeftIsEmpty() {
        Set<Integer> left = Collections.emptySet();
        Set<Integer> right = Collections.singleton(2);
        assertEquals(right, differenceLeft(left, right));
    }

    @Test
    public void canFindSetDifferenceWhenRightIsEmpty() {
        Set<Integer> left = Collections.singleton(4);
        Set<Integer> right = Collections.emptySet();
        assertEquals(right, differenceLeft(left, right));
    }

    @Test
    public void canFindSetDifferenceWhenBothSetsAreFull() {
        Set<Integer> left = Collections.singleton(6);
        Set<Integer> right = Collections.singleton(3);
        assertEquals(right, differenceLeft(left, right));
    }

    @Test
    public void canFindSetDifferenceBetweenManyElements() {
        Set<Integer> left = new HashSet<>();
        Set<Integer> right = new HashSet<>();
        left.add(201);
        left.add(8932);
        left.add(-8832);
        right.add(780);
        right.add(-92);
        right.add(97892);
        right.add(767672);
        assertEquals(right, differenceLeft(left, right));
    }

}
