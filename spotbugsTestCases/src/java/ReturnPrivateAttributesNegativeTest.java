import edu.umd.cs.findbugs.annotations.NoWarning;

import java.util.Date;
import java.util.Hashtable;

public class ReturnPrivateAttributesNegativeTest {
    private Date date;
    private Date[] dateArray;
    private Hashtable<Integer, String> ht = new Hashtable<Integer, String>();

    public ReturnPrivateAttributesNegativeTest() {
        date = new Date();
        dateArray = new Date[20];
        ht.put(1, "123-45-6789");
        for (int i = 0; i < dateArray.length; i++) {
            dateArray[i] = new Date();
        }
    }

    @NoWarning("RPMA")
    public Date getDate() {
        return (Date) date.clone();
    }


    @NoWarning("RPMA")
    public Date[] getDateArray() {
        Date[] dateCopy = new Date[dateArray.length];
        for (int i = 0; i < dateArray.length; i++) {
            dateCopy[i] = (Date) dateArray[i].clone();
        }
        return dateCopy;
    }

    @NoWarning("RPMA")
    public Hashtable<Integer, String> getValues() {
        return (Hashtable<Integer, String>) ht.clone();
    }
}
