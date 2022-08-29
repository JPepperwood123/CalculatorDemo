/**
* NumIndiv holds any individual number who is a rational number so that it
* can be divided into a numerator and denominator. NaN is a special NumIndiv whose denomiator is zero.
*
*/
public final class NumIndiv extends Number implements Comparable<NumIndiv> {
  private final int numerator, denominator;

  public static final NumIndiv NaN = new NumIndiv(1, 0);

  public static final NumIndiv zero = new NumIndiv(0);

  
  public NumIndiv(int n) {
      numerator = n;
      denominator = 1;
      checkRep();
  }

  public NumIndiv(int n, int d) {
      if (d != 0) {
          // Reduce individual number to the lowest form
          n /= gcd(n, d);
          d /= gcd(n, d);
          
          if(d < 0) {
            numerator = -n;
            denominator = -d;
          }
      } 

      numerator = n;
      denominator = d;
    
      checkRep();
  }
}