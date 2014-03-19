package ru.benchmark.deltaBlue.java;

import java.util.ArrayList;

/**
 * Created by evans on 19.03.14.
 */

// A Plan is an ordered list of constraints to be executed in sequence
// to resatisfy all currently satisfiable constraints in the face of
// one or more changing inputs.
class Plan {

    private ArrayList<Constraint> v;

    public Plan() { v= new ArrayList<Constraint>(); }

    public void addConstraint(Constraint c) { v.add(c); }

    public int size() { return v.size(); }

    public Constraint constraintAt(int index) {
        return v.get(index); }

    public void execute()
    {
        for (int i= 0; i < size(); ++i) {
            Constraint c= constraintAt(i);
            c.execute();
        }
    }

}
