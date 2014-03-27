package ru.benchmark.deltaBlue.code;

/**
 * Created by evans on 19.03.14.
 */ // I am an abstract superclass for constraints having a single
// possible output variable.
//
abstract class UnaryConstraint extends Constraint {

    protected Variable myOutput; // possible output variable
    protected boolean satisfied; // true if I am currently satisfied

    protected UnaryConstraint(Variable v, Strength strength)
    {
        super(strength);
        myOutput= v;
        satisfied= false;
        addConstraint();
    }

    // Answer true if this constraint is satisfied in the current solution.
    public boolean isSatisfied() { return satisfied; }

    // Record the fact that I am unsatisfied.
    public void markUnsatisfied() { satisfied= false; }

    // Answer my current output variable.
    public Variable output() { return myOutput; }

    // Add myself to the constraint graph.
    public void addToGraph()
    {
        myOutput.addConstraint(this);
        satisfied= false;
    }

    // Remove myself from the constraint graph.
    public void removeFromGraph()
    {
        if (myOutput != null) myOutput.removeConstraint(this);
        satisfied= false;
    }

    // Decide if I can be satisfied and record that decision.
    protected void chooseMethod(int mark)
    {
        satisfied=    myOutput.mark != mark
                && Strength.stronger(strength, myOutput.walkStrength);
    }

    protected void markInputs(int mark) {}   // I have no inputs

    public boolean inputsKnown(int mark) { return true; }

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied."
    public void recalculate()
    {
        myOutput.walkStrength= strength;
        myOutput.stay= !isInput();
        if (myOutput.stay) execute(); // stay optimization
    }

    protected void printInputs() {} // I have no inputs

}
