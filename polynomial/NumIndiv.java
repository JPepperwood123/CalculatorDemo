package polynomial;

/**
 * NumIndiv holds any individual number who is a rational number so that it
 * can be divided into a numerator and denominator. NaN is a special NumIndiv
 * whose denomiator is zero. NaN is considered a positive number.
 *
 */
public final class NumIndiv extends Number implements Comparable<NumIndiv> {
    // Representation invariant for every NumIndiv n:
    //   n.denominator >= 0 && n.denominator > 0 and ==> r.numerator and r.denominator are reduced to its lowest forms)


    private final int numerator, denominator;

    public static final NumIndiv NaN = new NumIndiv(1, 0);
    public static final NumIndiv negNaN = new NumIndiv(-1, 0);
    public static final NumIndiv zero = new NumIndiv(0);

    /**
     * Constructor with numerator value only
     */
    public NumIndiv(int n) {
        numerator = n;
        denominator = 1;
        checkRepInv();
    }

    /**
     * Constructor with numerator and denominator value
     */
    public NumIndiv(int n, int d) {
        if (d != 0) {
            // Reduce individual number to the lowest form
            n /= gcd(n, d);
            d /= gcd(n, d);

            if(d < 0) {
                n = -n;
                d = -d;
            }
        }

        numerator = n;
        denominator = d;

        checkRepInv();
    }

    /**
     * If the representation invariant is violated, throws an exception
     */
    private void checkRepInv() {
        assert (denominator >= 0) : "Denominator can't be negative";

        if (denominator > 0) {
            int thisGcd = gcd(numerator, denominator);
            assert (thisGcd == 1 || thisGcd == -1) : "Not in lowest form";
        }
    }

    /**
     * Checks if the NumIndiv is not a number
     */
    public boolean isNaN() {
        return denominator == 0;
    }

    /**
     * Checks if the NumIndiv is negative.
     */
    public boolean isNegativeNumber() {
        if (isNaN()) {
            return false;
        }
        return numerator < 0;
    }

    /**
     * Checks if the NumIndiv is a positive number
     */
    public boolean isPositiveNumber() {
        if (isNaN()) {
            return true;
        }
        return numerator > 0;
    }

    /**
     * Compares two NumIndiv and checks if it this is greater than "comp"
     */
    @Override
    public int compareTo(NumIndiv argument) {
        if (this.isNaN() && argument.isNaN()) {
            return 0;
        } else if (this.isNaN()) {
            return 1;
        } else if (argument.isNaN()) {
            return -1;
        }

        NumIndiv curr = this.subtraction(argument);
        return curr.numerator;
    }

    /**
     * Approximates value to an integer
     */
    @Override
    public int intValue() {
        return numerator / denominator;
    }

    /**
     * Approximates value to a long value
     */
    @Override
    public long longValue() {
        return intValue();
    }

    /**
     * Approximates value to a float value
     */
    @Override
    public float floatValue() {
        if (isNaN()) {
            return Float.NaN;
        }
        return ((float) numerator) / ((float) denominator);
    }

    /**
     * Approximates value to a double value
     */
    @Override
    public double doubleValue() {
        if (isNaN()) {
            return Double.NaN;
        }
        return ((double) numerator) / ((double) denominator);
    }

    /**
     * Returns the negation of the current NumIndiv
     */
    public NumIndiv negate() {
        return new NumIndiv(-numerator, denominator);
    }

    /**
     * Returns the sum of two NumIndiv
     */
    public NumIndiv addition(NumIndiv argument) {
        if (argument.isNaN()) {
            return NaN;
        }

        // Can directly do the operation as constructor does GCD while initializing
        return new NumIndiv((this.numerator * argument.denominator) + (argument.numerator * this.denominator),
                                this.denominator * argument.denominator);
    }

    /**
     * Returns the difference of two NumIndiv
     */
    public NumIndiv subtraction(NumIndiv argument) {
        if (argument.isNaN()) {
            return negNaN;
        }

        // Adding the negation of one is the subtraction of the numbers
        NumIndiv argNeg = argument.negate();
        return this.addition(argNeg);
    }

    /**
     * Returns the product of two NumIndiv
     */
    public NumIndiv multiplication(NumIndiv argument) {
        if (argument.isNaN()) {
            return NaN;
        }

        // Can directly do the operation as constructor does GCD while initializing
        return new NumIndiv(this.numerator * argument.numerator, this.denominator * argument.denominator);
    }

    /**
     * Returns the division of two NumIndiv
     */
    public NumIndiv division(NumIndiv argument) {
        if (argument.isNaN()) {
            return zero;
        }

        // Can directly do the operation as constructor does GCD while initializing
        return new NumIndiv(this.numerator * argument.denominator, this.denominator * argument.numerator);
    }

    /**
     * Returns the hashCode of this specific NumIndiv
     */
    @Override
    public int hashCode() {
        if(isNaN()) {
            return -1;
        }

        int result = 1;
        int currGcd = gcd(numerator, denominator);
        result = 31 * result + (numerator ^ currGcd);
        result = 31 * result + (currGcd ^ denominator);

        return result;
    }

    /**
     * Equality operation.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof NumIndiv) {
            NumIndiv ni = (NumIndiv) obj;

            // Check if both are NaN
            if(this.isNaN() && ni.isNaN()) {
                return true;
            } else {
                return (this.numerator == ni.numerator) && (this.denominator == ni.denominator);
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the String representation of NumIndiv
     */
    @Override
    public String toString() {
        if(isNaN()) {
            return "NaN";
        }

        if (denominator != 1) {
            return numerator + "/" + denominator;
        } else {
            return numerator + "";
        }
    }

    public static NumIndiv valueOf(String numStr) {
        if(numStr.equals("NaN")) {
            // NaN case
            return new NumIndiv(1, 0);
        } else if(numStr.indexOf('/') == -1) {
            // Integer case
            return new NumIndiv(Integer.parseInt(numStr));
        } else {
            int currentNumerator = Integer.parseInt(numStr.substring(0, numStr.indexOf('/')));
            int currentDenominator = Integer.parseInt(numStr.substring(numStr.indexOf('/') + 1));
            return new NumIndiv(currentNumerator, currentDenominator);
        }
    }

    /**
     * Returns the greatest common divisor of 'a' and 'b'.
     */
    private static int gcd(int first, int second) {
        // Euclid's method
        if(second == 0) {
            return 0;
        }

        while (second != 0) {
            int tmp = second;
            second = first % second;
            first = tmp;
        }
        return first;
    }
}