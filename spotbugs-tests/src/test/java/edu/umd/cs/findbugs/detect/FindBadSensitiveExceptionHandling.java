package edu.umd.cs.findbugs.detect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import static org.hamcrest.MatcherAssert.assertThat;
import static edu.umd.cs.findbugs.test.CountMatcher.containsExactly;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.AbstractIntegrationTest;
import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class FindBadSensitiveExceptionHandling extends AbstractIntegrationTest {

    private static Map<String, BugInstanceMatcher[]> bugInstances = new HashMap<String, BugInstanceMatcher[]>();
    private Map<String, Boolean[]> bugValues = new HashMap<String, Boolean[]>();

    @BeforeAll
    public static void setup() {

        bugInstances.put("BadFileNotFoundException1", new BugInstanceMatcher[] {
            createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream1", 17), createBugInstanceMatcher("BadFileNotFoundException",
                    "badFileOutputStream1", 40), createBugInstanceMatcher("BadFileNotFoundException", "badRandomAccessFile1", 64) });
        bugInstances.put("BadFileNotFoundException2", new BugInstanceMatcher[] {
            createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream2", 24), createBugInstanceMatcher("BadFileNotFoundException",
                    "badFileOutputStream2", 48), createBugInstanceMatcher("BadFileNotFoundException", "badRandomAccessFile2", 72) });
        bugInstances.put("BadFileNotFoundException3", new BugInstanceMatcher[] {
            createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream3", 31), createBugInstanceMatcher("BadFileNotFoundException",
                    "badFileOutputStream3", 57), createBugInstanceMatcher("BadFileNotFoundException", "badRandomAccessFile3", 81) });
        bugInstances.put("BadSQLException1",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadSQLException", "badSQLE1", 17),
                    createBugInstanceMatcher("BadSQLException", "badSQLE1", 18),
                    createBugInstanceMatcher("BadSQLException", "badSQLE1", 19),
                    createBugInstanceMatcher("BadSQLException", "badSQLE1", 20) });
        bugInstances.put("BadSQLException2",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadSQLException", "badSQLE2", 31) });
        bugInstances.put("BadSQLException3",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadSQLException", "badSQLE3", 40) });
        bugInstances.put("BadBindException1",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadBindException", "badBindException1", 11),
                    createBugInstanceMatcher("BadBindException", "badBindException1",
                            12) });
        bugInstances.put("BadBindException2",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadBindException",
                        "badBindException2", 22) });
        bugInstances.put("BadBindException3",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadBindException",
                        "badBindException3", 36) });
        bugInstances.put("BadBindException4",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadBindException",
                        "badBindException4", 55) });
        bugInstances.put("BadInsufficientResourcesException1",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadInsufficientResourcesException", "badIRE1",
                            11) });
        bugInstances.put("BadInsufficientResourcesException2",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadInsufficientResourcesException", "badIRE2",
                            20) });
        bugInstances.put("BadMissingResourceException1",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadMissingResourceException", "badMRE1", 10),
                    createBugInstanceMatcher("BadMissingResourceException", "badMRE1",
                            11) });
        bugInstances.put("BadMissingResourceException2",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadMissingResourceException",
                        "badMRE2", 19) });
        bugInstances.put("BadMissingResourceException3",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadMissingResourceException",
                        "badMRE3", 28) });
        bugInstances.put("BadJarException1",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadJarException", "badJE1", 12) });
        bugInstances.put("BadJarException2",
                new BugInstanceMatcher[] { createBugInstanceMatcher("BadJarException", "badJE2", 16) });
        bugInstances.put("BadJarException12",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE12", 25) });
        bugInstances.put("BadJarException22",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE22", 34) });
        bugInstances.put("BadJarException13",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE13", 43) });
        bugInstances.put("BadJarException23",
                new BugInstanceMatcher[] {
                    createBugInstanceMatcher("BadJarException", "badJE23", 55) });
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
                performAnalysis("exceptionInfo/" + temp + ".class", "exceptionInfo/ExceptionReporter.class",
                        "exceptionInfo/ExceptionReporterPermission.class", "exceptionInfo/FilteredSensitiveException.class",
                        "exceptionInfo/MyExceptionReporter.class", "exceptionInfo/Reporter.class", "exceptionInfo/SecurityIOException.class",
                        "exceptionInfo/SensitiveException1.class", "exceptionInfo/SensitiveException2.class", "exceptionInfo/MockContext.class");
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

        assertTrueAll(bugValues.get("BadFileNotFoundException1"));
        assertTrueAll(bugValues.get("BadFileNotFoundException2"));
        assertTrueAll(bugValues.get("BadSQLException1"));
        assertTrueAll(bugValues.get("BadSQLException2"));
        assertTrueAll(bugValues.get("BadBindException1"));
        assertTrueAll(bugValues.get("BadBindException2"));
        assertTrueAll(bugValues.get("BadBindException3"));
        assertTrueAll(bugValues.get("BadInsufficientResourcesException1"));
        assertTrueAll(bugValues.get("BadInsufficientResourcesException2"));
        assertTrueAll(bugValues.get("BadMissingResourceException1"));
        assertTrueAll(bugValues.get("BadMissingResourceException2"));
        assertTrueAll(bugValues.get("BadJarException1"));
        assertTrueAll(bugValues.get("BadJarException2"));
        assertTrueAll(bugValues.get("BadJarException12"));
        assertTrueAll(bugValues.get("BadJarException22"));
    }

    @Test
    void testGoodExceptionHandling() {
        performAnalysis("exceptionInfo/GoodMissingResourceException.class", "exceptionInfo/GoodJarException.class",
                "exceptionInfo/GoodInsufficientResourcesException.class", "exceptionInfo/GoodFileNotFoundException.class",
                "exceptionInfo/GoodBindException.class",
                "exceptionInfo/GoodSQLException.class", "exceptionInfo/ExceptionReporter.class",
                "exceptionInfo/ExceptionReporterPermission.class", "exceptionInfo/FilteredSensitiveException.class",
                "exceptionInfo/MyExceptionReporter.class", "exceptionInfo/Reporter.class", "exceptionInfo/SecurityIOException.class",
                "exceptionInfo/SensitiveException1.class", "exceptionInfo/SensitiveException2.class");
        assertNumOfIEHBugs(0);
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

    private void assertNumOfIEHBugs(int num) {
        final BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder()
                .bugType("IEH_INSECURE_EXCEPTION_HANDLING").build();
        assertThat(getBugCollection(), containsExactly(num, bugTypeMatcher));
    }
}
