package edu.umd.cs.findbugs.detect;

import static edu.umd.cs.findbugs.test.CountMatcher.containsExactly;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import edu.umd.cs.findbugs.AbstractIntegrationTest;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;

public class FindDivisionByZeroTest extends AbstractIntegrationTest {
    @Test
    public void testDivisionByZero() {
        performAnalysis("DivisionByZero.class");

        assertNumOfDZBugs(16);

        assertDZBug("divByZeroIntParam", 7);
        assertDZBug("divByZeroLongParam", 14);
        assertDZBug("divByZeroIntParam2", 19);
        assertDZBug("divByZeroLongParam2", 26);
        assertDZBug("divByZeroIntParam3", 38);
        assertDZBug("divByZeroLongParam3", 48);
        assertDZBug("divByZeroIntParam4", 54);
        assertDZBug("divByZeroLongParam4", 63);
        assertDZBug("remByZeroIntParam", 73);
        assertDZBug("remByZeroLongParam", 80);
        assertDZBug("remByZeroIntParam2", 85);
        assertDZBug("remByZeroLongParam2", 92);
        assertDZBug("remByZeroIntParam3", 104);
        assertDZBug("remByZeroLongParam3", 114);
        assertDZBug("remByZeroIntParam4", 120);
        assertDZBug("remByZeroLongParam4", 129);
    }

    private void assertNumOfDZBugs(int num) {
        final BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder()
                .bugType("DZ_DIVISION_BY_ZERO").build();
        assertThat(getBugCollection(), containsExactly(num, bugTypeMatcher));
    }

    private void assertDZBug(String method, int line) {
        final BugInstanceMatcher bugInstanceMatcher = new BugInstanceMatcherBuilder()
                .bugType("DZ_DIVISION_BY_ZERO")
                .inClass("DivisionByZero")
                .inMethod(method)
                .atLine(line)
                .build();
        assertThat(getBugCollection(), hasItem(bugInstanceMatcher));
    }
}
