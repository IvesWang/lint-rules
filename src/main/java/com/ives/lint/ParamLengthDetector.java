package com.ives.lint;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.ClassContext;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;

import lombok.ast.AnnotationMethodDeclaration;
import lombok.ast.AstVisitor;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.MethodDeclaration;
import lombok.ast.MethodInvocation;
import lombok.ast.Node;

public class ParamLengthDetector extends Detector implements Detector.JavaScanner{

    public static final Issue ISSUE =   Issue.create("ParameterLengthTest",
                                                    "Parameters length should be equal",
                                                    "Parameters length should be equal desc",
                                                    Category.CORRECTNESS,
                                                    6,
                                                    Severity.ERROR,
                                                    new Implementation(ParamLengthDetector.class, Scope.JAVA_FILE_SCOPE));

//    @Override
//    public void visitMethod(JavaContext context, JavaElementVisitor visitor, PsiMethodCallExpression call, PsiMethod method) {
//        super.visitMethod(context, visitor, call, method);
//    }

    @Override
    public AstVisitor createJavaVisitor(JavaContext context) {
        return new ParameterVisitor(context);//返回一个侦测器
    }
    private class ParameterVisitor extends  ForwardingAstVisitor{
        private JavaContext mContext;

        public ParameterVisitor(JavaContext context) {
            this.mContext = context;
        }

//        @Override
//        public boolean visitAnnotationMethodDeclaration(AnnotationMethodDeclaration node) {
//
//            this.mContext.report(ISSUE, node, mContext.getLocation(node), "我的提示");
//            return super.visitAnnotationMethodDeclaration(node);
//        }
//
//        @Override
//        public void afterVisitMethodDeclaration(MethodDeclaration node) {
//            this.mContext.report(ISSUE, node, mContext.getLocation(node), "我的提示");
//            super.afterVisitMethodDeclaration(node);
//        }

        @Override
        public boolean visitMethodInvocation(MethodInvocation node) {

            if (node.toString().startsWith("System.out.println")) {
                mContext.report(ISSUE, node, mContext.getLocation(node),
                        "请使用Ln3，避免使用System.out.println");
                return true;
            }

            JavaParser.ResolvedNode resolve = mContext.resolve(node);
            if (resolve instanceof JavaParser.ResolvedMethod) {
                JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) resolve;
                // 方法所在的类校验
                JavaParser.ResolvedClass containingClass = method.getContainingClass();
                if (containingClass.matches("android.util.Log")) {
                    mContext.report(ISSUE, node, mContext.getLocation(node),
                            "请使用Ln3，避免使用Log");
                    return true;
                }
            }
            return super.visitMethodInvocation(node);
        }
    }

//    @Override
//    public void visitMethod(JavaContext context, AstVisitor visitor, MethodInvocation node) {
//        super.visitMethod(context, visitor, node);
//    }

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Arrays.asList(
//                AnnotationMethodDeclaration.class,
//                MethodDeclaration.class,
                MethodInvocation.class);
    }

//    @Override
//    public JavaElementVisitor createPsiVisitor(JavaContext context) {
//        return new JavaElementVisitor() {
//            @Override
//            public void visitAnnotationParameterList(PsiAnnotationParameterList list) {
//                super.visitAnnotationParameterList(list);
//            }
//
//            @Override
//            public void visitAnnotationMethod(PsiAnnotationMethod method) {
//                super.visitAnnotationMethod(method);
//            }
//        };
//    }
//
//    @Override
//    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
//        return Arrays.asList(PsiAnnotationMethod.class);
//    }
}
