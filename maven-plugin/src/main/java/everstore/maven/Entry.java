package everstore.maven;

import everstore.maven.asm.EventClassVisitor;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

import static jdk.internal.org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;

public class Entry {
    public static void main(String[] args) throws IOException {
        ClassWriter writer = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);

        ClassVisitor cv = new EventClassVisitor(writer);
        try (InputStream stream = Entry.class.getResourceAsStream("/everstore/maven/TestEvent.class")) {
            ClassReader classReader = new ClassReader(stream);
            classReader.accept(cv, 0);
        }
//
//
//        ClassWriter cw = new ClassWriter(0);
//        FieldVisitor fv;
//        MethodVisitor mv;
//        AnnotationVisitor av0;
//
//        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "everstore/maven/TestEvent", null, "java/lang/Object", new String[]{"everstore/maven/EventSerializable"});
//
//        {
//            fv = cw.visitField(ACC_PUBLIC + ACC_FINAL, "id", "I", null, null);
//            fv.visitEnd();
//        }
//        {
//            fv = cw.visitField(ACC_PUBLIC + ACC_FINAL, "name", "Ljava/lang/String;", null, null);
//            fv.visitEnd();
//        }
//        {
//            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
//            mv.visitEnd();
//        }
//        {
//            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(ILjava/lang/String;)V", null, null);
//            mv.visitEnd();
//        }
//        {
//            mv = cw.visitMethod(ACC_PUBLIC, "serializeEvent", "(Leverstore/maven/EventWriter;)V", null, null);
//            mv.visitEnd();
//        }
//        cw.visitEnd();

        /*
        aload 1
    ldc "id"
    aload 0
    getfield 'everstore/maven/TestEvent.id','I'
    INVOKEINTERFACE everstore/maven/EventWriter.put (Ljava/lang/String;I)V
    aload 1
    ldc "name"
    aload 0
    getfield 'everstore/maven/TestEvent.name','Ljava/lang/String;'
    INVOKEINTERFACE everstore/maven/EventWriter.put (Ljava/lang/String;Ljava/lang/String;)V
    return
         */


        //writer.toString()
//
        final MethodVisitor method =
                writer.visitMethod(Opcodes.ACC_PUBLIC, "newInterfaces", "(Leverstore/maven/EventWriter;)V", null, null);
        method.visitCode();

//        Handle bootstrap = new Handle(Opcodes.H_INVOKESTATIC, dynamicLinkageClassName, bootstrapMethodName, 28
//                mt.toMethodDescriptorString());


        method.visitVarInsn(ALOAD, 1);
        method.visitLdcInsn("id");
        method.visitVarInsn(ALOAD, 0);
        method.visitFieldInsn(GETFIELD, "everstore/maven/TestEvent", "id", "I");
        method.visitInvokeDynamicInsn("everstore/maven/EventWriter.put", "(Ljava/lang/String;I)V", );

        method.visitEnd();
    }

}
