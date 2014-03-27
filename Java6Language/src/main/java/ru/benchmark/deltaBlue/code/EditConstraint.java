package ru.benchmark.deltaBlue.code;

/**
 * Created by evans on 19.03.14.
 */ // I am a unary input constraint used to mark a variable that the
// client wishes to change.
//
class EditConstraint extends UnaryConstraint {

    public EditConstraint(Variable v, Strength str) { super(v, str); }

    // I indicate that a variable is to be changed by imperative code.
    public boolean isInput() { return true; }

    public void execute() {} // Edit constraints do nothing.

}
