package edu.umd.cs.findbugs.detect;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;

public class FindInsecureExceptionHandling extends OpcodeStackDetector implements Detector {

    private final BugAccumulator bugAccumulator;
    private boolean seenInvoke = false;
    private CodeException[] exT = null;

    private String[] SensitiveCalls = { "java.net.ServerSocket.<init>", "java.net.ServerSocket.bind",
        "java.util.jar.JarFile.getInputStream", "java.util.jar.JarFile.<init>", "java.io.FileInputStream.<init>",
        "javax.naming.Context.lookup", "java.util.ResourceBundle.getBundle", "java.util.ResourceBundle.getObject",
        "java.sql.DriverManager.getConnection", "java.sql.DriverManager.getDriver",
        "java.sql.DriverManager.registerDriver",
        "java.sql.DriverManager.deregisterDriver",
        "java.io.FileOutputStream.<init>",
        "java.io.RandomAccessFile.<init>"
    };

    private HashMap<String, String> exceptions = new HashMap<String, String>();
    private RethrowFinderContainer possibleExceptionRethrows = new RethrowFinderContainer();

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
        exceptions.put("java.io.FileOutputStream.<init>", "java/io/FileNotFoundException");
        exceptions.put("java.io.RandomAccessFile.<init>", "java/io/FileNotFoundException");
    }

    @Override
    public void sawOpcode(int seen) {
        if (seen == Const.INVOKESPECIAL || seen == Const.INVOKEVIRTUAL || seen == Const.INVOKESTATIC
                || seen == Const.INVOKEINTERFACE) {
            seenInvoke = true;
        } else {
            seenInvoke = false;
        }
        possibleExceptionRethrows.sawOpcode(seen);
    }

    @Override
    public void afterOpcode(int seen) {
        super.afterOpcode(seen);
        possibleExceptionRethrows.afterOpcode(seen);
        if (!seenInvoke)
            return;
        if (seen == Const.INVOKESPECIAL || seen == Const.INVOKEVIRTUAL || seen == Const.INVOKESTATIC
                || seen == Const.INVOKEINTERFACE) {

            exT = this.getCode().getExceptionTable();

            String className = this.getDottedClassConstantOperand();
            String methodName = this.getNameConstantOperand();
            XMethod method = this.getXMethodOperand();

            String sourceSig = className + "." + methodName;

            Optional<String> possibleSensitiveCall = Arrays.stream(SensitiveCalls)
                    .filter(call -> sourceSig.equals(call))
                    .findAny();

            if (possibleSensitiveCall.isPresent()) {
                String call = possibleSensitiveCall.get();

                if (!isHandled(call)) {
                    BugInstance bug = new BugInstance(this, "IEH_INSECURE_EXCEPTION_HANDLING", NORMAL_PRIORITY)
                            .addClassAndMethod(this).addCalledMethod(method).addString("has no handler");
                    bugAccumulator.accumulateBug(bug, this);
                } else {
                    addExceptionToCheckForRethrow(call);
                }
            }
        }
    }

    @Override
    public void visitAfter(JavaClass obj) {
        bugAccumulator.reportAccumulatedBugs();
    }

    private boolean isHandled(String call) {
        int regIndex = this.getPC();
        boolean handled = false;
        ConstantPool cpool = this.getCode().getConstantPool();
        for (CodeException ex : exT) {
            if (ex.getStartPC() <= regIndex && regIndex < ex.getEndPC()) {
                int index = ex.getCatchType();
                if (index != 0) {
                    String exSig = ((ConstantClass) (cpool.getConstant(index))).getBytes(cpool);
                    ClassDescriptor expectedException = DescriptorFactory.createClassDescriptor(exceptions.get(call));
                    try {
                        if (isChildClassOf(expectedException, exSig)) {
                            handled = true;
                            break;
                        }
                    } catch (CheckedAnalysisException e) {
                        break;
                    }
                } else {
                    handled = true;
                }
            }
        }
        return handled;
    }

    private void addExceptionToCheckForRethrow(String call) {
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

        ConstantPool cpool = getConstantPool();

        for (int i = 1; i <= exceptions.size(); i++) {

            int index = exceptions.get(i - 1).getCatchType();
            try {
                if (index != 0) {
                    String exSig = ((ConstantClass) (cpool.getConstant(index))).getBytes(cpool);
                    if (isChildClassOf(DescriptorFactory.createClassDescriptor(handledException), exSig)) {

                        possibleExceptionRethrows.add(new ExceptionRethrowFinder(exceptions.get(i - 1).getHandlerPC(),
                                i < exceptions.size() ? exceptions.get(i).getHandlerPC() : -1, this));
                    }
                } else {
                    possibleExceptionRethrows.add(new ExceptionRethrowFinder(exceptions.get(i - 1).getHandlerPC(),
                            i < exceptions.size() ? exceptions.get(i).getHandlerPC() : -1, this));
                }
            } catch (CheckedAnalysisException e) {

            }
        }
    }

    private boolean isChildClassOf(ClassDescriptor className, String superC) throws CheckedAnalysisException {

        String stringClassName = className.toString();
        int i = 0;

        while (!stringClassName.equals("java/lang/Object") && i < 20) {
            if (stringClassName.equals(superC)) {
                return true;
            }
            className = className.getXClass().getSuperclassDescriptor();
            if (className == null) {
                break;
            }
            stringClassName = className.toString();
            if (stringClassName == null) {
                break;
            }
            i++;
        }
        return false;
    }

    private class ExceptionRethrowFinder {

        private int varIndex = -1;
        private boolean exceptionLoaded = false;
        private boolean exceptionWrapped = false;
        private boolean end = false;
        private boolean first = true;
        private OpcodeStack.Item loadedEx = null;

        private final int startPC;
        private final int endPC;
        private final OpcodeStackDetector original;

        public ExceptionRethrowFinder(int startPC, int endPC, OpcodeStackDetector original) {
            this.startPC = startPC;
            this.endPC = endPC;
            this.original = original;
        }

        public int getStartPC() {
            return this.startPC;
        }

        public int getEndPC() {
            return this.endPC;
        }

        public boolean isFinished() {
            return this.end;
        }

        public void sawOpcode(int seen) {

            if (original.getPC() >= this.endPC && this.endPC != -1)
                this.end = true;
            if (this.end)
                return;

            if (this.first) {

                if (seen == Const.ASTORE) {
                    varIndex = original.getRegisterOperand();

                } else {
                    varIndex = convertStoreOpToNumber(seen);
                    if (varIndex == -1)
                        end = true;
                }
                this.first = false;
            } else {
                if (exceptionLoaded && seen == Const.INVOKESPECIAL) {
                    ClassDescriptor className = original.getClassDescriptorOperand();
                    try {
                        if (isChildClassOf(className, "java/lang/Exception")) {
                            if (isExOnStack()) {
                                exceptionWrapped = true;
                            } else {
                                end = true;
                            }
                        }
                    } catch (CheckedAnalysisException e) {
                        end = true;
                    }
                } else if (seen == Const.ATHROW && exceptionWrapped) {
                    BugInstance bug = new BugInstance(original, "IEH_INSECURE_EXCEPTION_HANDLING", NORMAL_PRIORITY)
                            .addClassAndMethod(original).addString("sensitive exception rethrown");
                    bugAccumulator.accumulateBug(bug, original);
                    end = true;
                } else if (seen == Const.GOTO || seen == Const.RETURN) {
                    end = true;
                }
            }
        }

        public void afterOpcode(int seen) {
            if ((seen == Const.ALOAD && varIndex == original.getRegisterOperand())
                    || convertLoadOpToNumber(seen) == varIndex) {
                exceptionLoaded = true;
                loadedEx = stack.getStackItem(0);
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

        private boolean isExOnStack() {
            for (int k = 0; k < stack.getStackDepth(); k++) {
                if (stack.getStackItem(k) == loadedEx) {
                    return true;
                }
            }
            return false;
        }
    }

    private class RethrowFinderContainer {
        private ArrayList<ExceptionRethrowFinder> possibleExceptionRethrows = new ArrayList<ExceptionRethrowFinder>();

        public RethrowFinderContainer() {

        }

        public void add(ExceptionRethrowFinder e) {
            possibleExceptionRethrows.add(e);
        }

        public void sawOpcode(int seen) {
            for (int i = 0; i < possibleExceptionRethrows.size(); i++) {
                ExceptionRethrowFinder rf = possibleExceptionRethrows.get(i);
                if (!rf.isFinished()) {
                    if (rf.startPC <= getPC())
                        rf.sawOpcode(seen);
                } else {
                    possibleExceptionRethrows.remove(rf);
                    i--;
                }
            }
        }

        public void afterOpcode(int seen) {
            for (int i = 0; i < possibleExceptionRethrows.size(); i++) {
                ExceptionRethrowFinder rf = possibleExceptionRethrows.get(i);
                if (!rf.isFinished()) {
                    if (rf.startPC <= getPC())
                        rf.afterOpcode(seen);
                } else {
                    possibleExceptionRethrows.remove(rf);
                    i--;
                }
            }
        }
    }
}
