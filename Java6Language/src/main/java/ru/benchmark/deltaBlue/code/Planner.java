package ru.benchmark.deltaBlue.code;

import java.util.ArrayList;

/**
 * Created by evans on 19.03.14.
 */
class Planner {

    int currentMark= 0;

    // Select a previously unused mark value.
    private int newMark() { return ++currentMark; }

    public Planner()
    {
        currentMark= 0;
    }

    // Attempt to satisfy the given constraint and, if successful,
    // incrementally update the dataflow graph.  Details: If satifying
    // the constraint is successful, it may override a weaker constraint
    // on its output. The algorithm attempts to resatisfy that
    // constraint using some other method. This process is repeated
    // until either a) it reaches a variable that was not previously
    // determined by any constraint or b) it reaches a constraint that
    // is too weak to be satisfied using any of its methods. The
    // variables of constraints that have been processed are marked with
    // a unique mark value so that we know where we've been. This allows
    // the algorithm to avoid getting into an infinite loop even if the
    // constraint graph has an inadvertent cycle.
    //
    public void incrementalAdd(Constraint c)
    {
        int mark= newMark();
        Constraint overridden= c.satisfy(mark);
        while (overridden != null) {
            overridden= overridden.satisfy(mark);
        }
    }


    // Entry point for retracting a constraint. Remove the given
    // constraint and incrementally update the dataflow graph.
    // Details: Retracting the given constraint may allow some currently
    // unsatisfiable downstream constraint to be satisfied. We therefore collect
    // a list of unsatisfied downstream constraints and attempt to
    // satisfy each one in turn. This list is traversed by constraint
    // strength, strongest first, as a heuristic for avoiding
    // unnecessarily adding and then overriding weak constraints.
    // Assume: c is satisfied.
    //
    public void incrementalRemove(Constraint c)
    {
        Variable out= c.output();
        c.markUnsatisfied();
        c.removeFromGraph();
        ArrayList<Constraint> unsatisfied= removePropagateFrom(out);
        Strength strength= Strength.required;
        do {
            for (int i= 0; i < unsatisfied.size(); ++i) {
                Constraint u= unsatisfied.get(i);
                if (u.strength == strength)
                    incrementalAdd(u);
            }
            strength= strength.nextWeaker();
        } while (strength != Strength.weakest);
    }

    // Recompute the walkabout strengths and stay flags of all variables
    // downstream of the given constraint and recompute the actual
    // values of all variables whose stay flag is true. If a cycle is
    // detected, remove the given constraint and answer
    // false. Otherwise, answer true.
    // Details: Cycles are detected when a marked variable is
    // encountered downstream of the given constraint. The sender is
    // assumed to have marked the inputs of the given constraint with
    // the given mark. Thus, encountering a marked node downstream of
    // the output constraint means that there is a path from the
    // constraint's output to one of its inputs.
    //
    public boolean addPropagate(Constraint c, int mark)
    {
        ArrayList<Constraint> todo= new ArrayList<Constraint>();
        todo.add(c);
        while (!todo.isEmpty()) {
            Constraint d= todo.get(0);
            todo.remove(0);
            if (d.output().mark == mark) {
                incrementalRemove(c);
                return false;
            }
            d.recalculate();
            addConstraintsConsumingTo(d.output(), todo);
        }
        return true;
    }


    // The given variable has changed. Propagate new values downstream.
    public void propagateFrom(Variable v)
    {
        ArrayList<Constraint> todo= new ArrayList<Constraint>();
        addConstraintsConsumingTo(v, todo);
        while (!todo.isEmpty()) {
            Constraint c= todo.get(0);
            todo.remove(0);
            c.execute();
            addConstraintsConsumingTo(c.output(), todo);
        }
    }

    // Update the walkabout strengths and stay flags of all variables
    // downstream of the given constraint. Answer a collection of
    // unsatisfied constraints sorted in order of decreasing strength.
    //
    protected ArrayList<Constraint> removePropagateFrom(Variable out)
    {
        out.determinedBy= null;
        out.walkStrength= Strength.weakest;
        out.stay= true;
        ArrayList<Constraint> unsatisfied= new ArrayList<Constraint>();
        ArrayList<Variable> todo= new ArrayList<Variable>();
        todo.add(out);
        while (!todo.isEmpty()) {
            Variable v= todo.get(0);
            todo.remove(0);
            for (int i= 0; i < v.constraints.size(); ++i) {
                Constraint c= v.constraints.get(i);
                if (!c.isSatisfied())
                    unsatisfied.add(c);
            }
            Constraint determiningC= v.determinedBy;
            for (int i= 0; i < v.constraints.size(); ++i) {
                Constraint nextC= v.constraints.get(i);
                if (nextC != determiningC && nextC.isSatisfied()) {
                    nextC.recalculate();
                    todo.add(nextC.output());
                }
            }
        }
        return unsatisfied;
    }

    // Extract a plan for resatisfaction starting from the outputs of
    // the given constraints, usually a set of input constraints.
    //
    protected Plan extractPlanFromConstraints(ArrayList<Constraint> constraints)
    {
        ArrayList<Constraint> sources= new ArrayList<Constraint>();
        for (int i= 0; i < constraints.size(); ++i) {
            Constraint c= constraints.get(i);
            if (c.isInput() && c.isSatisfied())
                sources.add(c);
        }
        return makePlan(sources);
    }

    // Extract a plan for resatisfaction starting from the given source
    // constraints, usually a set of input constraints. This method
    // assumes that stay optimization is desired; the plan will contain
    // only constraints whose output variables are not stay. Constraints
    // that do no computation, such as stay and edit constraints, are
    // not included in the plan.
    // Details: The outputs of a constraint are marked when it is added
    // to the plan under construction. A constraint may be appended to
    // the plan when all its input variables are known. A variable is
    // known if either a) the variable is marked (indicating that has
    // been computed by a constraint appearing earlier in the plan), b)
    // the variable is 'stay' (i.e. it is a constant at plan execution
    // time), or c) the variable is not determined by any
    // constraint. The last provision is for past states of history
    // variables, which are not stay but which are also not computed by
    // any constraint.
    // Assume: sources are all satisfied.
    //
    protected Plan makePlan(ArrayList<Constraint> sources)
    {
        int mark= newMark();
        Plan plan= new Plan();
        ArrayList<Constraint> todo= sources;
        while (!todo.isEmpty()) {
            Constraint c= todo.get(0);
            todo.remove(0);
            if (c.output().mark != mark && c.inputsKnown(mark)) {
                // not in plan already and eligible for inclusion
                plan.addConstraint(c);
                c.output().mark= mark;
                addConstraintsConsumingTo(c.output(), todo);
            }
        }
        return plan;
    }

    protected void addConstraintsConsumingTo(Variable v, ArrayList<Constraint> coll)
    {
        Constraint determiningC= v.determinedBy;
        ArrayList<Constraint> cc= v.constraints;
        for (int i= 0; i < cc.size(); ++i) {
            Constraint c= cc.get(i);
            if (c != determiningC && c.isSatisfied())
                coll.add(c);
        }
    }

}
