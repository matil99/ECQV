package Certificate;

import CryptoUtilities.EllipticCurve;
import CryptoUtilities.EllipticCurvePoint;
import CryptoUtilities.HashFunction;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class X509Factory
{
    private int createdCertificates;
    private final String issuer;
    public X509Factory(String issuer)
    {
        this.createdCertificates = 0;
        this.issuer = issuer;
    }
    public X509Factory()
    {
        this.createdCertificates = 0;
        this.issuer = null;
    }
    public X509 create(String hashName, long duration, String subject, String algorithm,  EllipticCurvePoint ellipticCurvePoint) throws Exception
    {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss" + createdCertificates);
        Date creationDate = new Date(System.currentTimeMillis());
        Date expireDate = new Date(System.currentTimeMillis() + (duration * 3600 * 24 * 366 * 1000));
        String serialNumber = HashFunction.hash("SHA-256", formatter.format(creationDate));
        Validity validity = new Validity(creationDate, expireDate);
        PublicKeyInfo publicKeyInfo = new PublicKeyInfo(algorithm, ellipticCurvePoint);
        createdCertificates = createdCertificates + 1;
        return new X509(serialNumber,"ECMQ/" + hashName + "/" + ellipticCurvePoint.getParentCurve().getName(), issuer, validity, subject, publicKeyInfo);
    }
    public String encode(X509 x509)
    {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss z");
        String version = String.valueOf(x509.getVersion());
        String serialNumber = x509.getSerialNumber();
        String algorithmIdentifier = x509.getAlgorithmIdentifier();
        String issuer = x509.getIssuer();
        String notBefore = formatter.format(x509.getNotBefore());
        String notAfter = formatter.format(x509.getNotAfter());
        String subject = x509.getSubject();
        String algorithm = x509.getAlgorithm();
        String x = String.valueOf(x509.getEllipticCurvePoint().getX());
        String y = String.valueOf(x509.getEllipticCurvePoint().getY());
        return version + "::" + serialNumber + "::" + algorithmIdentifier + "::" + issuer + "::" + notBefore + "::" + notAfter + "::" + subject + "::" + algorithm + "::" + x + "::" + y;
    }
    public X509 decode(String input, EllipticCurve ellipticCurve) throws Exception
    {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss z");
        String[] cert = input.split("::");
        if(Integer.parseInt(cert[0]) != 0) throw new Exception("Zła wersja certyfikatu");
        String serialNumber = cert[1];
        String algorithmIdentifier = cert[2];
        String issuer = cert[3];
        Date notBefore = formatter.parse(cert[4]);
        Date notAfter= formatter.parse(cert[5]);
        Validity validity = new Validity(notBefore, notAfter);
        String subject = cert[6];
        String algorithm = cert[7];
        String x = cert[8];
        String y = cert[9];
        EllipticCurvePoint ellipticCurvePoint = new EllipticCurvePoint(new BigInteger(x), new BigInteger(y), ellipticCurve);
        PublicKeyInfo subjectPublicKeyInfo = new PublicKeyInfo(algorithm, ellipticCurvePoint);
        return new X509(serialNumber, algorithmIdentifier, issuer, validity, subject, subjectPublicKeyInfo);
    }
}
