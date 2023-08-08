package CryptoUtilities;

import java.math.BigInteger;

public class EllipticCurvePoint
{
    private BigInteger x,y;
    private final EllipticCurve parentCurve;
    private final boolean infinity;
    public EllipticCurvePoint(BigInteger x, BigInteger y, EllipticCurve parentCurve)
    {
        this.x = x;
        this.y = y;
        this.parentCurve = parentCurve;
        this.infinity = false;
    }
    public EllipticCurvePoint(EllipticCurve parentCurve)
    {
        this.parentCurve = parentCurve;
        this.infinity = true;
    }
    public EllipticCurvePoint opposite()
    {
        return new EllipticCurvePoint(this.x, this.y.negate().mod(parentCurve.getP()), parentCurve);
    }
    public EllipticCurvePoint add(EllipticCurvePoint other)
    {
        BigInteger lambda, x3, y3;
        BigInteger a = parentCurve.getA();
        BigInteger p = parentCurve.getP();
        if (this.infinity)
        {
            return other;
        }
        if (other.infinity)
        {
            return this;
        }
        if (this.equals(other.opposite()))
        {
            return new EllipticCurvePoint(parentCurve);
        }
        if (this.equals(other))
        {
            BigInteger nominator = ((((x.modPow(BigInteger.TWO, p)).multiply(BigInteger.valueOf(3))).mod(p)).add(a)).mod(p);
            BigInteger denominator = ((y.multiply(BigInteger.valueOf(2))).mod(p)).modInverse(p);
            lambda = (nominator.multiply(denominator)).mod(p);
        }
        else
        {
            BigInteger nominator = (other.y.subtract(y)).mod(p);
            BigInteger denominator = ((other.x.subtract(x)).mod(p)).modInverse(p);
            lambda = (nominator.multiply(denominator)).mod(p);
        }
        x3 = (((lambda.modPow(BigInteger.TWO, p)).add(x.negate().mod(p))).add(other.x.negate().mod(p))).mod(p);
        y3 = ((((x.add(x3.negate().mod(p))).multiply(lambda)).mod(p)).add(y.negate().mod(p))).mod(p);
        return new EllipticCurvePoint(x3, y3, parentCurve);
    }
    public EllipticCurvePoint mult(BigInteger k)
    {
        EllipticCurvePoint R0 = new EllipticCurvePoint(parentCurve);
        EllipticCurvePoint R1 = this;
        for (int i = 0; i < k.toString(2).length(); i++)
        {
            if (k.toString(2).charAt(i) == '0')
            {
                R1 = R0.add(R1);
                R0 = R0.add(R0);
            }
            else
            {
                R0 = R0.add(R1);
                R1 = R1.add(R1);
            }
        }
        return R0;
    }
    public boolean equals(EllipticCurvePoint other)
    {
        if (infinity && other.infinity) return true;
        else return x.equals(other.getX()) && y.equals(other.getY()) && parentCurve.equals(other.parentCurve);
    }
    public BigInteger getX()
    {
        return x;
    }
    public BigInteger getY()
    {
        return y;
    }
    public String toString()
    {
        if (infinity)
        {
            return "oo";
        }
        else return "(" + x.toString(16) + "," + y.toString(16) + ")";
    }

    public EllipticCurve getParentCurve() {
        return parentCurve;
    }
}
