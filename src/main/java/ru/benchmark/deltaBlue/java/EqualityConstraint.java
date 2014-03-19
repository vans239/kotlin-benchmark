package ru.benchmark.deltaBlue.java;

/**
 * Created by evans on 19.03.14.
 */ // I constrain two variables to have the same value: "v1 = v2".
//
class EqualityConstraint extends BinaryConstraint {

    // Install a constraint with the given strength equating the given variables.
    public EqualityConstraint(Variable var1, Variable var2, Strength strength)
    {
        super(var1, var2, strength);
    }

    // Enforce this constraint. Assume that it is satisfied.
    public void execute() {
        output().value= input().value;
    }

}
