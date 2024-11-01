package edu.umd.cs.findbugs.detect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.AbstractIntegrationTest;
import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class FindBadSensitiveExceptionHandling extends AbstractIntegrationTest {

    private class Tuple<E, K> {
        public E val1;
        public K val2;

        public Tuple(E val1, K val2) {
            this.val1 = val1;
            this.val2 = val2;
        }
    }

    private static Map<String, BugInstanceMatcher[]> bugInstances = new HashMap<String, BugInstanceMatcher[]>();
    private Map<String, Boolean[]> bugValues = new HashMap<String, Boolean[]>();

    @BeforeAll
    public static void setup() {

        bugInstances.put("BadFileNotFoundException1", new BugInstanceMatcher[] {
            createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream1", 17) });
        bugInstances.put("BadFileNotFoundException2", new BugInstanceMatcher[] {
            createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream2", 22) });
        bugInstances.put("BadFileNotFoundException3", new BugInstanceMatcher[] {
            createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream3", 31) });
        bugInstances.put("BadSQLException1",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadSQLException", "badSQLE1", 17),
                    createBugInstanceMatcher("BadSQLException", "badSQLE1", 18),
                    createBugInstanceMatcher("BadSQLException", "badSQLE1", 19),
                    createBugInstanceMatcher("BadSQLException", "badSQLE1", 20) });
        bugInstances.put("BadSQLException2",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadSQLException", "badSQLE2", 29) });
        bugInstances.put("BadSQLException3",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadSQLException", "badSQLE3", 38) });
        bugInstances.put("BadBindException1",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadBindException", "badBindException1", 11),
                    createBugInstanceMatcher("BadBindException", "badBindException1",
                            12) });
        bugInstances.put("BadBindException2",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadBindException",
                        "badBindException2", 19) });
        bugInstances.put("BadBindException3",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadBindException",
                        "badBindException3", 33) });
        bugInstances.put("BadBindException4",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadBindException",
                        "badBindException4", 52) });
        bugInstances.put("BadInsufficientResourcesException1",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadInsufficientResourcesException", "badIRE1",
                            11) });
        bugInstances.put("BadInsufficientResourcesException2",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadInsufficientResourcesException", "badIRE2",
                            17) }); // eddig van
                                                                                                                                                                                               // lecsekkolva,
                                                                                                                                                                                               // hogy melyik
                                                                                                                                                                                               // sorra dobja
                                                                                                                                                                                               // a hibát
                                                                                                                                                                                               // (idáig
                                                                                                                                                                                               // mindig a
                                                                                                                                                                                               // kétes
                                                                                                                                                                                               // exceptiont
                                                                                                                                                                                               // dobó fv
                                                                                                                                                                                               // hívása)
        bugInstances.put("BadMissingResourceException1",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadMissingResourceException", "badMRE1", 10),
                    createBugInstanceMatcher("BadMissingResourceException", "badMRE1",
                            11) });
        bugInstances.put("BadMissingResourceException2",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadMissingResourceException",
                        "badMRE2", 16) });
        bugInstances.put("BadMissingResourceException3",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadMissingResourceException",
                        "badMRE3", 25) });
        bugInstances.put("BadJarException1",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadJarException", "badJE1", 12) });
        bugInstances.put("BadJarException2",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadJarException", "badJE2", 16) });
        bugInstances.put("BadJarException12",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE12", 24) });
        bugInstances.put("BadJarException22",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE22", 33) });
        bugInstances.put("BadJarException13",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE13", 42) });
        bugInstances.put("BadJarException23",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE23", 54) });
    }

    @Test
    void testBadExceptionHandling() {
        bugValues.clear();
        String temp = "";
        for (Map.Entry<String, BugInstanceMatcher[]> arr : bugInstances.entrySet()) {
            String name = arr.getKey();
            int length = arr.getValue().length;
            Boolean[] barr = new Boolean[length];
            for (int i = 0; i < length; i++)
                barr[i] = false;
            bugValues.put(name, barr);

        }
        for (Map.Entry<String, BugInstanceMatcher[]> arr : bugInstances.entrySet()) {
            String name = arr.getKey();
            String temp1 = name.substring(0, name.length() - 1);
            if (Character.isDigit(temp1.substring(temp1.length() - 1).toCharArray()[0]))
                temp1 = temp1.substring(0, temp1.length() - 1);
            if (temp != temp1) {
                temp = temp1;
                performAnalysis("exceptionInfo/" + temp + ".class");
            }
            BugCollection bugCollection = getBugCollection();

            for (BugInstance bugInstance : bugCollection) {
                BugInstanceMatcher[] matcherArray = arr.getValue();
                for (int i = 0; i < matcherArray.length; i++) {
                    if (matcherArray[i].matches(bugInstance)) {
                        bugValues.get(arr.getKey())[i] = true;
                        break;
                    }
                }
            }
        }

        /* try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream("C:/Users/Loci/Documents/logs/logt.txt"),
                            StandardCharsets.UTF_8));
            bw.append("mukodik a logolas\n");
            bw.append(bugValues.toString());
        } catch (IOException e) {
            throw new RuntimeException("Fos a fajlbairas");
        } */

        assertTrueAll(bugValues.get("BadFileNotFoundException1"));
        assertTrueAll(bugValues.get("BadSQLException1"));
        assertTrueAll(bugValues.get("BadBindException1"));
        //assertTrueAll(bugValues.get("BadInsufficientResourcesException1"));
        assertTrueAll(bugValues.get("BadMissingResourceException1"));
        assertTrueAll(bugValues.get("BadJarException1"));
    }

    private static BugInstanceMatcher createBugInstanceMatcher(String cls, String method, int line) {
        final BugInstanceMatcher bugInstanceMatcher = new BugInstanceMatcherBuilder()
                .bugType("IEH_INSECURE_EXCEPTION_HANDLING")
                .inClass(cls)
                .inMethod(method)
                .atLine(line)
                .build();
        return bugInstanceMatcher;
    }

    private static void assertTrueAll(Boolean[] arr) {
        for (Boolean bug : arr) {
            Assertions.assertTrue(bug);
        }
    }
}
