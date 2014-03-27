/*
 * CL_SUN_COPYRIGHT_JVM_BEGIN
 *   If you or the company you represent has a separate agreement with both
 *   CableLabs and Sun concerning the use of this code, your rights and
 *   obligations with respect to this code shall be as set forth therein. No
 *   license is granted hereunder for any other purpose.
 * CL_SUN_COPYRIGHT_JVM_END
*/
package ru.benchmark.deltaBlue.kotlin

/*
 * @(#)DeltaBlue.java	1.6 06/10/10
 *
 * Copyright  1990-2006 Sun Microsystems, Inc. All Rights Reserved.  
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER  
 *   
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License version  
 * 2 only, as published by the Free Software Foundation.   
 *   
 * This program is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * General Public License version 2 for more details (a copy is  
 * included at /legal/license.txt).   
 *   
 * You should have received a copy of the GNU General Public License  
 * version 2 along with this work; if not, write to the Free Software  
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA   
 *   
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa  
 * Clara, CA 95054 or visit www.sun.com if you need additional  
 * information or have any questions. 
 *
 */
/*

  This is a Java implemention of the DeltaBlue algorithm described in:
    "The DeltaBlue Algorithm: An Incremental Constraint Hierarchy Solver"
    by Bjorn N. Freeman-Benson and John Maloney
    January 1990 Communications of the ACM,
    also available as University of Washington TR 89-08-06.

  This implementation by Mario Wolczko, Sun Microsystems, Sep 1996,
  based on the Smalltalk implementation by John Maloney.

*/

//package COM.sun.labs.kanban.DeltaBlue;

import java.util.ArrayList

//import Benchmark;



/* 
Strengths are used to measure the relative importance of constraints.
New strengths may be inserted in the strength hierarchy without
disrupting current constraints.  Strengths cannot be created outside
this class, so pointer comparison can be used for value comparison.
*/

class Strength(val strengthValue: Int, val name: String) {
    // for iteration
    public fun nextWeaker(): Strength {
        when (strengthValue) {
            0 -> {
                return weakest
            }
            1 -> {
                return weakDefault
            }
            2 -> {
                return normal
            }
            3 -> {
                return strongDefault
            }
            4 -> {
                return preferred
            }
            5 -> {
                return strongPreferred
            }
            else -> {
                System.err.println("Invalid call to nextStrength()!")
                System.exit(1)
                throw RuntimeException() //instead of return
            }
        }
    }

    public fun print() {
        System.out.print("strength[" + Integer.toString(strengthValue) + "]")
    }

    class object {

        public fun stronger(s1: Strength, s2: Strength): Boolean {
            return s1.strengthValue < s2.strengthValue
        }

        public fun weaker(s1: Strength, s2: Strength): Boolean {
            return s1.strengthValue > s2.strengthValue
        }

        public fun weakestOf(s1: Strength, s2: Strength): Strength {
            return (if (weaker(s1, s2))
                s1
            else
                s2)
        }

        public fun strongest(s1: Strength, s2: Strength): Strength {
            return (if (stronger(s1, s2))
                s1
            else
                s2)
        }

        // Strength constants
        public val required: Strength = Strength(0, "required")
        public val strongPreferred: Strength = Strength(1, "strongPreferred")
        public val preferred: Strength = Strength(2, "preferred")
        public val strongDefault: Strength = Strength(3, "strongDefault")
        public val normal: Strength = Strength(4, "normal")
        public val weakDefault: Strength = Strength(5, "weakDefault")
        public val weakest: Strength = Strength(6, "weakest")
    }
}




//------------------------------ variables ------------------------------

// I represent a constrained variable. In addition to my value, I
// maintain the structure of the constraint graph, the current
// dataflow graph, and various parameters of interest to the DeltaBlue
// incremental constraint solver.

class Variable(val name: String, var value: Int, var walkStrength: Strength, nconstraints: Int) {
    val constraints = ArrayList<Constraint>(nconstraints)
    var determinedBy: Constraint? = null
    var stay = true
    var mark = 0

    public fun print() {
        System.out.print(name + "(")
        walkStrength.print()
        System.out.print("," + value + ")")
    }

    // Add the given constraint to the set of all constraints that refer to me.
    public fun addConstraint(c: Constraint) {
        constraints.add(c)
    }

    // Remove all traces of c from this variable.
    public fun removeConstraint(c: Constraint) {
        constraints.remove(c)
        if (determinedBy == c)
            determinedBy = null
    }

    // Attempt to assign the given value to me using the given strength.
    public fun setValue(value: Int, strength: Strength) {
        val e = EditConstraint(this, strength)
        if (e.isSatisfied()) {
            this.value = value
            DeltaBlue.planner!!.propagateFrom(this)
        }
        e.destroyConstraint()
    }

    class object {

        public fun init(name: String, value: Int): Variable {
            val __ = Variable(name, value, Strength.weakest, 2)
            return __
        }

        public fun init(name: String): Variable {
            val __ = Variable(name, 0, Strength.weakest, 2)
            return __
        }
    }
}




//------------------------ constraints ------------------------------------

// I am an abstract class representing a system-maintainable
// relationship (or "constraint") between a set of variables. I supply
// a strength instance variable; concrete subclasses provide a means
// of storing the constrained variables and other information required
// to represent a constraint.

abstract class Constraint(val strength: Strength) {

    // the strength of this constraintpublic var strength : Strength = 0

    // Answer true if this constraint is satisfied in the current solution.
    public abstract fun isSatisfied(): Boolean

    // Record the fact that I am unsatisfied.
    public abstract fun markUnsatisfied()

    // Normal constraints are not input constraints. An input constraint
    // is one that depends on external state, such as the mouse, the
    // keyboard, a clock, or some arbitrary piece of imperative code.
    public open fun isInput(): Boolean {
        return false
    }

    // Activate this constraint and attempt to satisfy it.
    protected fun addConstraint() {
        addToGraph()
        DeltaBlue.planner!!.incrementalAdd(this)
    }

    // Deactivate this constraint, remove it from the constraint graph,
    // possibly causing other constraints to be satisfied, and destroy
    // it.
    public fun destroyConstraint() {
        if (isSatisfied())
            DeltaBlue.planner!!.incrementalRemove(this)
        removeFromGraph()
    }

    // Add myself to the constraint graph.
    public abstract fun addToGraph()

    // Remove myself from the constraint graph.
    public abstract fun removeFromGraph()

    // Decide if I can be satisfied and record that decision. The output
    // of the choosen method must not have the given mark and must have
    // a walkabout strength less than that of this constraint.
    protected abstract fun chooseMethod(mark: Int)

    // Set the mark of all input from the given mark.
    protected abstract fun markInputs(mark: Int)

    // Assume that I am satisfied. Answer true if all my current inputs
    // are known. A variable is known if either a) it is 'stay' (i.e. it
    // is a constant at plan execution time), b) it has the given mark
    // (indicating that it has been computed by a constraint appearing
    // earlier in the plan), or c) it is not determined by any
    // constraint.
    public abstract fun inputsKnown(mark: Int): Boolean

    // Answer my current output variable. Raise an error if I am not
    // currently satisfied.
    public abstract fun output(): Variable

    // Attempt to find a way to enforce this constraint. If successful,
    // record the solution, perhaps modifying the current dataflow
    // graph. Answer the constraint that this constraint overrides, if
    // there is one, or nil, if there isn't.
    // Assume: I am not already satisfied.
    //
    public fun satisfy(mark: Int): Constraint? {
        chooseMethod(mark)
        if (!isSatisfied()) {
            if (strength == Strength.required) {
                DeltaBlue.error("Could not satisfy a required constraint")
            }
            return null
        }
        // constraint can be satisfied
        // mark inputs to allow cycle detection in addPropagate
        markInputs(mark)
        val out = output()
        val overridden = out.determinedBy
        if (overridden != null)
            overridden.markUnsatisfied()
        out.determinedBy = this
        if (!DeltaBlue.planner!!.addPropagate(this, mark)) {
            System.out.println("Cycle encountered")
            return null
        }
        out.mark = mark
        return overridden
    }

    // Enforce this constraint. Assume that it is satisfied.
    public abstract fun execute()

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied.
    public abstract fun recalculate()

    protected abstract fun printInputs()

    protected fun printOutput() {
        output().print()
    }

    public fun print() {
//        val i: Int
//        val outIndex: Int

        if (!isSatisfied()) {
            System.out.print("Unsatisfied")
        } else {
            System.out.print("Satisfied(")
            printInputs()
            System.out.print(" -> ")
            printOutput()
            System.out.print(")")
        }
        System.out.print("\n")
    }
}



//-------------unary constraints-------------------------------------------

// I am an abstract superclass for constraints having a single
// possible output variable.
//
abstract class UnaryConstraint(val myOutput: Variable, strength: Strength) : Constraint(strength) {
    var satisfied = false

    // possible output variableprotected var myOutput : Variable = 0
    // true if I am currently satisfiedprotected var satisfied : Boolean = false

    // Answer true if this constraint is satisfied in the current solution.
    override fun isSatisfied(): Boolean {
        return satisfied
    }

    // Record the fact that I am unsatisfied.
    override fun markUnsatisfied() {
        satisfied = false
    }

    // Answer my current output variable.
    override fun output(): Variable {
        return myOutput
    }

    // Add myself to the constraint graph.
    override fun addToGraph() {
        myOutput.addConstraint(this)
        satisfied = false
    }

    // Remove myself from the constraint graph.
    override fun removeFromGraph() {
        myOutput.removeConstraint(this)
        satisfied = false
    }

    // Decide if I can be satisfied and record that decision.
    override fun chooseMethod(mark: Int) {
        satisfied = myOutput.mark != mark && Strength.stronger(strength, myOutput.walkStrength)
    }

    override fun inputsKnown(mark: Int): Boolean {
        return true
    }

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied."
    override fun recalculate() {
        myOutput.walkStrength = strength
        myOutput.stay = !isInput()
        if (myOutput.stay)
            execute()
    }
    {
        addConstraint()
    }
}


// I am a unary input constraint used to mark a variable that the
// client wishes to change.
//
class EditConstraint(v: Variable, str: Strength) : UnaryConstraint(v, str) {
    override fun markInputs(mark: Int) {
    }
    override fun execute() {
    }
    override fun printInputs() {
    }

    // I indicate that a variable is to be changed by imperative code.
    override fun isInput(): Boolean {
        return true
    }
}

// I mark variables that should, with some level of preference, stay
// the same. I have one method with zero inputs and one output, which
// does nothing. Planners may exploit the fact that, if I am
// satisfied, my output will not change during plan execution. This is
// called "stay optimization".
//
class StayConstraint(v: Variable, str: Strength) : UnaryConstraint(v, str) {
    override fun execute() {
    }
    override fun printInputs() {
    }
    override fun markInputs(mark: Int) {
    }
}




//-------------binary constraints-------------------------------------------


// I am an abstract superclass for constraints having two possible
// output variables.
//
abstract class BinaryConstraint(val v1: Variable, val v2: Variable, strength: Strength) : Constraint(strength) {
    var direction = nodirection

    // Answer true if this constraint is satisfied in the current solution.
    override fun isSatisfied(): Boolean {
        return direction != nodirection
    }

    // Add myself to the constraint graph.
    override fun addToGraph() {
        v1.addConstraint(this)
        v2.addConstraint(this)
        direction = nodirection
    }

    // Remove myself from the constraint graph.
    override fun removeFromGraph() {
        v1.removeConstraint(this)
        v2.removeConstraint(this)
        direction = nodirection
    }

    // Decide if I can be satisfied and which way I should flow based on
    // the relative strength of the variables I relate, and record that
    // decision.
    //
    override fun chooseMethod(mark: Int) {
        if (v1.mark == mark)
            direction = (if (v2.mark != mark && Strength.stronger(strength, v2.walkStrength))
                forward
            else
                nodirection)

        if (v2.mark == mark)
            direction = (if (v1.mark != mark && Strength.stronger(strength, v1.walkStrength))
                backward
            else
                nodirection)

        // If we get here, neither variable is marked, so we have a choice.
        if (Strength.weaker(v1.walkStrength, v2.walkStrength))
            direction = (if (Strength.stronger(strength, v1.walkStrength))
                backward
            else
                nodirection)
        else
            direction = (if (Strength.stronger(strength, v2.walkStrength))
                forward
            else
                nodirection)
    }

    // Record the fact that I am unsatisfied.
    override fun markUnsatisfied() {
        direction = nodirection
    }

    // Mark the input variable with the given mark.
    override fun markInputs(mark: Int) {
        input().mark = mark
    }

    override fun inputsKnown(mark: Int): Boolean {
        val i = input()
        return i.mark == mark || i.stay || i.determinedBy == null
    }

    // Answer my current output variable.
    override fun output(): Variable {
        return (if (direction == forward)
            v2
        else
            v1)
    }

    // Answer my current input variable
    public fun input(): Variable {
        return (if (direction == forward)
            v1
        else
            v2)
    }

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied.
    //
    override fun recalculate() {
        val `in` = input()
        val out = output()
        out.walkStrength = Strength.weakestOf(strength, `in`.walkStrength)
        out.stay = `in`.stay
        if (out.stay)
            execute()
    }

    override fun printInputs() {
        input().print()
    }

    class object {
        var backward: Byte = -1
        val nodirection: Byte = 0
        var forward: Byte = 1
    }
}


// I constrain two variables to have the same value: "v1 = v2".
//
class EqualityConstraint(var1: Variable, var2: Variable, strength: Strength) : BinaryConstraint(var1, var2, strength) {

    {
        addConstraint()
    }
    // Enforce this constraint. Assume that it is satisfied.
    override fun execute() {
        output().value = input().value
    }

}


// I relate two variables by the linear scaling relationship: "v2 =
// (v1 * scale) + offset". Either v1 or v2 may be changed to maintain
// this relationship but the scale factor and offset are considered
// read-only.
//
class ScaleConstraint(src: Variable, val scale: Variable, val offset: Variable, dest: Variable, strength: Strength)
: BinaryConstraint(src, dest, strength) {
    {
        addConstraint()
    }

    // scale factor input variableprotected var scale : Variable = 0
    // offset input variableprotected var offset : Variable = 0

    // Add myself to the constraint graph.
    override fun addToGraph() {
        super.addToGraph()
        scale.addConstraint(this)
        offset.addConstraint(this)
    }

    // Remove myself from the constraint graph.
    override fun removeFromGraph() {
        super.removeFromGraph()
        scale.removeConstraint(this)
        offset.removeConstraint(this)
    }

    // Mark the inputs from the given mark.
    override fun markInputs(mark: Int) {
        super.markInputs(mark)
        offset.mark = mark
        scale.mark = mark
    }

    // Enforce this constraint. Assume that it is satisfied.
    override fun execute() {
        if (direction == BinaryConstraint.forward)
            v2.value = v1.value * scale.value + offset.value
        else
            v1.value = (v2.value - offset.value) / scale.value
    }

    // Calculate the walkabout strength, the stay flag, and, if it is
    // 'stay', the value for the current output of this
    // constraint. Assume this constraint is satisfied.
    override fun recalculate() {
        val `in` = input()
        val out = output()
        out.walkStrength = Strength.weakestOf(strength, `in`.walkStrength)
        out.stay = `in`.stay && scale.stay && offset.stay
        if (out.stay)
            execute()
    }
}


// ------------------------------------------------------------


// A Plan is an ordered list of constraints to be executed in sequence
// to resatisfy all currently satisfiable constraints in the face of
// one or more changing inputs.

class Plan() {

    private var v = ArrayList<Constraint>()

    public fun addConstraint(c: Constraint) {
        v.add(c)
    }

    public fun size(): Int {
        return v.size()
    }

    public fun constraintAt(index: Int): Constraint {
        return v.get(index)
    }

    public fun execute() {
        for (i in 0..size() - 1) {
            val c = constraintAt(i)
            c.execute()
        }
    }
}


// ------------------------------------------------------------

// The DeltaBlue planner

class Planner() {

    var currentMark: Int = 0

    // Select a previously unused mark value.
    private fun newMark(): Int {
        return ++currentMark
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
    public fun incrementalAdd(c: Constraint) {
        val mark = newMark()
        var overridden = c.satisfy(mark)
        while (overridden != null) {
            overridden = overridden?.satisfy(mark)
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
    public fun incrementalRemove(c: Constraint) {
        val out = c.output()
        c.markUnsatisfied()
        c.removeFromGraph()
        val unsatisfied = removePropagateFrom(out)
        var strength = Strength.required
        do {
            for (i in 0..unsatisfied.size() - 1) {
                val u = unsatisfied.get(i)
                if (u.strength == strength)
                    incrementalAdd(u)
            }
            strength = strength.nextWeaker()
        } while (strength != Strength.weakest)
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
    public fun addPropagate(c: Constraint, mark: Int): Boolean {
        val todo = ArrayList<Constraint>()
        todo.add(c)
        while (!todo.isEmpty()) {
            val d = todo.get(0)
            todo.remove(0)
            if (d.output().mark == mark) {
                incrementalRemove(c)
                return false
            }
            d.recalculate()
            addConstraintsConsumingTo(d.output(), todo)
        }
        return true
    }


    // The given variable has changed. Propagate new values downstream.
    public fun propagateFrom(v: Variable) {
        val todo = ArrayList<Constraint>()
        addConstraintsConsumingTo(v, todo)
        while (!todo.isEmpty()) {
            val c = todo.get(0)
            todo.remove(0)
            c.execute()
            addConstraintsConsumingTo(c.output(), todo)
        }
    }

    // Update the walkabout strengths and stay flags of all variables
    // downstream of the given constraint. Answer a collection of
    // unsatisfied constraints sorted in order of decreasing strength.
    //
    protected fun removePropagateFrom(out: Variable): ArrayList<Constraint> {
        out.determinedBy = null
        out.walkStrength = Strength.weakest
        out.stay = true
        val unsatisfied = ArrayList<Constraint>()
        val todo = ArrayList<Variable>()
        todo.add(out)
        while (!todo.isEmpty()) {
            val v = todo.get(0)
            todo.remove(0)
            for (i in 0..v.constraints.size() - 1) {
                val c = v.constraints.get(i)
                if (!c.isSatisfied())
                    unsatisfied.add(c)
            }
            val determiningC = v.determinedBy
            for (i in 0..v.constraints.size() - 1) {
                val nextC = v.constraints.get(i)
                if (nextC != determiningC && nextC.isSatisfied()) {
                    nextC.recalculate()
                    todo.add(nextC.output())
                }
            }
        }
        return unsatisfied
    }

    // Extract a plan for resatisfaction starting from the outputs of
    // the given constraints, usually a set of input constraints.
    //
    fun extractPlanFromConstraints(constraints: ArrayList<Constraint>): Plan {
        val sources = ArrayList<Constraint>()
        for (i in 0..constraints.size() - 1) {
            val c = constraints.get(i)
            if (c.isInput() && c.isSatisfied())
                sources.add(c)
        }
        return makePlan(sources)
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
    protected fun makePlan(sources: ArrayList<Constraint>): Plan {
        val mark = newMark()
        val plan = Plan()
        val todo = sources
        while (!todo.isEmpty()) {
            val c = todo.get(0)
            todo.remove(0)
            if (c.output().mark != mark && c.inputsKnown(mark)) {
                // not in plan already and eligible for inclusion
                plan.addConstraint(c)
                c.output().mark = mark
                addConstraintsConsumingTo(c.output(), todo)
            }
        }
        return plan
    }

    protected fun addConstraintsConsumingTo(v: Variable, coll: ArrayList<Constraint>) {
        val determiningC = v.determinedBy
        val cc = v.constraints
        for (i in 0..cc.size() - 1) {
            val c = cc.get(i)
            if (c != determiningC && c.isSatisfied())
                coll.add(c)
        }
    }


    {
        currentMark = 0
    }
}

//------------------------------------------------------------

public class DeltaBlue() {
    private var total_ms: Long = 0
    public fun getRunTime(): Long {
        return total_ms
    }

    public fun inst_main(args: Array<String>) {
        var iterations = 100000
        var options = ""

        if (args.size > 0)
            iterations = Integer.parseInt(args[0])

        if (args.size > 1)
            options = args[1]

        val startTime = System.currentTimeMillis()
        for (j in 0..iterations - 1) {
            chainTest(100)
            //projectionTest(100)
        }
        val endTime = System.currentTimeMillis()
        total_ms = endTime - startTime
        System.out.println("DeltaBlue\tJava\t" + options + "\t" + iterations + "x\t" + (total_ms.toDouble() / iterations.toDouble()) + " ms")
        System.out.println("DeltaBlue\tJava\t" + options + "\t" + iterations + "x\t" + (iterations.toDouble() / total_ms.toDouble()) + " ms")
    }

    //  This is the standard DeltaBlue ru.benchmark. A long chain of
    //  equality constraints is constructed with a stay constraint on
    //  one end. An edit constraint is then added to the opposite end
    //  and the time is measured for adding and removing this
    //  constraint, and extracting and executing a constraint
    //  satisfaction plan. There are two cases. In case 1, the added
    //  constraint is stronger than the stay constraint and values must
    //  propagate down the entire length of the chain. In case 2, the
    //  added constraint is weaker than the stay constraint so it cannot
    //  be accomodated. The cost in this case is, of course, very
    //  low. Typical situations lie somewhere between these two
    //  extremes.
    //
    public fun chainTest(n: Int) {
        planner = Planner()

        var prev: Variable? = null
        var first: Variable? = null
        var last: Variable? = null

        // Build chain of n equality constraints
        for (i in 0..n) {
            val name = "v" + Integer.toString(i)
            val v = Variable.init(name)
            if (prev != null)
                EqualityConstraint(prev!!, v, Strength.required)
            if (i == 0)
                first = v
            if (i == n)
                last = v
            prev = v
        }

        StayConstraint(last!!, Strength.strongDefault)
        val editC = EditConstraint(first!!, Strength.preferred)
        val editV = ArrayList<Constraint>()
        editV.add(editC)
        val plan = planner!!.extractPlanFromConstraints(editV)
        for (i in 0..100 - 1) {
            first!!.value = i
            plan.execute()
            if (last!!.value != i)
                error("Chain test failed!")
        }
        editC.destroyConstraint()
    }


    // This test constructs a two sets of variables related to each
    // other by a simple linear transformation (scale and offset). The
    // time is measured to change a variable on either side of the
    // mapping and to change the scale and offset factors.
    //
    public fun projectionTest(n: Int) {
        planner = Planner()

        val scale = Variable.init("scale", 10)
        val offset = Variable.init("offset", 1000)
        var src: Variable? = null
        var dst: Variable? = null

        val dests = ArrayList<Variable>()

        for (i in 0..n - 1) {
            src = Variable.init("src" + Integer.toString(i), i)
            dst = Variable.init("dst" + Integer.toString(i), i)
            dests.add(dst!!)
            StayConstraint(src!!, Strength.normal)
            ScaleConstraint(src!!, scale, offset, dst!!, Strength.required)
        }

        change(src!!, 17)
        if (dst!!.value != 1170)
            error("Projection test 1 failed!")

        change(dst!!, 1050)
        if (src!!.value != 5)
            error("Projection test 2 failed!")

        change(scale, 5)
        for (i in 0..n - 1 - 1) {
            if ((dests.get(i)).value != i * 5 + 1000)
                error("Projection test 3 failed!")
        }

        change(offset, 2000)
        for (i in 0..n - 1 - 1) {
            if ((dests.get(i)).value != i * 5 + 2000)
                error("Projection test 4 failed!")
        }
    }

    private fun change(`var`: Variable, newValue: Int) {
        val editC = EditConstraint(`var`, Strength.preferred)
        val editV = ArrayList<Constraint>()
        editV.add(editC)
        val plan = planner!!.extractPlanFromConstraints(editV)
        for (i in 0..10 - 1) {
            `var`.value = newValue
            plan.execute()
        }
        editC.destroyConstraint()
    }

    class object {
        public var planner: Planner? = null

        public fun main(args: Array<String>) {
            (DeltaBlue()).inst_main(args)
        }

        public fun error(s: String) {
            System.err.println(s)
            System.exit(1)
        }

    }
}

fun main(args: Array<String>) = DeltaBlue.main(args)