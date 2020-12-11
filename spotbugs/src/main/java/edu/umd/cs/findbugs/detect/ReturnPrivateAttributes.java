package edu.umd.cs.findbugs.detect;

import java.util.Map;
import java.util.HashMap;

import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.ba.XField;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class ReturnPrivateAttributes
        extends OpcodeStackDetector
        implements Detector {
    private final BugReporter bugReporter;

    private XField fieldUnderClone = null;
    private XField fieldCloneUnderCast = null;
    private Map<OpcodeStack.Item, XField> arrayClones =
            new HashMap<OpcodeStack.Item, XField>();

    public ReturnPrivateAttributes(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        fieldUnderClone = null;
        fieldCloneUnderCast = null;

        if (seen == Const.ARETURN) {
            OpcodeStack.Item item = stack.getStackItem(0);
            XField field = item.getXField();
            if (field == null) {
                field = arrayClones.get(item);
                if (field == null)
                    return;
            }

            if (!field.getClassDescriptor().equals(getClassDescriptor()))
                return;

            if (!field.isPrivate() && !field.isProtected())
                return;

            Field clsField = getClassField(field, getClassName());
            if (clsField.getType() instanceof ObjectType &&
                    isImmutable(((ObjectType) clsField.getType())
                            .getClassName()))
                return;

            bugReporter
                    .reportBug(new BugInstance(this,
                            "RPMA_RETURN_PRIVATE_MUTABLE_ATTRIBUTES",
                            NORMAL_PRIORITY)
                                    .addClass(this).addField(field)
                                    .addClassAndMethod(this).addSourceLine(this));
        } else if (seen == Const.INVOKEINTERFACE ||
                seen == Const.INVOKEVIRTUAL) {
            MethodDescriptor method = getMethodDescriptorOperand();
            if (method == null)
                return;

            if (!"clone".equals(method.getName()))
                return;

            XField field = stack.getStackItem(0).getXField();
            if (field == null)
                return;

            if (!field.getClassDescriptor().equals(getClassDescriptor()))
                return;

            if (!field.isPrivate() && !field.isProtected())
                return;

            Field clsField = getClassField(field, getClassName());
            Type type = clsField.getType();
            if (!(type instanceof ArrayType))
                return;

            Type elementType = ((ArrayType) type).getElementType();
            if (elementType instanceof ObjectType &&
                    isImmutable(((ObjectType) elementType).getClassName()))
                return;

            fieldUnderClone = field;
        } else if (seen == Const.CHECKCAST) {
            OpcodeStack.Item item = stack.getStackItem(0);
            XField field = arrayClones.get(item);
            if (field == null)
                return;
            fieldCloneUnderCast = field;
        }
    }

    @Override
    public void afterOpcode(int seen) {
        super.afterOpcode(seen);

        if (seen == Const.INVOKEINTERFACE || seen == Const.INVOKEVIRTUAL) {
            if (fieldUnderClone == null)
                return;
            arrayClones.put(stack.getStackItem(0), fieldUnderClone);
        } else if (seen == Const.CHECKCAST) {
            OpcodeStack.Item item = stack.getStackItem(0);
            if (fieldCloneUnderCast == null)
                return;
            arrayClones.put(item, fieldCloneUnderCast);
        }
    }

    private static Field getClassField(XField field, String className) {
        try {
            JavaClass cls = Repository.lookupClass(className);
            for (Field clsField : cls.getFields()) {
                if (field.getName().equals(clsField.getName()))
                    return clsField;
            }
        } catch (ClassNotFoundException cnfe) {
            assert false;
        }
        return null;
    }

    private static boolean isImmutable(String className) {
        if ("java.lang.String".equals(className) ||
                "java.lang.Character".equals(className) ||
                "java.lang.Byte".equals(className) ||
                "java.lang.Short".equals(className) ||
                "java.lang.Integer".equals(className) ||
                "java.lang.Long".equals(className) ||
                "java.lang.Float".equals(className) ||
                "java.lang.Double".equals(className) ||
                "java.lang.Boolean".equals(className)) {
            return true;
        }

        try {
            JavaClass cls = Repository.lookupClass(className);

            if (!cls.isFinal()) {
                return false;
            }

            for (Field field : cls.getFields()) {
                if (!field.isStatic() &&
                        (!field.isFinal() || !field.isPrivate())) {
                    return false;
                }
            }

            return true;
        } catch (ClassNotFoundException cnfe) {
            assert false;
        }
        return false;
    }
}
