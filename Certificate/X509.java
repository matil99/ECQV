package Certificate;

import CryptoUtilities.EllipticCurvePoint;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/*Uproszczona wersja pierwsza certifkatu*/
public class X509
{
    private final int version = 0; /*Wersja 1*/
    private final String serialNumber; /*Numer seryjny certyfiaktu*/
    private final String algorithmIdentifier; /*Algorytm użyty do generacji certyfikatu*/
    private final String issuer; /*Nazwa wystawcy*/
    private final Validity validity; /*Okres ważności certifikatu*/
    private final String subject; /*Nazwa odbiorcy*/
    private final PublicKeyInfo subjectPublicKeyInfo; /*Dane potrzebne do odzyskania klucza publicznego*/

    public X509(String serialNumber, String algorithmIdentifier, String issuer, Validity validity, String subject, PublicKeyInfo subjectPublicKeyInfo)
    {
        this.serialNumber = serialNumber;
        this.algorithmIdentifier = algorithmIdentifier;
        this.issuer = issuer;
        this.validity = validity;
        this.subject = subject;
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
    }
    public void save() throws Exception
    {
        String pathToKeys = ".\\resources\\Certificates\\";
        SimpleDateFormat formatter= new SimpleDateFormat("MMM dd HH:mm:ss yyyy z");
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathToKeys + serialNumber+ ".txt"), StandardCharsets.UTF_8)))
        {
            writer.write("Certificate: \n");
            writer.write("\tData: \n");
            writer.write("\t\tVersion: " + (version + 1) + "\n");
            writer.write("\t\tSerial Number: \n");
            writer.write("\t\t\t");
            for (int i = 0; i < serialNumber.length()-1; i=i+2)
            {
                if (i != serialNumber.length()-2)
                    writer.write(serialNumber.substring(i, i+2) + ":");
                else
                    writer.write(serialNumber.substring(i, i+2));
            }
            writer.write("\n");
            writer.write("\t\tAlgorithm: " + algorithmIdentifier + "\n");
            writer.write("\t\tIssuer: " + issuer + "\n");
            writer.write("\t\tValidity: \n");
            writer.write("\t\t\tNot before: " + formatter.format(validity.getNotBefore()) + "\n");
            writer.write("\t\t\tNot after: " + formatter.format(validity.getNotAfter()) + "\n");
            writer.write("\t\tSubject: " + subject + "\n");
            writer.write("\t\tSubject Public Key Info: \n");
            writer.write("\t\t\tPublic Key Algorithm: " + subjectPublicKeyInfo.getAlgorithm() + "\n");
            writer.write("\t\t\tPoint: \n");
            String x = subjectPublicKeyInfo.getEllipticCurvePoint().getX().toString(16);
            writer.write("\t\t\t\t");
            for (int i = 0; i < x.length()-1; i=i+2)
            {
                if (i != x.length()-2)
                    writer.write(x.substring(i, i+2) + ":");
                else
                    writer.write(x.substring(i, i+2) + "\n");
            }
            String y = subjectPublicKeyInfo.getEllipticCurvePoint().getY().toString(16);
            writer.write("\t\t\t\t");
            for (int i = 0; i < y.length()-1; i=i+2)
            {
                if (i != x.length()-2)
                    writer.write(y.substring(i, i+2) + ":");
                else
                    writer.write(y.substring(i, i+2) + "\n");
            }
        }
    }



    public int getVersion()
    {
        return version;
    }
    public String getSerialNumber()
    {
        return serialNumber;
    }
    public String getAlgorithmIdentifier()
    {
        return algorithmIdentifier;
    }
    public String getIssuer()
    {
        return issuer;
    }
    public Validity getValidity()
    {
        return validity;
    }
    public String getSubject()
    {
        return subject;
    }
    public Date getNotBefore()
    {
        return validity.getNotBefore();
    }
    public Date getNotAfter()
    {
        return validity.getNotAfter();
    }
    public PublicKeyInfo getSubjectPublicKeyInfo()
    {
        return subjectPublicKeyInfo;
    }
    public String getAlgorithm()
    {
        return subjectPublicKeyInfo.getAlgorithm();
    }
    public EllipticCurvePoint getEllipticCurvePoint()
    {
        return subjectPublicKeyInfo.getEllipticCurvePoint();
    }

}
