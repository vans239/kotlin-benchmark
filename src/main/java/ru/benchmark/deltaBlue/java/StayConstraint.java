package ru.benchmark.deltaBlue.java;

/**
 * Created by evans on 19.03.14.
 */ // I mark variables that should, with some level of preference, stay
// the same. I have one method with zero inputs and one output, which
// does nothing. Planners may exploit the fact that, if I am
// satisfied, my output will not change during plan execution. This is
// called "stay optimization".
//
class StayConstraint extends UnaryConstraint {

    // Install a stay constraint with the given strength on the given variable.
    public StayConstraint(Variable v, Strength str) { super(v, str); }

    public void execute() {} // Stay constraints do nothing.

}
