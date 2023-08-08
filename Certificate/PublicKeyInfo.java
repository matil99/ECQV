package Certificate;

import CryptoUtilities.EllipticCurvePoint;

public class PublicKeyInfo
{
    private final String algorithm;
    private final EllipticCurvePoint ellipticCurvePoint;
    public PublicKeyInfo(String algorithm, EllipticCurvePoint ellipticCurvePoint)
    {
        this.algorithm = algorithm;
        this.ellipticCurvePoint = ellipticCurvePoint;
    }
    public String getAlgorithm()
    {
        return algorithm;
    }
    public EllipticCurvePoint getEllipticCurvePoint()
    {
        return ellipticCurvePoint;
    }
    public String toString()
    {
        return algorithm + ": " + ellipticCurvePoint;
    }
}
