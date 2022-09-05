package polynomial;

import java.util.*;

/**
 * PolyCal represents a single polynomial expresssions which is in the form of a sum
 * of a list of TermCalc expressions
 */
public class PolyCalc {
    /**
     * Holds the list of TermCalc which this PolyCal is a sum of
     */
    private final List<TermCalc> listOfTerms;

    // Representation Invariant:
    // listOfTerms consists of TermCalc in which no term has a zero coefficient,
    // negative exponent and the terms are stored in listOfTerms in descending order

    /**
     * Constants to hold a NaN valye and a zero value in a PolyCalc
     */
    public static final PolyCalc NaN = new PolyCalc(TermCalc.NaN);

    public static final PolyCalc ZERO = new PolyCalc();

    /**
     * If the representation invariant is violated, throws an exception
     */
    private void checkRep() {
        assert (listOfTerms != null);

        for(int i = 0; i < listOfTerms.size(); i++) {
            assert (!listOfTerms.get(i).getCoeff().equals(NumIndiv.ZERO)); // Check for no zero coefficient
            assert (listOfTerms.get(i).getExpt() >= 0); // Check for no negative exponent

            // Check for terms in descending order of exponent
            if (i < listOfTerms.size() - 1) {
                assert (listOfTerms.get(i + 1).getExpt() < listOfTerms.get(i).getExpt());
            }
        }
    }

    /**
     * Default constructor with no terms
     */
    public PolyCalc() {
        listOfTerms = new ArrayList<>();
        checkRep();
    }

    /**
     * Constructor which creates a polynomial expression PolyCalc with "tc" as the only non-zero term
     */
    public PolyCalc(TermCalc tc) {
        listOfTerms = new ArrayList<>();

        if (!tc.isZero()) {
            listOfTerms.add(tc);
        }
        checkRep();
    }

    /**
     * Constructor which creates a polynomial expression PolyCalc with a TermCalc with defined coefficient and exponent
     */
    public PolyCalc(int coefficient, int exponent) {
        this(new TermCalc(new NumIndiv(coefficient), exponent));
        checkRep();
    }

    /**
     * Constructor which creates a polynomial expression with a list of TermCalc
     */
    private PolyCalc(List<TermCalc> listTC) {
        listOfTerms = listTC;
        checkRep();
    }

    /**
     * Returns the highest degree of this polynomial expression of PolyCalc and 0 if no term
     */
    public int getDegree() {
        checkRep();
        if (listOfTerms.size() != 0) {
            checkRep();
            return listOfTerms.get(0).getExpt();
        }
        checkRep();
        // No term in this PolyCalc
        return 0;
    }

    /**
     * Returns the term whose degree is "currExponent" and 0 if no such term exists
     */
    public TermCalc getTerm(int currExponent) {
        checkRep();
        for (TermCalc tc : listOfTerms) {
            if (tc.getExpt() == currExponent) {
                checkRep();
                return tc;
            }
        }

        checkRep();
        // No such term in this PolyCalc
        return TermCalc.ZERO;
    }

    /**
     * Checks if this Polycalc is NaN
     */
    public boolean isNaN() {
        checkRep();
        for (TermCalc tc : listOfTerms) {
            if (tc.isNaN()) {
                // If any coefficient is NaN, true is returned
                checkRep();
                return true;
            }
        }
        checkRep();
        // Returning false as no coefficient was NaN
        return false;
    }

    /**
     * Return the additive inverse of this PolyCalc
     */
    public PolyCalc negate() {
        checkRep();
        if(isNaN()) {
            return PolyCalc.NaN;
        }
        List<TermCalc> newTerms = new ArrayList<>(this.listOfTerms);
        scaleCoeff(newTerms, new NumIndiv(-1));

        checkRep();
        return new PolyCalc(newTerms);
    }

    /**
     * Returns the sum of two PolyCalcs
     */
    public PolyCalc addition(PolyCalc p) {
        checkRep();
        if (this.isNaN() || p.isNaN()) {
            return PolyCalc.NaN;
        }

        // First adding all terms in the current list
        List<TermCalc> result = new ArrayList<>(this.listOfTerms);

        // Next all terms from p are added using "sortedInsert"
        for (TermCalc newTerm : p.listOfTerms) {
            sortedInsert(result, newTerm);
        }

        checkRep();
        return new PolyCalc(result);
    }

    /**
     * Returns the difference of two PolyCalcs
     */
    public PolyCalc subtract(PolyCalc p) {
        checkRep();
        return this.addition(p.negate());
    }

    /**
     * Returns the product of two PolyCalcs
     */
    public PolyCalc multiplication(PolyCalc p) {
        checkRep();
        if (this.isNaN() || p.isNaN()) {
            return PolyCalc.NaN;
        }
        List<TermCalc> result = new ArrayList<>();

        for (TermCalc firstTerm : this.listOfTerms) {
            for (TermCalc secondTerm : p.listOfTerms) {
                // Each individual term from both lists are multiplied and added to the list
                sortedInsert(result, firstTerm.multiplication(secondTerm));
            }
        }

        checkRep();
        return new PolyCalc(result);
    }

    /**
     * Returns the truncating division of two PolyCalcs such that it returns the quotient and disregards
     * the remainder
     */
    public PolyCalc division(PolyCalc p) {
        checkRep();
        if (this.isNaN() || p.isNaN() || p.equals(ZERO)) {
            return NaN;
        }

        List<TermCalc> result = new ArrayList<>();

        PolyCalc remaining = new PolyCalc(this.listOfTerms);

        while (remaining.getDegree() >= p.getDegree() && remaining.listOfTerms.size() != 0) {
            // Dividing term of highest degree of remaining with that of p
            TermCalc quotient = remaining.getTerm(remaining.getDegree()).division(p.getTerm(p.getDegree()));

            PolyCalc newPoly = new PolyCalc(quotient);
            // Remaining is calculated by subtracting product of divisor and quotient term from it
            remaining = remaining.subtract(p.multiplication(newPoly));
            // Quotient is added to result
            sortedInsert(result, quotient);
        }
        checkRep();
        return new PolyCalc(result);
    }

    /**
     * Return the derivative of this PolyCalc
     */
    public PolyCalc differentiate() {
        checkRep();
        if (this.isNaN()){
            return PolyCalc.NaN;
        }

        List<TermCalc> res = new ArrayList<>();

        for (TermCalc term : this.listOfTerms){
            TermCalc diff = term.differentiation();
            if (!diff.isZero()) {
                res.add(diff);
            }
        }

        checkRep();
        return new PolyCalc(res);
    }

    /**
     * Returns the antiderivative of this PolyCalc
     */
    public PolyCalc antiDifferentiate(NumIndiv integrationConstant) {
        checkRep();
        if (integrationConstant == null) {
            throw new IllegalArgumentException("integrationConstant is null");
        }
        if (this.isNaN() || integrationConstant.isNaN()){
            return PolyCalc.NaN;
        }

        List<TermCalc> result = new ArrayList<>();

        for (TermCalc term : this.listOfTerms){
            result.add(term.antiDifferentiation());
        }

        // constant has exponential of 0
        sortedInsert(result, new TermCalc(integrationConstant, 0));

        checkRep();
        return new PolyCalc(result);
    }

    /**
     * Returns the integral of this PolyCalc, integrated from lowerBound to upperBound
     */
    public double integrateBounds(double lowerBound, double upperBound) {
        checkRep();
        if (this.isNaN() || Double.isNaN(lowerBound) || Double.isNaN(upperBound)){
            return Double.NaN;
        }

        PolyCalc antiDifTerms = this.antiDifferentiate(NumIndiv.ZERO);

        double res = antiDifTerms.evaluate(upperBound) - antiDifTerms.evaluate(lowerBound);

        checkRep();
        return res;
    }

    /**
     * Returns the value of this PolyCalc, evaluated at value
     */
    public double evaluate(double value) {
        checkRep();

        if (this.isNaN()){
            return Double.NaN;
        }

        double res = 0;
        for (TermCalc term: this.listOfTerms){
            res += term.evaluatePoly(value);
        }


        checkRep();
        return res;
    }

    /**
     * Returns the String representation of PolyCalc
     */
    @Override
    public String toString() {
        if(listOfTerms.size() == 0) {
            return "0";
        }

        if(isNaN()) {
            return "NaN";
        }

        StringBuilder output = new StringBuilder();
        boolean first = true;
        for (TermCalc tc : listOfTerms) {
            if (first) {
                first = false;
                output.append(tc.toString());
            } else {
                if (tc.getCoeff().isNegativeNumber()) {
                    output.append(tc.toString());
                } else {
                    output.append("+").append(tc.toString());
                }
            }
        }

        return output.toString();
    }

    /**
     * Returns a PolyCalc given a string representing the value
     */
    public static PolyCalc valueOf(String polyStr) {

        List<TermCalc> parsedTerms = new ArrayList<>();
        StringTokenizer termStrings = new StringTokenizer(polyStr, "+-", true);

        boolean nextNegative = false;
        while (termStrings.hasMoreTokens()) {
            String termToken = termStrings.nextToken();

            if (termToken.equals("-")) {
                nextNegative = true;
            } else if (termToken.equals("+")) {
                nextNegative = false;
            } else {
                // Not "+" or "-"; must be a term
                TermCalc term = TermCalc.valueOf(termToken);

                // Need to fix coeff if it was preceeded by a '-'
                if (nextNegative) {
                    term = term.negate();
                }

                // accumulate terms of polynomial in 'parsedTerms'
                sortedInsert(parsedTerms, term);
            }
        }
        return new PolyCalc(parsedTerms);
    }

    /**
     * Standard hashCode function.
     */
    @Override
    public int hashCode() {
        if (this.isNaN()) {
            return 0;
        }
        return listOfTerms.hashCode();
    }

    /**
     * Returns the hashCode of this specific PolyCalc
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PolyCalc) {
            PolyCalc pc = (PolyCalc) obj;

            // special case: check if both are NaN
            if (this.isNaN() && pc.isNaN()) {
                return true;
            } else {
                return this.listOfTerms.equals(pc.listOfTerms);
            }
        } else {
            return false;
        }
    }

    /**
     * Scales coefficients in newList by an integer scalar
     */
    private static void scaleCoeff(List<TermCalc> newList, NumIndiv scalar) {
        for (int i = 0; i < newList.size(); i++) {
            TermCalc curr = newList.get(i);
            // Multiplying the coefficient by scalar for all values in newList
            newList.set(i, new TermCalc(curr.getCoeff().multiplication(scalar), curr.getExpt()));
        }
    }

    /**
     * Inserts a TermCalc into a sorted sequence of TermCalc, preserving the sorted nature of the sequence.
     * If a TermCalc with the given degree already exists, it adds their coefficients
     */
    private static void sortedInsert(List<TermCalc> lst, TermCalc newTerm) {
        if (!newTerm.isZero()) {
            int i = 0;
            while (i < lst.size() && lst.get(i).getExpt() >= newTerm.getExpt()) {
                // Checks if the exponents in any case are ever equal
                if (lst.get(i).getExpt() == newTerm.getExpt()) {
                    // Coefficients of the like terms are added in this case
                    lst.set(i, new TermCalc(lst.get(i).getCoeff().addition(newTerm.getCoeff()), newTerm.getExpt()));
                    // If the coefficient upon addition is 0, the term is removed from the list
                    if (lst.get(i).isZero()) {
                        lst.remove(i);
                        i--;
                    }
                    return;
                }
                i++;
            }
            // Added to the position just before the degree on newTerm is lower in the list than the one in newTerm
            lst.add(i, newTerm);
        }
    }
}