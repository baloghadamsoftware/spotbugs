package exceptionInfo;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class BadConcurrentModificationException {
    
    public void badCME1() throws ConcurrentModificationException{
        List<Integer> integers = new ArrayList<Integer>();
            integers.add(1);
            integers.add(2);
            integers.add(3);

            for (Integer integer : integers) {
                integers.remove(1);
            }
            //good ugyanez csak try catchel
    }
}
