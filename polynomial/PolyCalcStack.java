package polynomial;

import java.util.Iterator;
import java.util.Stack;

/**
 * PolyCalcStack represents a mutable finite sequence of PolyCalc objects
 */
@SuppressWarnings("JdkObsolete")
public final class PolyCalcStack implements Iterable<PolyCalc> {

    /**
     * Stack containing the PolyCalc objects.
     */
    private final Stack<PolyCalc> polyStack;

    // RepInvariant:
    // polyStack != null && polys.get(i) != null for all i

    /**
     * If the representation invariant is violated, throws an exception
     */
    private void checkRep() {
        assert (polyStack != null);

        for(PolyCalc p : polyStack) {
            assert (p != null);
        }
    }

    /**
     * Default constructor with no terms
     */
    public PolyCalcStack() {
        polyStack = new Stack<>();
        checkRep();
    }

    /**
     * Returns the number of PolyCalcs in this PolyCalcStack
     */
    public int size() {
        checkRep();
        return polyStack.size();
    }

    /**
     * Pushes a PolyCalc onto the top of this PolyCalcStack
     */
    public void push(PolyCalc currPoly) {
        if (currPoly == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        checkRep();
        polyStack.push(currPoly);
        checkRep();
    }

    /**
     * Removes and returns the top PolyCalc
     */
    public PolyCalc pop() {
        if (this.size() < 1) {
            throw new IllegalArgumentException("size less than 1");
        }
        checkRep();
        return polyStack.pop();
    }

    /**
     * Duplicates the top element on the RatPolyStack
     */
    public void duplicate() {
        if (this.size() < 1) {
            throw new IllegalArgumentException("size less than 1");
        }
        checkRep();
        PolyCalc duplicate = this.pop();
        // Pushes twice hence duplicating it
        this.push(duplicate);
        this.push(duplicate);
        checkRep();
    }

    /**
     * Swaps the top two elements on the RatPolyStack
     */
    public void swap() {
        checkRep();
        if (this.size() > 1) {
            PolyCalc first = this.pop();
            PolyCalc second = this.pop();
            // LIFO property so pushes in same order of popping to swap it
            this.push(first);
            this.push(second);
        }
        checkRep();
    }

    /**
     * Clears all PolyCalcs from the RatPolyStack
     */
    public void clear() {
        checkRep();
        polyStack.clear();
        checkRep();
    }

    /**
     * Returns the RatPoly that is 'index' positions from the top of the RatPolyStack
     */
    public PolyCalc getNthFromTop(int index) {
        checkRep();
        if (index <= this.size()) {
            return polyStack.get(this.size() - index - 1);
        } else {
            return PolyCalc.ZERO;
        }
    }

    /**
     * Adds the top two values of RatPolyStack and places the result on top of the stack.
     */
    public void addition() {
        checkRep();
        if (this.size() > 1) {
            PolyCalc first = this.pop();
            PolyCalc second = this.pop();
            this.push(second.addition(first));
        }
        checkRep();
    }

    /**
     * Subtracts the top value from the next top of RatPolyStack and places the result on top of the stack.
     */
    public void subtraction() {
        checkRep();
        if (this.size() > 1) {
            PolyCalc first = this.pop();
            PolyCalc second = this.pop();
            this.push(second.subtract(first));
        }
        checkRep();
    }

    /**
     * Multiplies the top value from the next top of RatPolyStack and places the result on top of the stack.
     */
    public void multiplication() {
        checkRep();
        if (this.size() > 1) {
            PolyCalc first = this.pop();
            PolyCalc second = this.pop();
            this.push(second.multiplication(first));
        }
        checkRep();

    }

    /**
     * Divides the top value from the next top of RatPolyStack and places the result on top of the stack.
     */
    public void division() {
        checkRep();
        if (this.size() > 1) {
            PolyCalc first = this.pop();
            PolyCalc second = this.pop();
            this.push(second.division(first));
        }
        checkRep();
    }

     /**
     * Differentiates the top value of RatPolyStack and places the result on top of the stack.
     */
    public void differentiate() {
        if (this.size() < 1) {
            throw new IllegalArgumentException("size less than 1");
        }
        checkRep();
        PolyCalc p = this.pop().differentiate();
        this.push(p);
        checkRep();
    }

    /**
     * Integrates the top value of RatPolyStack and places the result on top of the stack.
     */
    public void integrate() {
        if (this.size() < 1) {
            throw new IllegalArgumentException("size less than 1");
        }
        checkRep();
        PolyCalc p = this.pop().antiDifferentiate(NumIndiv.ZERO);
        this.push(p);
        checkRep();
    }

    /**
     * Returns an iterator of the elements contained in the stack.
     *
     * @return an iterator of the elements contained in the stack in order from the bottom of the
     * stack to the top of the stack
     */
    @Override
    public Iterator<PolyCalc> iterator() {
        return polyStack.iterator();
    }
}