package com.ives.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ives on 2017/7/2 0002.
 */

public class ParameterLengthConstraintDetector extends Detector implements Detector.JavaPsiScanner, Detector.ClassScanner {

    public static final Issue ISSUE =   Issue.create("ParameterLengthConstraint",
            "At least one parameter not a set or their lengths are inconsistent",
            "Parameters which specified by annotation '@LengthEqual' in a same method or constructor,must fulfil these two conditions:\n" +
                    "1. Their type must be subtype of java.util.Set,java.util.Collection or an array.\n" +
                    "2. Their elements's counts are consistent.",
            Category.CORRECTNESS,
            6,
            Severity.ERROR,
            new Implementation(ParameterLengthConstraintDetector.class, Scope.JAVA_FILE_SCOPE));
//
//    @Override
//    public List<Class<? extends Node>> getApplicableNodeTypes() {
//        return Arrays.asList(Annotation.class);
//    }
//
//    @Override
//    public AstVisitor createJavaVisitor(JavaContext context) {
//        return new LengthEqualAnnotationVisitor(context);
//    }

    @Override
    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
        return Arrays.asList(PsiAnnotation.class);
    }

    @Override
    public JavaElementVisitor createPsiVisitor(JavaContext context) {
        return new LengthEqualAnnotationChecker(context);
    }

    private class LengthEqualAnnotationChecker extends JavaElementVisitor {
        private JavaContext mContext;
        public LengthEqualAnnotationChecker(JavaContext context) {
            mContext = context;
        }

        private Integer test(@LengthEqual String aa, @LengthEqual int[] bb, @LengthEqual List<String> cc, @LengthEqual Boolean[] dd){
            return null;
        }
        @Override
        public void visitAnnotation(PsiAnnotation annotation) {
            String annotationName = annotation.getQualifiedName();
            if(annotationName==null || !annotationName.startsWith("com.ives.lint.LengthEqual")){
                return;
            }
            PsiAnnotationOwner owner = annotation.getOwner();
            if(owner instanceof PsiModifierList){
               PsiElement param = ((PsiModifierList) owner).getParent();
                if(param instanceof PsiParameter){
//                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getName()+"\t$$$getName");
//                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getTypeElement().toString()+"\t$$$getTypeElement");
//                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getType().toString()+"\t$$$getType");
//                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getNameIdentifier().toString()+"\t$$$getNameIdentifier");
//                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getNextSibling().toString()+"\t$$$getNextSibling");
//                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getType().getCanonicalText()+"\t$$$getCanonicalText");
//                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getTypeElement().getNavigationElement().toString()+"\t$$$getNavigationElement");
                    mContext.report(ISSUE, mContext.getLocation(annotation), "------------------");
                    if(!isCollection(((PsiParameter) param).getType())){
                        mContext.report(ISSUE, mContext.getLocation(annotation), "@LengthEqual 标记的参数只允许是集合或者数组");
                    }
                    mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiParameter) param).getType().getCanonicalText()+"\t$$$getCanonicalText");
                }else {
                    mContext.report(ISSUE, mContext.getLocation(annotation), "@LengthEqual 只允许添加到方法参数中");
                }
                mContext.report(ISSUE, mContext.getLocation(annotation), "这个是不是数组"+ (param  instanceof PsiArrayType));
            }
            mContext.report(ISSUE, mContext.getLocation(annotation), "探测到了注解");
//            mContext.report(ISSUE, mContext.getLocation(annotation), ((PsiModifierList)annotation.getOwner()).getParent().toString());

            super.visitAnnotation(annotation);
        }
        private boolean isCollection(PsiType paraType){
            //TODO 暂时采用一个笨办法，判断是否集合或者数组。有没有更好的办法？比如拿到参数的可判断子类或类型的类对象，而不是名字？
            //TODO 而且还应当是一个有序集合
            PsiType[] superTypes = ((PsiParameter) paraType).getType().getSuperTypes();
            if(superTypes!=null){
                for (PsiType pt : superTypes) {
                    if(pt.getCanonicalText().startsWith("java.util.Collection")||
                            pt.getCanonicalText().startsWith("java.util.Set")||
                            pt.getCanonicalText().endsWith("[]"))return true;
                }
            }
            return false;
        }
    }

}
