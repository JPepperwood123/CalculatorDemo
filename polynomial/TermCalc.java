package polynomial;

/**
 * TermCalc represents a single polynomial term expression which is of the form : A*x^B
 * where A is a NumIndiv representing the coefficient of TermCalc and B is an integer
 * value representing the power of x
 */
public class TermCalc {

    private final NumIndiv coefficient;

    private final int exponent;

    // Representation Invariant:
    // coefficient is not null and if the coefficient is NumIndiv.ZERO, exponent is 0

    // Constants
    public static final TermCalc NaN = new TermCalc(NumIndiv.NAN, 0);
    public static final TermCalc ZERO = new TermCalc(NumIndiv.ZERO, 0);
    private static final NumIndiv ONE = new NumIndiv(1);

    /**
     * Constructor with coefficient and exponent values
     */
    public TermCalc(NumIndiv c, int e) {
        this.coefficient = c;
        if (c.equals(NumIndiv.ZERO)) {
            this.exponent = 0;
        } else {
            this.exponent = e;
        }
        checkRep();
    }

    /**
     * If the representation invariant is violated, throws an exception
     */
    private void checkRep() {
        assert (coefficient != null);

        assert (!coefficient.equals(NumIndiv.ZERO) || exponent == 0);
    }

    /**
     * Returns coefficient of this TermCalc.
     */
    public NumIndiv getCoeff() {
        return coefficient;
    }

    /**
     * Returns exponent of this TermCalc.
     */
    public int getExpt() {
        return exponent;
    }

    /**
     * Checks if the TermCalc is not a number
     */
    public boolean isNaN() {
        return coefficient.isNaN();
    }

    /**
     * Checks if the TermCalc is zero
     */
    public boolean isZero() {
        return coefficient.equals(NumIndiv.ZERO);
    }

    /**
     * Returns value of the polynomial at value of xValue
     */
    public double evaluatePoly(double xValue) {
        if (this.isNaN()) {
            return Double.NaN;
        }

        return (coefficient.doubleValue()) * Math.pow(xValue, exponent);
    }

    /**
     * Returns the negation of the current TermCalc
     */
    public TermCalc negate() {
        return new TermCalc(coefficient.negate(), exponent);
    }

    /**
     * Returns the sum of two NumIndiv
     */
    public TermCalc addition(TermCalc argument) {
        if (this.exponent != argument.getExpt() && !this.isNaN() && !this.isZero()
                && !argument.isNaN() && !argument.isZero()) {
            throw new IllegalArgumentException("Can't add two different exponents");
        }

        if (this.isNaN() || argument.isNaN()) {
            return NaN;
        }

        return new TermCalc(this.coefficient.addition(argument.getCoeff()), this.exponent);
    }

    /**
     * Returns the difference of two NumIndiv
     */
    public TermCalc subtraction(TermCalc argument) {
        TermCalc argNeg = argument.negate();
        return this.addition(argNeg);
    }

    /**
     * Returns the product of two TermCalc
     */
    public TermCalc multiplication(TermCalc argument) {
        if (this.isNaN() || argument.isNaN()) {
            return NaN;
        }

        return new TermCalc(this.coefficient.multiplication(argument.getCoeff()), this.exponent + argument.getExpt());
    }

    /**
     * Returns the division of two TermCalc
     */
    public TermCalc division(TermCalc argument) {
        if (this.isNaN() || argument.isNaN()) {
            return NaN;
        }

        return new TermCalc(this.coefficient.division(argument.getCoeff()), this.exponent - argument.getExpt());
    }

}
