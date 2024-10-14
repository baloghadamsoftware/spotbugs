package edu.umd.cs.findbugs.detect;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.JavaClass;

public class FindInsecureExceptionHandling extends OpcodeStackDetector implements Detector {

    private final BugAccumulator bugAccumulator;
    private ClassContext classContext = null;
    private int opCodeIndex = 0;
    private boolean seenInvSpec = false;
    private BufferedWriter bw;
    private boolean firsttime = true;
    private static int n = 1;

    public FindInsecureExceptionHandling(BugReporter bugReporter) {
        this.bugAccumulator = new BugAccumulator(bugReporter);
    }

    @Override
    public void sawOpcode(int seen) {
        if (firsttime) {
            try {
                try {
                    if (this.getClassContext().getJavaClass().getClassName() == "exceptionInfo.BadBindException") {
                        bw = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream("C:/Users/Loci/Documents/logs/logx.txt"),
                                        StandardCharsets.UTF_8));
                        bw.append("mukodik a logolas\n");
                    } else {
                        bw = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream("C:/Users/Loci/Documents/logs/log" + n + ".txt"),
                                        StandardCharsets.UTF_8));
                        bw.append("mukodik a logolas\n");
                    }
                } catch (IOException e) {
                    throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                }
                n++;
                bw.append(this.getClassContext().getJavaClass().getClassName() + "\n");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }
            firsttime = false;
            return;
        }
        if (seen == Const.INVOKESPECIAL) {

            try {
                bw.append("InvokeSpecial seen ");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }
            seenInvSpec = true;
            /*
             * OpcodeStack.Item item=stack.getStackItem(2);
             * if(item==null) return;
             * Object cls=item.getConstant();
             * if(cls instanceof java.net.Socket){
             * for(CodeException ex : exT){
             * if(ex.getCatchType())
             * }
             * }
             */
        } else {
            seenInvSpec = false;
        }
        opCodeIndex++;
    }

    @Override
    public void afterOpcode(int seen) {
        super.afterOpcode(seen);
        if (!seenInvSpec)
            return;
        try {
            bw.append("afteropcodeInvokeSpecialSeen ");
        } catch (IOException e) {
            throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
        }
        if (seen == Const.INVOKESPECIAL) {

            CodeException[] exT = this.getCode().getExceptionTable();

            if (stack.getStackDepth() <= 0)
                return;
            OpcodeStack.Item item = stack.getStackItem(0);
            int regIndex = item.getRegisterNumber();
            XMethod method = item.getReturnValueOf();
            String sig = method.getSignature();

            boolean handled = false;
            if (sig == "java/net/ServerSocket.\"<init>\":(I)V"
                    || sig == "java/net/ServerSocket.bind:(Ljava/net/SocketAddress;)V") {
                for (CodeException ex : exT) {
                    if (ex.getStartPC() <= regIndex && regIndex < ex.getEndPC()) {
                        handled = true;
                        break;
                    }
                }
                if (!handled) {
                    BugInstance bug = new BugInstance(this, "IEH_INSECURE_EXCEPTION_HANDLING", LOW_PRIORITY)
                            .addClassAndMethod(this).addCalledMethod(method).addString("has no handler");
                    bugAccumulator.accumulateBug(bug, this);
                    try {
                        bw.append("bugReported: " + bug.toString() + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void visitAfter(JavaClass obj) {
        bugAccumulator.reportAccumulatedBugs();
        try {
            bw.close();
            firsttime = true;
        } catch (IOException e) {
        }
    }

}
