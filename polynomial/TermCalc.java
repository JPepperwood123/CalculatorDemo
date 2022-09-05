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

    /**
     * Return the derivative of this TermCalc.
    */
    public TermCalc differentiation() {
        if(isNaN()) {
            return NaN;
        }
        return new TermCalc(coefficient.multiplication(new NumIndiv(exponent)), exponent - 1);
    }

    /**
     * Return the derivative of this TermCalc.
     */
    public TermCalc antiDifferentiation() {
        if(isNaN()) {
            return NaN;
        }
        return new TermCalc(coefficient.division(new NumIndiv(exponent + 1)), exponent + 1);
    }

    /**
     * Returns the hashCode of this specific TermCalc
     */
    @Override
    public int hashCode() {
        if(isNaN()) {
            return -1;
        }

        int result = 1;
        result = 31 * result + coefficient.hashCode();
        result = 31 * result + (exponent * 47);

        checkRep();
        return result;
    }

    /**
     * Equality operation.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TermCalc) {
            TermCalc tc = (TermCalc) obj;

            // Check if both are NaN
            if(this.isNaN() && tc.isNaN()) {
                return true;
            } else {
                return this.coefficient.equals(tc.getCoeff()) && (this.exponent == tc.getExpt());
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the String representation of TermCalc
     */
    @Override
    public String toString() {
        if(isNaN()) {
            return "NaN";
        }

        String result = "";
        NumIndiv tempCoefficent = coefficient;

        if(tempCoefficent.isNegativeNumber()) {
            result += "-";
            tempCoefficent = tempCoefficent.negate();
        }

        if(tempCoefficent.equals(ONE) && exponent == 1) {
            result += "x";
        } else if(exponent == 0) {
            result += tempCoefficent.toString();
        } else if(tempCoefficent.equals(ONE)) {
            result += "x^" + exponent;
        } else if(exponent == 1) {
            result += tempCoefficent + "*x";
        } else {
            result += tempCoefficent + "*x^" + exponent;
        }
        checkRep();
        return result;
    }

    /**
     * Returns a TermCalc given a string representing the value
     */
    public static TermCalc valueOf(String termStr) {
        if(termStr.equals("NaN")) {
            // NaN case
            return NaN;
        }

        // A TermCalc is either of the form A*x^B or A*x or x^B or A or x, where A is a NumIndiv
        // and B is an integer

        int multiplicationLocation = termStr.indexOf("*");
        int powerLocation = termStr.indexOf("^");
        int xLocation = termStr.indexOf("x");
        NumIndiv currentCoefficient;

        if (multiplicationLocation == -1) {
            // Forms x^B or A or x
            if (xLocation == -1) {
                // Form A only
                currentCoefficient = NumIndiv.valueOf(termStr);
            } else {
                if (termStr.indexOf("-") == 0) {
                    // -x^B or -x means coefficeient -1
                    currentCoefficient = new NumIndiv(-1);
                } else if (termStr.indexOf("-") == 1) {
                    // -x^B or -x means coefficient 1
                    currentCoefficient = new NumIndiv(1);
                } else {
                    throw new RuntimeException("Incorrect format of \"-\" sign");
                }
            }
        } else {
            // Forms A*x^B or A*x
            currentCoefficient = NumIndiv.valueOf(termStr.substring(0, multiplicationLocation));
        }

        int currentExponent;

        if (powerLocation == -1) {
            // Forms A*x or A or x
            if (xLocation == -1) {
                // Form A
                currentExponent = 0;
            } else {
                // Forms A*x or x
                currentExponent = 1;
            }
        } else {
            // Forms A*x^B or x^B
            currentExponent = Integer.parseInt(termStr.substring(powerLocation + 1));
        }

        return new TermCalc(currentCoefficient, currentExponent);
    }
}