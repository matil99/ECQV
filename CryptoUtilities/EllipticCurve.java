package CryptoUtilities;

import java.math.BigInteger;

public class EllipticCurve
{
    private final String name;
    private final BigInteger p;
    private final BigInteger a;
    private final BigInteger b;
    private BigInteger n;
    private BigInteger h;
    private EllipticCurvePoint G;
    public EllipticCurve(String name) throws Exception
    {
        this.name = name;
        switch (name)
        {
            case "example" : /* 0 */
            {
                this.p = new BigInteger("11", 10);
                this.a = new BigInteger("6", 10);
                this.b = new BigInteger("7", 10);
            }break;
            case "secp256r1": /* 1 */
            {
                this.p = new BigInteger("ffffffff00000001000000000000000000000000ffffffffffffffffffffffff", 16);
                this.a = new BigInteger("ffffffff00000001000000000000000000000000fffffffffffffffffffffffc", 16);
                this.b = new BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16);
                this.G = new EllipticCurvePoint(new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16), new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16), this);
                this.n =  new BigInteger("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551", 16);
                this.h = new BigInteger("1", 16);
            }break;
            case "secp256k1" : /* 2 */
            {
                this.p = new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f", 16);
                this.a = new BigInteger("0", 16);
                this.b = new BigInteger("7", 16);
                this.G = new EllipticCurvePoint(new BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798",16), new BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8", 16), this);
                this.n =  new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);
                this.h = new BigInteger("1", 16);
            } break;
            /*Do zrobienia krzywa 384 bitowa i 521 bitowa*/
            case "secp384r1" : /* 3 */
            {
                this.p = new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffeffffffff0000000000000000ffffffff", 16);
                this.a = new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffeffffffff0000000000000000fffffffc", 16);
                this.b = new BigInteger("b3312fa7e23ee7e4988e056be3f82d19181d9c6efe8141120314088f5013875ac656398d8a2ed19d2a85c8edd3ec2aef", 16);
                this.G = new EllipticCurvePoint(new BigInteger("aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7",16), new BigInteger("3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f", 16), this);
                this.n =  new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffc7634d81f4372ddf581a0db248b0a77aecec196accc52973", 16);
                this.h = new BigInteger("01", 16);
            } break;
            case "secp521r1" : /* 4 */
            {
                this.p = new BigInteger("01ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
                this.a = new BigInteger("01fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc", 16);
                this.b = new BigInteger("0051953eb9618e1c9a1f929a21a0b68540eea2da725b99b315f3b8b489918ef109e156193951ec7e937b1652c0bd3bb1bf073573df883d2c34f1ef451fd46b503f00", 16);
                this.G = new EllipticCurvePoint(new BigInteger("00c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66",16), new BigInteger("011839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", 16), this);
                this.n =  new BigInteger("01fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffa51868783bf2f966b7fcc0148f709a5d03bb5c9b8899c47aebb6fb71e91386409", 16);
                this.h = new BigInteger("01", 16);
            } break;
            default:
            {
                throw new Exception("Such curve does not exist");
            }
        }
    }
    public BigInteger getP()
    {
        return this.p;
    }
    public EllipticCurvePoint getG()
    {
        return G;
    }
    public BigInteger getA()
    {
        return a;
    }
    public BigInteger getB()
    {
        return b;
    }
    public BigInteger getN()
    {
        return n;
    }
    public String getName()
    {
        return name;
    }
    public boolean equals(EllipticCurve other)
    {
        return a.equals(other.getA()) && b.equals(other.getB()) && p.equals(other.getP());
    }
    public String toString()
    {
        return "E: y^2 = x^3 + " + a.toString(16) + " x + " + b.toString(16);
    }
}
