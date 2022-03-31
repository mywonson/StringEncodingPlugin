package com.wonson;
import org.objectweb.asm.*;
import java.io.*;
import java.util.Base64;
import static org.objectweb.asm.Opcodes.*;
public class Operator {
    public static void run(File file){
        if(file.isDirectory()){
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (!pathname.isDirectory() && !pathname.getName().endsWith(".class")) {
                        return false;
                    } else {
                        return true;
                    }
                }
            };
            File[] files = file.listFiles(fileFilter);
            for (File listFile : files) {
                 run(listFile);
            }
        }else {
            for(int index = 0; index < 3;index++) {
                if(index == 0) {
                    start(file,true);
                }else {
                    start(file,false);
                }
            }
        }
    }

    private static void start(File classFile,boolean addDecodeMethod){
        try {
            FileInputStream fileInputStream = new FileInputStream(classFile);
            byte[] src = inputStreamToByteArray(fileInputStream);
            ClassReader classReader = new ClassReader(src);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5,classWriter){
                private String owner;
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, name, signature, superName, interfaces);
                    this.owner = name;
                    if(addDecodeMethod) {
                        System.out.println("ÕýÔÚ¼ÓÃÜ:" + name);
                        addDecodeFunction(classWriter);
                    }
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                    MethodVisitor myMethodVisitor = new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                        @Override
                        public void visitLdcInsn(Object cst) {
                            if(cst != null && cst instanceof String){
                                String target = String.class.cast(cst);
                                byte[] bytes = target.getBytes();
                                for(int i = 0;i < bytes.length; i++) bytes[i] ^= 0x6F;
                                String encode = Base64.getEncoder().encodeToString(bytes);
                                methodVisitor.visitLdcInsn(encode);
                                mv.visitMethodInsn(INVOKESTATIC,owner, "decodeString", "(Ljava/lang/String;)Ljava/lang/String;", false);
                                return;
                            }
                            super.visitLdcInsn(cst);
                        }
                    };
                    return myMethodVisitor;
                }
            };
            classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES);
            byte[] bytes = classWriter.toByteArray();
            FileOutputStream fileOutputStream = new FileOutputStream(classFile);
            fileOutputStream.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addDecodeFunction(ClassWriter cw){
        MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "decodeString", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(21, l0);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false);
        mv.visitVarInsn(ASTORE, 1);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLineNumber(22, l1);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{"[B", Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        Label l3 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l3);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(23, l4);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(DUP2);
        mv.visitInsn(BALOAD);
        mv.visitIntInsn(BIPUSH, 111);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2B);
        mv.visitInsn(BASTORE);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitLineNumber(22, l5);
        mv.visitIincInsn(2, 1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l3);
        mv.visitLineNumber(25, l3);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
        mv.visitInsn(ARETURN);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLocalVariable("i", "I", null, l2, l3, 2);
        mv.visitLocalVariable("msg", "Ljava/lang/String;", null, l0, l6, 0);
        mv.visitLocalVariable("decode", "[B", null, l1, l6, 1);
        mv.visitMaxs(4, 3);
        mv.visitEnd();
    }

    private static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        byte[] result = null;
        byte[] buffer = new byte[1024];
        int offset;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((offset = inputStream.read(buffer)) != -1) byteArrayOutputStream.write(buffer,0,offset);
        result = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        inputStream.close();
        return result;
    }
}