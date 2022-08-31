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

    @Override
    public int compareTo(NumIndiv comp) {
        if (this.isNaN() && comp.isNaN()) {
            return 0;
        } else if (this.isNaN()) {
            return 1;
        } else if (comp.isNaN()) {
            return -1;
        }

        NumIndiv curr = this.subtract(comp);
        return curr.numerator;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return intValue();
    }

    @Override
    public float floatValue() {
        if (isNaN()) {
            return Float.NaN;
        }
        return ((float) numerator) / ((float) denominator);
    }

    @Override
    public double doubleValue() {
        if (isNaN()) {
            return Double.NaN;
        }
        return ((double) numerator) / ((double) denominator);
    }
}