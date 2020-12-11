import edu.umd.cs.findbugs.annotations.ExpectWarning;

import java.util.Date;
import java.util.Hashtable;

public class ReturnPrivateAttributesTest {
    private Date date;
    private Date[] dateArray;
    private Hashtable<Integer, String> ht = new Hashtable<Integer, String>();

    public ReturnPrivateAttributesTest() {
        date = new Date();
        dateArray = new Date[20];
        ht.put(1, "123-45-6789");
        for (int i = 0; i < dateArray.length; i++) {
            dateArray[i] = new Date();
        }
    }

    @ExpectWarning("RPMA")
    public Date getDate() {
        return date;
    }

    @ExpectWarning("RPMA")
    public Date getDate2() {
        Date d = date;
        return d;
    }

    @ExpectWarning("RPMA")
    public Date[] getDateArray() {
        return dateArray;
    }

    @ExpectWarning("RPMA")
    public Date[] getDateArray2() {
        return dateArray.clone();
    }

    @ExpectWarning("RPMA")
    public Hashtable<Integer, String> getValues() {
        return ht;
    }
}
