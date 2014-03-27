package ru.benchmark.deltaBlue.code;
/*
 * CL_SUN_COPYRIGHT_JVM_BEGIN
 *   If you or the company you represent has a separate agreement with both
 *   CableLabs and Sun concerning the use of this code, your rights and
 *   obligations with respect to this code shall be as set forth therein. No
 *   license is granted hereunder for any other purpose.
 * CL_SUN_COPYRIGHT_JVM_END
*/

/*
 * @(#)DeltaBlue.java	1.6 06/10/10
 *
 * Copyright  1990-2006 Sun Microsystems, Inc. All Rights Reserved.  
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER  
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at /legal/license.txt).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *   
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa  
 * Clara, CA 95054 or visit www.sun.com if you need additional  
 * information or have any questions. 
 *
 */
/*

  This is a Java implemention of the DeltaBlue algorithm described in:
    "The DeltaBlue Algorithm: An Incremental Constraint Hierarchy Solver"
    by Bjorn N. Freeman-Benson and John Maloney
    January 1990 Communications of the ACM,
    also available as University of Washington TR 89-08-06.

  This implementation by Mario Wolczko, Sun Microsystems, Sep 1996,
  based on the Smalltalk implementation by John Maloney.

*/

//package COM.sun.labs.kanban.DeltaBlue;

import java.util.ArrayList;

public class DeltaBlue {

    private long total_ms;
    public long getRunTime() { return total_ms; }

    public static Planner planner;



    //  This is the standard DeltaBlue ru.benchmark. A long chain of
    //  equality constraints is constructed with a stay constraint on
    //  one end. An edit constraint is then added to the opposite end
    //  and the time is measured for adding and removing this
    //  constraint, and extracting and executing a constraint
    //  satisfaction plan. There are two cases. In case 1, the added
    //  constraint is stronger than the stay constraint and values must
    //  propagate down the entire length of the chain. In case 2, the
    //  added constraint is weaker than the stay constraint so it cannot
    //  be accomodated. The cost in this case is, of course, very
    //  low. Typical situations lie somewhere between these two
    //  extremes.
    //
    public void chainTest(int n)
    {
        planner = new Planner();

        Variable prev= null, first= null, last= null;

        // Build chain of n equality constraints
        for (int i= 0; i <= n; i++) {
            String name= "v" + Integer.toString(i);
            Variable v= new Variable(name);
            if (prev != null)
                new EqualityConstraint(prev, v, Strength.required);
            if (i == 0) first= v;
            if (i == n) last= v;
            prev= v;
        }

        new StayConstraint(last, Strength.strongDefault);
        Constraint editC= new EditConstraint(first, Strength.preferred);
        ArrayList<Constraint> editV= new ArrayList<Constraint>();
        editV.add(editC);
        Plan plan= planner.extractPlanFromConstraints(editV);
        for (int i= 0; i < 100; i++) {
            first.value= i;
            plan.execute();
            if (last.value != i)
                error("Chain test failed!");
        }
        editC.destroyConstraint();
    }


    // This test constructs a two sets of variables related to each
    // other by a simple linear transformation (scale and offset). The
    // time is measured to change a variable on either side of the
    // mapping and to change the scale and offset factors.
    //
    public void projectionTest(int n)
    {
        planner= new Planner();

        Variable scale= new Variable("scale", 10);
        Variable offset= new Variable("offset", 1000);
        Variable src= null, dst= null;

        ArrayList<Variable> dests= new ArrayList<Variable>();

        for (int i= 0; i < n; ++i) {
            src= new Variable("src" + Integer.toString(i), i);
            dst= new Variable("dst" + Integer.toString(i), i);
            dests.add(dst);
            new StayConstraint(src, Strength.normal);
            new ScaleConstraint(src, scale, offset, dst, Strength.required);
        }

        change(src, 17);
        if (dst.value != 1170) error("Projection test 1 failed!");

        change(dst, 1050);
        if (src.value != 5) error("Projection test 2 failed!");

        change(scale, 5);
        for (int i= 0; i < n - 1; ++i) {
            if ((dests.get(i)).value != i * 5 + 1000)
                error("Projection test 3 failed!");
        }

        change(offset, 2000);
        for (int i= 0; i < n - 1; ++i) {
            if ((dests.get(i)).value != i * 5 + 2000)
                error("Projection test 4 failed!");
        }
    }

    private void change(Variable var, int newValue)
    {
        EditConstraint editC= new EditConstraint(var, Strength.preferred);
        ArrayList<Constraint> editV= new ArrayList<Constraint>();
        editV.add(editC);
        Plan plan= planner.extractPlanFromConstraints(editV);
        for (int i= 0; i < 10; i++) {
            var.value= newValue;
            plan.execute();
        }
        editC.destroyConstraint();
    }


    public static void error(String s)
    {
        System.err.println(s);
        System.exit(1);
    }

    public void inst_main(String args[])
    {
        int iterations= 100000;
        String options = "";

        if (args != null && args.length > 0)
            iterations = Integer.parseInt(args[0]);

        if (args != null && args.length > 1)
            options = args[1];

        long startTime= System.currentTimeMillis();
        for (int j= 0; j < iterations; ++j) {
            chainTest(100);
            projectionTest(100);
        }
        long endTime= System.currentTimeMillis();
        total_ms= endTime - startTime;
        System.out.println("DeltaBlue\tJava\t" + options + "\t" + iterations + "x\t" + ((double)total_ms / iterations) + " ms");
        System.out.println("DeltaBlue\tJava\t" + options + "\t" + iterations + "x\t" + ((double)iterations / total_ms) + " run/ms");
    }

    public static void main(String[] args) {
        (new DeltaBlue()).inst_main(args);
    }
}

