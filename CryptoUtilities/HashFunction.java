package CryptoUtilities;

import java.math.BigInteger;
import java.security.MessageDigest;

public class HashFunction
{
    public static String hash(int hash, String input) throws Exception
    {
        String instance;
        switch(hash)
        {
            case 1 : instance = "SHA-256"; break;
            case 2: instance = "SHA-384"; break;
            case 3 : instance = "SHA-512"; break;
            case 4 : instance = "SHA3-256"; break;
            case 5 : instance = "SHA3-384"; break;
            case 6 : instance = "SHA3-512"; break;
            default:
            {
                throw new Exception("Such algorithm does not exist");
            }
        }
        MessageDigest function = MessageDigest.getInstance(instance);
        byte[] digest = function.digest(input.getBytes());
        return new BigInteger(1, digest).toString(16);
    }
    public static String hash(String hash, String input) throws Exception
    {
        MessageDigest function = MessageDigest.getInstance(hash);
        byte[] digest = function.digest(input.getBytes());
        return new BigInteger(1, digest).toString(16);
    }
    public static String hash(int hash, byte[] input) throws Exception
    {
        String instance;
        switch(hash)
        {
            case 1 : instance = "SHA-256"; break;
            case 2: instance = "SHA-384"; break;
            case 3 : instance = "SHA-512"; break;
            case 4 : instance = "SHA3-256"; break;
            case 5 : instance = "SHA3-384"; break;
            case 6 : instance = "SHA3-512"; break;
            default:
            {
                throw new Exception("Such algorithm does not exist");
            }
        }
        MessageDigest function = MessageDigest.getInstance(instance);
        byte[] digest = function.digest(input);
        return new BigInteger(1, digest).toString(16);
    }
}
