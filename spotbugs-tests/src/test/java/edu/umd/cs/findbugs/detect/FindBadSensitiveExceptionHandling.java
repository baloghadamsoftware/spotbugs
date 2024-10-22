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

    private static Map<String, BugInstanceMatcher> bugInstances = new HashMap<String, BugInstanceMatcher>();
    private Map<String, Boolean> bugValues = new HashMap<String, Boolean>();

    @BeforeAll
    public static void setup() {
        bugInstances.put("BadFileNotFoundException1", createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream1", 17));
        bugInstances.put("BadFileNotFoundException2", createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream2", 25));
        bugInstances.put("BadFileNotFoundException3", createBugInstanceMatcher("BadFileNotFoundException", "badFileInputStream3", 34));
        bugInstances.put("BadSQLException1", createBugInstanceMatcher("BadSQLException", "badSQLE1", 17));
        bugInstances.put("BadSQLException2", createBugInstanceMatcher("BadSQLException", "badSQLE2", 28));
        bugInstances.put("BadSQLException3", createBugInstanceMatcher("BadSQLException", "badSQLE3", 37));
        bugInstances.put("BadBindException1", createBugInstanceMatcher("BadBindException", "badBindException1", 11));
        bugInstances.put("BadBindException2", createBugInstanceMatcher("BadBindException", "badBindException2", 23));
        bugInstances.put("BadBindException3", createBugInstanceMatcher("BadBindException", "badBindException3", 37));
        bugInstances.put("BadBindException4", createBugInstanceMatcher("BadBindException", "badBindException4", 56));
        bugInstances.put("BadInsufficientResourcesException1", createBugInstanceMatcher("BadInsufficientResourcesException", "badIRE1", 11));
        bugInstances.put("BadInsufficientResourcesException2", createBugInstanceMatcher("BadInsufficientResourcesException", "badIRE2", 20));
        bugInstances.put("BadMissingResourceException1", createBugInstanceMatcher("BadMissingResourceException", "badMRE1", 10));
        bugInstances.put("BadMissingResourceException2", createBugInstanceMatcher("BadMissingResourceException", "badMRE2", 18));
        bugInstances.put("BadMissingResourceException3", createBugInstanceMatcher("BadMissingResourceException", "badMRE3", 27));
        bugInstances.put("BadJarException1", createBugInstanceMatcher("BadJarException", "badJE1", 12));
        bugInstances.put("BadJarException2", createBugInstanceMatcher("BadJarException", "badJE2", 16));
        bugInstances.put("BadJarException12", createBugInstanceMatcher("BadJarException", "badJE12", 24));
        bugInstances.put("BadJarException22", createBugInstanceMatcher("BadJarException", "badJE22", 33));
        bugInstances.put("BadJarException13", createBugInstanceMatcher("BadJarException", "badJE13", 42));
        bugInstances.put("BadJarException23", createBugInstanceMatcher("BadJarException", "badJE23", 54));
    }

    @Test
    void testBadExceptionHandling() {
        bugValues.clear();
        String temp = "";
        for (String name : bugInstances.keySet()) {
            bugValues.put(name, false);
            String temp1 = name.substring(0, name.length() - 1);
            if (Character.isDigit(temp1.substring(temp1.length() - 1).toCharArray()[0]))
                temp1 = temp1.substring(0, temp1.length() - 1);
            if (temp != temp1) {
                temp = temp1;
                performAnalysis("exceptionInfo/" + temp + ".class");
            }
        }
        BugCollection bugCollection = getBugCollection();

        for (BugInstance bugInstance : bugCollection) {
            for (Map.Entry<String, BugInstanceMatcher> matcher : bugInstances.entrySet()) {
                if (matcher.getValue().matches(bugInstance)) {
                    bugValues.replace(matcher.getKey(), true);
                    break;
                }
            }
        }

        /* if (bugValues.get("BadBindException1")) {
            for (BugInstance bugInstance : bugCollection) {
                if (bugInstance.getType() == "IEH_INSECURE_EXCEPTION_HANDLING") {
                    System.out.println(bugInstance);
                }
            }
        } */
        Assertions.assertTrue(bugValues.get("BadBindException1"));
    }

    private static BugInstanceMatcher createBugInstanceMatcher(String cls, String method, int line) {
        final BugInstanceMatcher bugInstanceMatcher = new BugInstanceMatcherBuilder()
                .bugType("IEH_INSECURE_EXCEPTION_HANDLING")
                .inClass(cls)
                .inMethod(method)
                //                .atLine(line)
                .build();
        return bugInstanceMatcher;
    }
}
