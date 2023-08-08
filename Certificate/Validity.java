package Certificate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Validity
{
    private final Date notBefore;
    private final Date notAfter;
    public Validity(Date notBefore, Date notAfter)
    {
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }
    public Date getNotBefore()
    {
        return notBefore;
    }
    public Date getNotAfter()
    {
        return notAfter;
    }
    public String toString()
    {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss z");
        return formatter.format(notBefore) + " - " + formatter.format(notAfter);
    }
}
