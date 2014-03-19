package ru.benchmark.deltaBlue.java;

import java.util.ArrayList;

/**
 * Created by evans on 19.03.14.
 */
// I represent a constrained variable. In addition to my value, I
// maintain the structure of the constraint graph, the current
// dataflow graph, and various parameters of interest to the DeltaBlue
// incremental constraint solver.

class Variable {

    public int value;               // my value; changed by constraints
    public ArrayList<Constraint> constraints;      // normal constraints that reference me
    public Constraint determinedBy; // the constraint that currently determines
    // my value (or null if there isn't one)
    public int mark;                // used by the planner to mark constraints
    public Strength walkStrength;   // my walkabout strength
    public boolean stay;            // true if I am a planning-time constant
    public String	name;             // a symbolic name for reporting purposes


    private Variable(String name, int initialValue, Strength walkStrength,
                     int nconstraints)
    {
        value= initialValue;
        constraints= new ArrayList<Constraint>(nconstraints);
        determinedBy= null;
        mark= 0;
        this.walkStrength= walkStrength;
        stay= true;
        this.name= name;
    }

    public Variable(String name, int value)
    {
        this(name, value, Strength.weakest, 2);
    }

    public Variable(String name)
    {
        this(name, 0, Strength.weakest, 2);
    }

    public void print()
    {
        System.out.print(name + "(");
        walkStrength.print();
        System.out.print("," + value + ")");
    }

    // Add the given constraint to the set of all constraints that refer to me.
    public void addConstraint(Constraint c)
    {
        constraints.add(c);
    }

    // Remove all traces of c from this variable.
    public void removeConstraint(Constraint c)
    {
        constraints.remove(c);
        if (determinedBy == c) determinedBy= null;
    }

    // Attempt to assign the given value to me using the given strength.
    public void setValue(int value, Strength strength)
    {
        EditConstraint e= new EditConstraint(this, strength);
        if (e.isSatisfied()) {
            this.value= value;
            DeltaBlue.planner.propagateFrom(this);
        }
        e.destroyConstraint();
    }

}
