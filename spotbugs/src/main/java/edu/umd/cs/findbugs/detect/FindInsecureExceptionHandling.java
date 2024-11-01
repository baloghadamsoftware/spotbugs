package edu.umd.cs.findbugs.detect;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;


import org.apache.bcel.Const;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;

public class FindInsecureExceptionHandling extends OpcodeStackDetector implements Detector {

    private final BugAccumulator bugAccumulator;
    private ArrayList<OpcodeStack.Item> cpStack = null;
    private OpcodeStack.Item lastStackItem = null;
    private boolean seenInvoke = false;
    private BufferedWriter bw = null;
    private boolean found = false;
    private CodeException[] exT = null;

    private String[] SensitiveCalls = { "java.net.ServerSocket.<init>", "java.net.ServerSocket.bind",
        "java.util.jar.JarFile.getInputStream", "java.util.jar.JarFile.<init>", "java.io.FileInputStream.<init>",
        "javax.naming.Context.lookup", "java.util.ResourceBundle.getBundle", "java.util.ResourceBundle.getObject",
        "java.sql.DriverManager.getConnection", "java.sql.DriverManager.getDriver",
        "java.sql.DriverManager.registerDriver",
        "java.sql.DriverManager.deregisterDriver" };

    private HashMap<String, String> exceptions = new HashMap<String, String>();

    public FindInsecureExceptionHandling(BugReporter bugReporter) {
        this.bugAccumulator = new BugAccumulator(bugReporter);

        exceptions.put("java.net.ServerSocket.<init>", "java/net/BindException");
        exceptions.put("java.net.ServerSocket.bind", "java/net/BindException");
        exceptions.put("java.util.jar.JarFile.getInputStream", "java/util/jar/JarException");
        exceptions.put("java.util.jar.JarFile.<init>", "java/util/jar/JarException");
        exceptions.put("java.io.FileInputStream.<init>", "java/io/FileNotFoundException");
        exceptions.put("javax.naming.Context.lookup", "javax/naming/InsufficientResourcesException");
        exceptions.put("java.util.ResourceBundle.getBundle", "java/util/MissingResourceException");
        exceptions.put("java.util.ResourceBundle.getObject", "java/util/MissingResourceException");
        exceptions.put("java.sql.DriverManager.getConnection", "java/sql/SQLException");
        exceptions.put("java.sql.DriverManager.getDriver", "java/sql/SQLException");
        exceptions.put("java.sql.DriverManager.registerDriver", "java/sql/SQLException");
        exceptions.put("java.sql.DriverManager.deregisterDriver", "java/sql/SQLException");
    }

    @Override
    public void sawOpcode(int seen) {
        if (!found) {
            try {
                try {
                    if (this.getDottedClassName().equals("exceptionInfo.BadSQLException")
                            && this.getMethodName().equals("badSQLE1")) {
                        bw = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream("C:/logs/log0.txt"),
                                        StandardCharsets.UTF_8));
                        bw.append("mukodik a logolas\n");
                        found = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                }
                if (bw != null)
                    bw.append(this.getDottedClassName() + " "
                            + this.getMethodName() + "\n");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }
        }
        if (seen == Const.INVOKESPECIAL || seen == Const.INVOKEVIRTUAL || seen == Const.INVOKESTATIC) {

            try {
                if (bw != null)
                    bw.append("InvokeSpecial seen\n");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }
            // cpStack = stackToArray(stack);
            seenInvoke = true;
        } else {
            seenInvoke = false;
        }
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
        if (seen == Const.INVOKESPECIAL || seen == Const.INVOKEVIRTUAL || seen == Const.INVOKESTATIC) {

            exT = this.getCode().getExceptionTable();

            String className = this.getDottedClassConstantOperand();
            String methodName = this.getNameConstantOperand();
            XMethod method = this.getXMethodOperand();

            String sourceSig = className + "." + methodName; // StringBuilderre le lehetne
                                                             // cserélni

            try {
                if (bw != null)
                    bw.append("sig: " + sourceSig + " " + "\n");
            } catch (IOException e) {
                throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
            }

            Optional<String> possibleSensitiveCall = Arrays.stream(SensitiveCalls)
                    .filter(call -> sourceSig.equals(call))
                    .findAny();

            if (possibleSensitiveCall.isPresent()) {
                String call = possibleSensitiveCall.get();
                try {
                    if (bw != null)
                        bw.append("right signature found\n");
                } catch (IOException e) {
                    throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                }

                if (!isHandled(call)) {
                    BugInstance bug = new BugInstance(this, "IEH_INSECURE_EXCEPTION_HANDLING", LOW_PRIORITY)
                            .addClassAndMethod(this).addCalledMethod(method).addString("has no handler");
                    bugAccumulator.accumulateBug(bug, this);
                    try {
                        if (bw != null)
                            bw.append("bugReported: " + bug.toString() + "line: "
                                    + SourceLineAnnotation.fromVisitedInstruction(this).toString()
                                    + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                    }
                } else {

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

    private boolean isHandled(String call) {
        int regIndex = this.getPC();
        boolean handled = false;
        ConstantPool cpool = this.getCode().getConstantPool();
        for (CodeException ex : exT) {
            if (ex.getStartPC() <= regIndex && regIndex < ex.getEndPC()) {
                handled = true;
                int index = ex.getCatchType();
                if (index != 0) {
                    if (!((ConstantClass) (cpool.getConstant(index))).getBytes(cpool).equals(exceptions.get(call))) {
                        handled = false;
                    }
                } else {
                    break;
                }
            }
        }
        try {
            if (bw != null)
                bw.append("exception handled: " + handled + "\n");
        } catch (IOException e) {
            throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
        }
        return handled;
    }

    private void doesItRethrow(String call) {
        Code code = this.getCode();
        int regIndex = this.getPC();
        String handledException = exceptions.get(call);

        ArrayList<CodeException> exceptions = new ArrayList<CodeException>();
        for (CodeException ex : exT) {
            if (ex.getStartPC() <= regIndex && regIndex < ex.getEndPC()) {
                exceptions.add(ex);
            }
        }
        exceptions.sort((ex1, ex2) -> {
            return ex1.getHandlerPC() - ex2.getHandlerPC();
        });
        RethrowFinder rw = new RethrowFinder(bugAccumulator);

        ConstantPool cpool = getConstantPool();

        for (int i = 1; i <= exceptions.size(); i++) {
            Code exCode = new Code(code);
            int index = exceptions.get(i - 1).getCatchType();
            if (((ConstantClass) (cpool.getConstant(index))).getBytes(cpool).equals(handledException)) {
                exCode.setCode(trimByteCode(exceptions.get(index).getHandlerPC(), exceptions.get(index + 1).getHandlerPC(), code.getCode()));
                rw.visit(exCode);
            }
        }
    }

    private byte[] trimByteCode(int startPC, int endPC, byte[] code) {
        byte[] res = new byte[endPC - startPC];
        for (int i = startPC; i < endPC; i++) {
            res[i] = code[i];
        }
        return res;
    }

    private class RethrowFinder extends OpcodeStackDetector {

        private boolean first = true;
        private int varIndex = -1;
        private boolean aloadSeen = false;
        private BugAccumulator bugAccumulator;

        private BufferedWriter bw = null;

        public RethrowFinder(BugAccumulator bugAccumulator) {
            this.bugAccumulator = bugAccumulator;
        }

        @Override
        public void sawOpcode(int seen) {
            if (first) {
                try {
                    if (this.getDottedClassName().equals("exceptionInfo.BadSQLException")
                            && this.getMethodName().equals("badSQLE1")) {
                        bw = new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream("C:/logs/logr.txt"),
                                        StandardCharsets.UTF_8));
                        bw.append("mukodik a logolas\n");
                        found = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                }

                if (seen == Const.ASTORE) {
                    varIndex = this.getRegisterOperand();

                    try {
                        if (bw != null)
                            bw.append("varIndex: " + varIndex + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                    }
                } else {
                    varIndex = convertStoreOpToNumber(seen);
                }
            } else {
                if (seen == Const.ALOAD && varIndex == this.getRegisterOperand())
                    aloadSeen = true;
                else if (convertLoadOpToNumber(seen) == varIndex)
                    aloadSeen = true;
                else if (seen == Const.ATHROW && aloadSeen) {
                    BugInstance bug = new BugInstance(this, "IEH_INSECURE_EXCEPTION_HANDLING", LOW_PRIORITY)
                            .addClassAndMethod(this).addString("sensitive exception rethrown");
                    bugAccumulator.accumulateBug(bug, this);
                    try {
                        if (bw != null)
                            bw.append("bugReported: " + bug.toString() + "line: "
                                    + SourceLineAnnotation.fromVisitedInstruction(this).toString()
                                    + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException("HELO rossz fajlbairas" + e.getMessage());
                    }
                }
            }
        }

        private int convertStoreOpToNumber(int seen) {
            switch (seen) {
            case Const.ASTORE_0:
                return 0;
            case Const.ASTORE_1:
                return 1;
            case Const.ASTORE_2:
                return 2;
            case Const.ASTORE_3:
                return 3;
            default:
                return -1;
            }
        }

        private int convertLoadOpToNumber(int seen) {
            switch (seen) {
            case Const.ALOAD_0:
                return 0;
            case Const.ALOAD_1:
                return 1;
            case Const.ALOAD_2:
                return 2;
            case Const.ALOAD_3:
                return 3;
            default:
                return -1;
            }
        }
    }

}
