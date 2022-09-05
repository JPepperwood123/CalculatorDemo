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
    public PolyCalc add(PolyCalc p) {
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
    public PolyCalc sub(PolyCalc p) {
        checkRep();
        return this.add(p.negate());
    }

    /**
     * Multiplication operation.
     *
     * @param p the other value to be multiplied
     * @return a RatPoly, r, such that r = "this * p"; if this.isNaN() or p.isNaN(), returns some r
     * such that r.isNaN()
     * @spec.requires p != null
     */
    public RatPoly mul(RatPoly p) {
        checkRep();
        if (this.isNaN() || p.isNaN()) {
            // Returning new object which shows that either polynomial was NaN
            return NaN;
        }
        // Stores result of multiplication of the polynomials
        List<RatTerm> result = new ArrayList<>();

        // {{Inv : result = result + (RatPoly p * RatTerms from positions 0 to position i in this.terms),
        //         where i is the position of the RatTerm in this.terms}}
        for (RatTerm firstTerm : this.terms) {
            // {{Inv : result = result + (firstTerm * RatTerms from positions 0 to position j in p.terms),
            //         where j is the position of the RatTerm in p.terms}}
            for (RatTerm secondTerm : p.terms) {
                // Each individual term from both lists are multiplied and added to the list
                sortedInsert(result, firstTerm.mul(secondTerm));
            }
        }
        checkRep();
        return new RatPoly(result);
    }

    /**
     * Truncating division operation.
     *
     * <p>Truncating division gives the number of whole times that the divisor is contained within the
     * dividend. That is, truncating division disregards or discards the remainder. Over the integers,
     * truncating division is sometimes called integer division; for example, 10/3=3, 15/2=7.
     *
     * <p>Here is a formal way to define truncating division: u/v = q, if there exists some r such
     * that:
     *
     * <ul>
     * <li>u = q * v + r<br>
     * <li>The degree of r is strictly less than the degree of v.
     * <li>The degree of q is no greater than the degree of u.
     * <li>r and q have no negative exponents.
     * </ul>
     * <p>
     * q is called the "quotient" and is the result of truncating division. r is called the
     * "remainder" and is discarded.
     *
     * <p>Here are examples of truncating division:
     *
     * <ul>
     * <li>"x^3-2*x+3" / "3*x^2" = "1/3*x" (with r = "-2*x+3")
     * <li>"x^2+2*x+15 / 2*x^3" = "0" (with r = "x^2+2*x+15")
     * <li>"x^3+x-1 / x+1 = x^2-x+2 (with r = "-3")
     * </ul>
     *
     * @param p the divisor
     * @return the result of truncating division, {@code this / p}. If p = 0 or this.isNaN() or
     * p.isNaN(), returns some q such that q.isNaN().
     * @spec.requires p != null
     */
    public RatPoly div(RatPoly p) {
        checkRep();
        if (this.isNaN() || p.isNaN() || p.equals(ZERO)) {
            // Returning new object which shows that either polynomial was NaN
            return NaN;
        }
        // Stores result of quotient after division between polynomials
        List<RatTerm> result = new ArrayList<>();

        // Stores the dividends on subtraction after dividing
        RatPoly remaining = new RatPoly(this.terms);

        // {{Inv : result = result + the RatTerm with the highest degree in remaining divided by
        //         the RatTerm with the highest degree in p, and remaining = remaining - the quotient
        //         of the above division * the RatPoly P}}
        while (remaining.degree() >= p.degree() && remaining.terms.size() != 0) {
            RatTerm quotient = remaining.getTerm(remaining.degree()).div(p.getTerm(p.degree()));
            RatPoly newPoly = new RatPoly(quotient);
            // Remaining is calculated by subtracting product of divisor and quotient term from it
            remaining = remaining.sub(p.mul(newPoly));
            // Quotient is added to result
            sortedInsert(result, quotient);
        }
        checkRep();
        return new RatPoly(result);
    }

    /**
     * Return the derivative of this RatPoly.
     *
     * @return a RatPoly, q, such that q = dy/dx, where this == y. In other words, q is the derivative
     * of this. If this.isNaN(), then return some q such that q.isNaN().
     * <p>The derivative of a polynomial is the sum of the derivative of each term.
     */
    public RatPoly differentiate() {
        checkRep();
        if (this.isNaN()){
            return NaN;
        }
        List<RatTerm> res = new ArrayList<>();
        //{Inv: [t_0.differentiate() + t_1.differentiate() + ... + t_i-1.differentiate()], where t_j is the jth term in terms}
        for (RatTerm term : terms){
            if (!term.differentiate().isZero()) {
                res.add(term.differentiate());
            }
        }
        checkRep();
        return new RatPoly(res);
    }

    /**
     * Returns the antiderivative of this RatPoly.
     *
     * @param integrationConstant the constant of integration to use when computing the antiderivative
     * @return a RatPoly, q, such that dq/dx = this and the constant of integration is
     * "integrationConstant" In other words, q is the antiderivative of this. If this.isNaN() or
     * integrationConstant.isNaN(), then return some q such that q.isNaN().
     * <p>The antiderivative of a polynomial is the sum of the antiderivative of each term plus
     * some constant.
     * @spec.requires integrationConstant != null
     */
    public RatPoly antiDifferentiate(RatNum integrationConstant) {
        checkRep();
        if (integrationConstant == null) {
            throw new IllegalArgumentException("integrationConstant is required to be not null");
        }
        if (this.isNaN() || integrationConstant.isNaN()){
            return NaN;
        }
        List<RatTerm> result = new ArrayList<RatTerm>();
        // {Inv: [t_0.antiDifferentiate() + t_1.antiDifferentiate() + ... + t_i-1.antiDifferentiate()], where t_j is the jth term in terms}
        for (RatTerm term : terms){
            result.add(term.antiDifferentiate());
        }
        // constant has exponential of 0
        sortedInsert(result, new RatTerm(integrationConstant, 0));
        checkRep();
        return new RatPoly(result);
    }

    /**
     * Returns the integral of this RatPoly, integrated from lowerBound to upperBound.
     *
     * <p>The Fundamental Theorem of Calculus states that the definite integral of f(x) with bounds a
     * to b is F(b) - F(a) where dF/dx = f(x) NOTE: Remember that the lowerBound can be higher than
     * the upperBound.
     *
     * @param lowerBound the lower bound of integration
     * @param upperBound the upper bound of integration
     * @return a double that is the definite integral of this with bounds of integration between
     * lowerBound and upperBound. If this.isNaN(), or either lowerBound or upperBound is
     * Double.NaN, return Double.NaN.
     */
    public double integrate(double lowerBound, double upperBound) {
        checkRep();
        if (this.isNaN() || lowerBound == Double.NaN || upperBound == Double.NaN){
            return Double.NaN;
        }
        RatPoly antiDifTerms = this.antiDifferentiate(RatNum.ZERO);
        double res = antiDifTerms.eval(upperBound) - antiDifTerms.eval(lowerBound);
        checkRep();
        return res;
    }

    /**
     * Returns the value of this RatPoly, evaluated at d.
     *
     * @param d the value at which to evaluate this polynomial
     * @return the value of this polynomial when evaluated at 'd'. For example, "x+2" evaluated at 3
     * is 5, and "x^2-x" evaluated at 3 is 6. If (this.isNaN() == true), return Double.NaN.
     */
    public double eval(double d) {
        checkRep();
        if (this.isNaN()){
            return Double.NaN;
        }
        double res = 0;
        //{inv: result = t_0.eval(d) + ... + t_(i-1).eval(d), where t_j is the jth term in terms}
        for (RatTerm term: terms){
            res += term.eval(d);
        }
        checkRep();
        return res;
    }

    /**
     * Returns a string representation of this RatPoly. Valid example outputs include
     * "x^17-3/2*x^2+1", "-x+1", "-1/2", and "0".
     *
     * @return a String representation of the expression represented by this, with the terms sorted in
     * order of degree from highest to lowest.
     * <p>There is no whitespace in the returned string.
     * <p>If the polynomial is itself zero, the returned string will just be "0".
     * <p>If this.isNaN(), then the returned string will be just "NaN".
     * <p>The string for a non-zero, non-NaN poly is in the form "(-)T(+|-)T(+|-)...", where "(-)"
     * refers to a possible minus sign, if needed, and "(+|-)" refers to either a plus or minus
     * sign. For each term, T takes the form "C*x^E" or "C*x" where {@code C > 0}, UNLESS: (1) the
     * exponent E is zero, in which case T takes the form "C", or (2) the coefficient C is one, in
     * which case T takes the form "x^E" or "x". In cases were both (1) and (2) apply, (1) is
     * used.
     */
    @Override
    public String toString() {

        if(terms.size() == 0) {
            return "0";
        }
        if(isNaN()) {
            return "NaN";
        }
        StringBuilder output = new StringBuilder();
        boolean isFirst = true;
        for(RatTerm rt : terms) {
            if(isFirst) {
                isFirst = false;
                output.append(rt.toString());
            } else {
                if(rt.getCoeff().isNegative()) {
                    output.append(rt.toString());
                } else {
                    output.append("+" + rt.toString());
                }
            }
        }
        return output.toString();
    }

    /**
     * Builds a new RatPoly, given a descriptive String.
     *
     * @param polyStr a string of the format described in the @spec.requires clause.
     * @return a RatPoly p such that p.toString() = polyStr
     * @spec.requires 'polyStr' is an instance of a string with no spaces that expresses a poly in the
     * form defined in the toString() method.
     * <p>Valid inputs include "0", "x-10", and "x^3-2*x^2+5/3*x+3", and "NaN".
     */
    public static RatPoly valueOf(String polyStr) {

        List<RatTerm> parsedTerms = new ArrayList<>();

        // First we decompose the polyStr into its component terms;
        // third arg orders "+" and "-" to be returned as tokens.
        StringTokenizer termStrings = new StringTokenizer(polyStr, "+-", true);

        boolean nextTermIsNegative = false;
        while(termStrings.hasMoreTokens()) {
            String termToken = termStrings.nextToken();

            if(termToken.equals("-")) {
                nextTermIsNegative = true;
            } else if(termToken.equals("+")) {
                nextTermIsNegative = false;
            } else {
                // Not "+" or "-"; must be a term
                RatTerm term = RatTerm.valueOf(termToken);

                // at this point, coeff and expt are initialized.
                // Need to fix coeff if it was preceeded by a '-'
                if(nextTermIsNegative) {
                    term = term.negate();
                }

                // accumulate terms of polynomial in 'parsedTerms'
                sortedInsert(parsedTerms, term);
            }
        }
        return new RatPoly(parsedTerms);
    }

    /**
     * Standard hashCode function.
     *
     * @return an int that all objects equal to this will also return
     */
    @Override
    public int hashCode() {
        // all instances that are NaN must return the same hashcode;
        if(this.isNaN()) {
            return 0;
        }
        return terms.hashCode();
    }

    /**
     * Standard equality operation.
     *
     * @param obj the object to be compared for equality
     * @return true if and only if 'obj' is an instance of a RatPoly and 'this' and 'obj' represent
     * the same rational polynomial. Note that all NaN RatPolys are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RatPoly) {
            RatPoly rp = (RatPoly) obj;

            // special case: check if both are NaN
            if(this.isNaN() && rp.isNaN()) {
                return true;
            } else {
                return terms.equals(rp.terms);
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
     * Inserts a term into a sorted sequence of terms, preserving the sorted nature of the sequence.
     * If a term with the given degree already exists, adds their coefficients (helper procedure).
     *
     * <p>Definitions: Let a "Sorted List<RatTerm>" be a List<RatTerm> V such that [1] V is sorted in
     * descending exponent order && [2] there are no two RatTerms with the same exponent in V && [3]
     * there is no RatTerm in V with a coefficient equal to zero
     *
     * <p>For a Sorted List<RatTerm> V and integer e, let cofind(V, e) be either the coefficient for a
     * RatTerm rt in V whose exponent is e, or zero if there does not exist any such RatTerm in V.
     * (This is like the coeff function of RatPoly.) We will write sorted(lst) to denote that lst is a
     * Sorted List<RatTerm>, as defined above.
     *
     * @param lst     the list into which newTerm should be inserted
     * @param newTerm the term to be inserted into the list
     * @spec.requires lst != null && sorted(lst)
     * @spec.modifies lst
     * @spec.effects sorted(lst_post) && (cofind(lst_post,newTerm.getExpt()) =
     * cofind(lst,newTerm.getExpt()) + newTerm.getCoeff())
     */
    private static void sortedInsert(List<TermCalc> lst, TermCalc newTerm) {
        if (!newTerm.isZero()) {
            int i = 0;
            // {{Inv : i < lst.size() and the exponent of lst at position 0 to position i >= the exponent of
            //          the newTerm}}
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