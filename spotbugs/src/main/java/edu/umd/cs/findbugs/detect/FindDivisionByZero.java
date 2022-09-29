/*
 * SpotBugs - Find Bugs in Java programs
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.detect;

import java.util.Iterator;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.IDIV;
import org.apache.bcel.generic.IREM;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.LDIV;
import org.apache.bcel.generic.LREM;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.LongRangeSet;
import edu.umd.cs.findbugs.ba.ValueRangeDataflow;
import edu.umd.cs.findbugs.ba.vna.ValueNumber;
import edu.umd.cs.findbugs.ba.vna.ValueNumberDataflow;
import edu.umd.cs.findbugs.ba.vna.ValueNumberFrame;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;

public class FindDivisionByZero implements Detector {
    private final BugAccumulator bugAccumulator;
    private final BugReporter bugReporter;

    public FindDivisionByZero(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
        this.bugAccumulator = new BugAccumulator(bugReporter);
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        for (Method method : classContext.getJavaClass().getMethods()) {
            try {
                analyzeMethod(classContext, method);
            } catch (CFGBuilderException e) {
                bugReporter.logError("Error finding locked call sites", e);
                continue;
            } catch (CheckedAnalysisException e) {
                bugReporter.logError("ValueRangeAnalysis failed for " + method, e);
                continue;
            }
        }
    }

    private void analyzeMethod(ClassContext classContext, Method method) throws CFGBuilderException,
            CheckedAnalysisException {
        ValueNumberDataflow vnaDataflow = classContext.getValueNumberDataflow(method);
        ValueRangeDataflow vraDataflow = classContext.getValueRangeDataflow(method);
        CFG cfg = classContext.getCFG(method);
        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();) {
            Location location = i.next();
            InstructionHandle handle = location.getHandle();
            Instruction ins = handle.getInstruction();
            if (!(ins instanceof IDIV) && !(ins instanceof IREM) &&
                    !(ins instanceof LDIV) && !(ins instanceof LREM)) {
                continue;
            }

            ValueNumberFrame frame = vnaDataflow.getFactAtLocation(location);
            ValueNumber divisorNumber = frame.getTopValue();
            if (divisorNumber == null) {
                continue;
            }
            LongRangeSet divisorRangeSet = vraDataflow.getFactAtLocation(location).getRange(divisorNumber);
            if (divisorRangeSet.ne(0).isEmpty()) {
                bugAccumulator.accumulateBug(new BugInstance("DZ_DIVISION_BY_ZERO", NORMAL_PRIORITY)
                        .addClassAndMethod(classContext.getJavaClass(), method)
                        .addSourceLine(classContext, method, location),
                        SourceLineAnnotation.fromVisitedInstruction(classContext, method, handle));
            }
        }
        bugAccumulator.reportAccumulatedBugs();
    }

    @Override
    public void report() {
        // TODO Auto-generated method stub
    }
}
