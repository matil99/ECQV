package Users;

import Certificate.X509;
import Certificate.X509Factory;
import CryptoUtilities.EllipticCurve;
import CryptoUtilities.EllipticCurvePoint;
import CryptoUtilities.HashFunction;

import java.io.*;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Client
{
    private final String clientIdentifier;
    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;
    private EllipticCurve ellipticCurve;
    private final Random rng;
    private final X509Factory x509Factory;
    private BigInteger k, clientPrivateKey;
    private EllipticCurvePoint serverPublicKey;
    public Client(String identifier, boolean condition)
    {
        this.clientIdentifier = identifier;
        this.x509Factory = new X509Factory();
        log("Utworzono fabrykę certyfikatów", condition);
        this.rng = new Random();
        log("Zainicjowano generator losowy", condition);
    }
    public void setup(boolean condition) throws Exception
    {
        String ip = Inet4Address.getLocalHost().getHostAddress();
        this.clientSocket = new Socket(ip, 6666);
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        log("Połączono z serwerem", condition);
        String[] msg = input.readLine().split(";");
        if (!msg[0].equals("0")) throw new Exception("Typ wiadomości inny niż oczekiwano. Połączenie zerwane");
        this.ellipticCurve = new EllipticCurve(msg[1]);
        log("Zainicjowano krzywą: " + ellipticCurve, condition);
        this.serverPublicKey = new EllipticCurvePoint(new BigInteger(msg[2], 10), new BigInteger(msg[3], 10), ellipticCurve);
        log("Otrzymano klucz publiczny serwera: " + serverPublicKey, condition);
    }
    public void ECQV_certRequest(boolean condition)
    {
        this.k = new BigInteger(ellipticCurve.getN().bitCount(), rng).mod(ellipticCurve.getN());
        log("Wylosowano wartość k: " + k.toString(16), condition);
        EllipticCurvePoint R = ellipticCurve.getG().mult(k);
        log("Wyznaczony punkt R: " + R, condition);
        output.println("1;" + clientIdentifier + ";" + R.getX() + ";" + R.getY());
        log("Wysłano żądanie certyfikatu: (" + clientIdentifier + "; " + R + ")", condition);
    }
    public void ECQV_certPublicKeyExtraction(boolean condition) throws Exception
    {
        String[] msg = input.readLine().split(";");
        if (!msg[0].equals("2")) throw new Exception("Typ wiadomości inny niż oczekiwano. Połączenie zerwane");
        log("Odebrano wygenerowany certyfikat", condition);
        BigInteger r = new BigInteger(msg[1]);
        log("Otrzymana wartość r: " + r.toString(16), condition);
        X509 certificate = x509Factory.decode(msg[2], ellipticCurve);
        log("Zdekodowano otrzymany certifkat", condition);
        BigInteger e = new BigInteger(HashFunction.hash(certificate.getAlgorithmIdentifier().split("/")[1], x509Factory.encode(certificate)),16);
        log("Wyznaczona wartość e: " + e.toString(16), condition);
        EllipticCurvePoint clientPublicKey = certificate.getEllipticCurvePoint().mult(e).add(serverPublicKey);
        log("Wyznaczony klucz publiczny: " + clientPublicKey, condition);
        if (ECQV_certReception(this.k, r, e, clientPublicKey, condition))
        {
            log("Otrzymano poprawny klucz publiczny", condition);
            certificate.save();
            log("Certyfikat pomyślnie zapisany do pliku", condition);
            String pathToKeys = ".\\resources\\Keys\\";
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathToKeys + clientIdentifier + ".txt"), StandardCharsets.UTF_8)))
            {
                writer.write(ellipticCurve.getName() + "\n");
                writer.write(clientPrivateKey.toString(16) + "\n");
                writer.write(clientPublicKey.getX().toString(16) + ";" + serverPublicKey.getY().toString(16));
            }
            log("Para kluczy pomyślnie zapisana do pliku", condition);
        }
    }
    public boolean ECQV_certReception(BigInteger k, BigInteger r, BigInteger e, EllipticCurvePoint Q, boolean condition)
    {
        this.clientPrivateKey = (r.add(e.multiply(k))).mod(ellipticCurve.getN());
        log("Wyznaczony klucz prywatny: " + clientPrivateKey.toString(16), condition);
        return ellipticCurve.getG().mult(clientPrivateKey).equals(Q);
    }
    public void stop(boolean condition) throws Exception
    {
        input.close();
        output.close();
        clientSocket.close();
        log("Połączenie zakończone", condition);
    }
    public void log(String msg, boolean condition)
    {
        if (condition)
        {
            System.out.println(clientIdentifier + ": " + msg + ".");
        }
    }
}
