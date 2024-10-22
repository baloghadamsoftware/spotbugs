package edu.umd.cs.findbugs.detect;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.util.StringMatcher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.JavaClass;

public class FindInsecureExceptionHandling extends OpcodeStackDetector implements Detector {

    private final BugAccumulator bugAccumulator;
    private ClassContext classContext = null;
    private int opCodeIndex = 0;
    private boolean seenInvoke = false;
    private BufferedWriter bw = null;
    private boolean found = false;
    private static int n = 1;
    private String[] SensitiveCalls = { "java.net.ServerSocket.<init>", "java.net.ServerSocket.bind",
            "java.util.jar.JarFile.getInputStream", "java/util/jar/JarFile.<init>", "java.io.FileInputStream.<init>",
            "javax.naming.Context.lookup", "java.util.ResourceBundle.getBundle", "java.util.ResourceBundle.getObject",
            "java.sql.DriverManager.getConnection","java.sql.DriverManager.getDriver","java.sql.DriverManager.registerDriver","java.sql.DriverManager.deregisterDriver" };

    public FindInsecureExceptionHandling(BugReporter bugReporter) {
        this.bugAccumulator = new BugAccumulator(bugReporter);
    }

    @Override
    public void sawOpcode(int seen) {
        if (!found) {
            try {
                try {
                    if (this.getDottedClassName().equals("exceptionInfo.BadBindException")
                            && this.getMethodName().equals("badBindException1")) {
                        bw = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream("C:/Users/Loci/Documents/logs/log0.txt"),
                                        StandardCharsets.UTF_8));
                        bw.append("mukodik a logolas\n");
                        found = true;
                    } /*
                       * else {
                       * bw = new BufferedWriter(
                       * new OutputStreamWriter(
                       * new FileOutputStream("C:/Users/Loci/Documents/logs/log" + n + methodN +
                       * ".txt"),
                       * StandardCharsets.UTF_8));
                       * bw.append("mukodik a logolas\n");
                       * }
                       */
                } catch (IOException e) {
                    throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                }
                n++;
                if (bw != null)
                    bw.append(this.getDottedClassName() + " "
                            + this.getMethodName() + "\n");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }
        }
        if (seen == Const.INVOKESPECIAL || seen == Const.INVOKEVIRTUAL) {

            try {
                if (bw != null)
                    bw.append("InvokeSpecial seen\n");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }
            seenInvoke = true;
        } else {
            seenInvoke = false;
        }
        opCodeIndex++;
    }

    @Override
    public void afterOpcode(int seen) {
        super.afterOpcode(seen);
        if (!seenInvoke)
            return;
        try {
            if (bw != null)
                bw.append("afteropcodeInvokeSpecialSeenIsTrue\n");
        } catch (IOException e) {
            throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
        }
        if (seen == Const.INVOKESPECIAL || seen == Const.INVOKEVIRTUAL) {

            CodeException[] exT = this.getCode().getExceptionTable();

            if (stack.getStackDepth() <= 0)
                return;
            OpcodeStack.Item item = stack.getStackItem(0);
            int regIndex = item.getRegisterNumber();
            XMethod method = item.getReturnValueOf();
            String sourceSig = method.getClassName() + "." + method.getName(); // StringBuilderre le lehetne
                                                                               // cserélni

            try {
                if (bw != null)
                    bw.append("sig: " + sourceSig + "\n");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }

            boolean handled = false;
            if (Arrays.stream(SensitiveCalls).anyMatch(sourceSig::equals)) {
                try {
                    if (bw != null)
                        bw.append("right signature found\n");
                } catch (IOException e) {
                    throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                }
                for (CodeException ex : exT) {
                    if (ex.getStartPC() <= regIndex && regIndex < ex.getEndPC()) {
                        handled = true;
                        break;
                    }
                }
                try {
                    if (bw != null)
                        bw.append("exception handled: " + handled + "\n");
                } catch (IOException e) {
                    throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                }
                if (!handled) {
                    BugInstance bug = new BugInstance(this, "IEH_INSECURE_EXCEPTION_HANDLING", LOW_PRIORITY)
                            .addClassAndMethod(this).addCalledMethod(method).addString("has no handler");
                    bugAccumulator.accumulateBug(bug, this);
                    try {
                        if (bw != null)
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
            if (bw != null)
                bw.close();
            found = false;
        } catch (IOException e) {
        }
    }

}
