package ru.benchmark.deltaBlue.java;

/**
 * Created by evans on 19.03.14.
 */ // I relate two variables by the linear scaling relationship: "v2 =
// (v1 * scale) + offset". Either v1 or v2 may be changed to maintain
// this relationship but the scale factor and offset are considered
// read-only.
//
class ScaleConstraint extends BinaryConstraint {

    protected Variable scale; // scale factor input variable
    protected Variable offset; // offset input variable

    // Install a scale constraint with the given strength on the given variables.
    public ScaleConstraint(Variable src, Variable scale, Variable offset,
                           Variable dest, Strength strength)
    {
        // Curse this wretched language for insisting that constructor invocation
        // must be the first thing in a method...
        // ..because of that, we must copy the code from the inherited
        // constructors.
        this.strength= strength;
        v1= src;
        v2= dest;
        direction= nodirection;
        this.scale= scale;
        this.offset= offset;
        addConstraint();
    }

    // Add myself to the constraint graph.
    public void addToGraph()
    {
        super.addToGraph();
        scale.addConstraint(this);
        offset.addConstraint(this);
    }

    // Remove myself from the constraint graph.
    public void removeFromGraph()
    {
        super.removeFromGraph();
        if (scale != null) scale.removeConstraint(this);
        if (offset != null) offset.removeConstraint(this);
    }

    // Mark the inputs from the given mark.
    protected void markInputs(int mark)
    {
        super.markInputs(mark);
        scale.mark= offset.mark= mark;
    }

    // Enforce this constraint. Assume that it is satisfied.
    public void execute()
    {
        if (direction == forward)
            v2.value= v1.value * scale.value + offset.value;
        else
            v1.value= (v2.value - offset.value) / scale.value;
    }

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied.
    public void recalculate()
    {
        Variable in= input(), out= output();
        out.walkStrength= Strength.weakestOf(strength, in.walkStrength);
        out.stay= in.stay && scale.stay && offset.stay;
        if (out.stay) execute(); // stay optimization
    }
}
