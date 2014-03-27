package ru.benchmark.deltaBlue.code;

/**
 * Created by evans on 19.03.14.
 */
// I am an abstract class representing a system-maintainable
// relationship (or "constraint") between a set of variables. I supply
// a strength instance variable; concrete subclasses provide a means
// of storing the constrained variables and other information required
// to represent a constraint.
abstract class Constraint {

    public Strength strength; // the strength of this constraint

    protected Constraint() {} // this has to be here because of
    // Java's constructor idiocy.

    protected Constraint(Strength strength)
    {
        this.strength= strength;
    }

    // Answer true if this constraint is satisfied in the current solution.
    public abstract boolean isSatisfied();

    // Record the fact that I am unsatisfied.
    public abstract void markUnsatisfied();

    // Normal constraints are not input constraints. An input constraint
    // is one that depends on external state, such as the mouse, the
    // keyboard, a clock, or some arbitrary piece of imperative code.
    public boolean isInput() { return false; }

    // Activate this constraint and attempt to satisfy it.
    protected void addConstraint()
    {
        addToGraph();
        DeltaBlue.planner.incrementalAdd(this);
    }

    // Deactivate this constraint, remove it from the constraint graph,
    // possibly causing other constraints to be satisfied, and destroy
    // it.
    public void destroyConstraint()
    {
        if (isSatisfied()) DeltaBlue.planner.incrementalRemove(this);
        removeFromGraph();
    }

    // Add myself to the constraint graph.
    public abstract void addToGraph();

    // Remove myself from the constraint graph.
    public abstract void removeFromGraph();

    // Decide if I can be satisfied and record that decision. The output
    // of the choosen method must not have the given mark and must have
    // a walkabout strength less than that of this constraint.
    protected abstract void chooseMethod(int mark);

    // Set the mark of all input from the given mark.
    protected abstract void markInputs(int mark);

    // Assume that I am satisfied. Answer true if all my current inputs
    // are known. A variable is known if either a) it is 'stay' (i.e. it
    // is a constant at plan execution time), b) it has the given mark
    // (indicating that it has been computed by a constraint appearing
    // earlier in the plan), or c) it is not determined by any
    // constraint.
    public abstract boolean inputsKnown(int mark);

    // Answer my current output variable. Raise an error if I am not
    // currently satisfied.
    public abstract Variable output();

    // Attempt to find a way to enforce this constraint. If successful,
    // record the solution, perhaps modifying the current dataflow
    // graph. Answer the constraint that this constraint overrides, if
    // there is one, or nil, if there isn't.
    // Assume: I am not already satisfied.
    //
    public Constraint satisfy(int mark)
    {
        chooseMethod(mark);
        if (!isSatisfied()) {
            if (strength == Strength.required) {
                DeltaBlue.error("Could not satisfy a required constraint");
            }
            return null;
        }
        // constraint can be satisfied
        // mark inputs to allow cycle detection in addPropagate
        markInputs(mark);
        Variable out= output();
        Constraint overridden= out.determinedBy;
        if (overridden != null) overridden.markUnsatisfied();
        out.determinedBy= this;
        if (!DeltaBlue.planner.addPropagate(this, mark)) {
            System.out.println("Cycle encountered");
            return null;
        }
        out.mark= mark;
        return overridden;
    }

    // Enforce this constraint. Assume that it is satisfied.
    public abstract void execute();

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied.
    public abstract void recalculate();

    protected abstract void printInputs();

    protected void printOutput() { output().print(); }

    public void print()
    {
        int i, outIndex;

        if (!isSatisfied()) {
            System.out.print("Unsatisfied");
        } else {
            System.out.print("Satisfied(");
            printInputs();
            System.out.print(" -> ");
            printOutput();
            System.out.print(")");
        }
        System.out.print("\n");
    }

}
