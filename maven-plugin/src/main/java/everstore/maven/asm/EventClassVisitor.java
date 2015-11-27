package everstore.maven.asm;

import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventClassVisitor extends ClassVisitor implements Opcodes {

    private final List<EventField> fields = new ArrayList<>();

    public EventClassVisitor(ClassWriter writer) {
        super(ASM5, writer);
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName,
                      String[] interfaces) {
        System.out.println("Visiting class: " + name);
        System.out.println("Super class: " + superName);
        String[] newInterfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
        newInterfaces[interfaces.length - 1] = "everstore/maven/EventSerializable";

        super.visit(version, access, name, signature, superName, newInterfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        System.out.println("Field: " + name + " " + desc + " value:" + value);
        fields.add(new EventField(name));
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        System.out.println("Attribute: " + attr);
        super.visitAttribute(attr);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        System.out.println("visitMethod: " + name + " signature: " + signature);
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
