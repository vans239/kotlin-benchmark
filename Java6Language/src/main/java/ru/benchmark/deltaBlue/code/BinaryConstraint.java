package ru.benchmark.deltaBlue.code;

/**
 * @author evans
 * 19.03.14.
 */

// I am an abstract superclass for constraints having two possible
// output variables.
//
abstract class BinaryConstraint extends Constraint {

    protected Variable v1, v2; // possible output variables
    protected byte direction; // one of the following...
    protected static byte backward= -1;    // v1 is output
    protected static byte nodirection= 0;  // not satisfied
    protected static byte forward= 1;      // v2 is output

    protected BinaryConstraint() {} // this has to be here because of
    // Java's constructor idiocy.

    protected BinaryConstraint(Variable var1, Variable var2, Strength strength) {
        super(strength);
        v1= var1;
        v2= var2;
        direction= nodirection;
        addConstraint();
    }

    // Answer true if this constraint is satisfied in the current solution.
    public boolean isSatisfied() { return direction != nodirection; }

    // Add myself to the constraint graph.
    public void addToGraph()
    {
        v1.addConstraint(this);
        v2.addConstraint(this);
        direction= nodirection;
    }

    // Remove myself from the constraint graph.
    public void removeFromGraph()
    {
        if (v1 != null) v1.removeConstraint(this);
        if (v2 != null) v2.removeConstraint(this);
        direction= nodirection;
    }

    // Decide if I can be satisfied and which way I should flow based on
    // the relative strength of the variables I relate, and record that
    // decision.
    //
    protected void chooseMethod(int mark)
    {
        if (v1.mark == mark)
            direction=
                    v2.mark != mark && Strength.stronger(strength, v2.walkStrength)
                            ? forward : nodirection;

        if (v2.mark == mark)
            direction=
                    v1.mark != mark && Strength.stronger(strength, v1.walkStrength)
                            ? backward : nodirection;

        // If we get here, neither variable is marked, so we have a choice.
        if (Strength.weaker(v1.walkStrength, v2.walkStrength))
            direction=
                    Strength.stronger(strength, v1.walkStrength) ? backward : nodirection;
        else
            direction=
                    Strength.stronger(strength, v2.walkStrength) ? forward : nodirection;
    }

    // Record the fact that I am unsatisfied.
    public void markUnsatisfied() { direction= nodirection; }

    // Mark the input variable with the given mark.
    protected void markInputs(int mark)
    {
        input().mark= mark;
    }

    public boolean inputsKnown(int mark)
    {
        Variable i= input();
        return i.mark == mark || i.stay || i.determinedBy == null;
    }

    // Answer my current output variable.
    public Variable output() { return direction==forward ? v2 : v1; }

    // Answer my current input variable
    public Variable input() { return direction==forward ? v1 : v2; }

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied.
    //
    public void recalculate()
    {
        Variable in= input(), out= output();
        out.walkStrength= Strength.weakestOf(strength, in.walkStrength);
        out.stay= in.stay;
        if (out.stay) execute();
    }

    protected void printInputs()
    {
        input().print();
    }

}
