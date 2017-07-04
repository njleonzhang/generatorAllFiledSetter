package copyConstructor;

import com.intellij.codeInsight.generation.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GenerateAllSetterHandler extends GenerateMembersHandlerBase {

	public GenerateAllSetterHandler() {
		super(null);
	}

	@Override
	protected String getNothingFoundMessage() {
		return "Copy constructor already exists";
	}

	@Override
	protected ClassMember[] getAllOriginalMembers(PsiClass aClass) {
		return toMembers(ConstructorUtil.getAllCopyableFields(aClass));
	}

	@Nullable
	@Override
	protected ClassMember[] chooseMembers(ClassMember[] members, boolean allowEmptySelection, boolean copyJavadocCheckbox, Project project, @Nullable Editor editor) {
		return members;
	}

	@NotNull
	@Override
	protected List<? extends GenerationInfo> generateMemberPrototypes(PsiClass aClass, ClassMember[] members) throws IncorrectOperationException {
		PsiMethod copyConstructor = generateCopyConstructor(aClass, members);
		return Collections.singletonList(new PsiGenerationInfo<PsiMethod>(copyConstructor));
	}

	@Override
	protected GenerationInfo[] generateMemberPrototypes(PsiClass aClass, ClassMember originalMember) throws IncorrectOperationException {
		return null;
	}

	private PsiMethod generateCopyConstructor(PsiClass psiClass, ClassMember[] copyableFields) {
		String parameterName = "other";

		StringBuilder code = new StringBuilder();
		code.append(String.format("public void set(%s %s) {", psiClass.getName(), parameterName));

		boolean superclassHasCopyConstructor = ConstructorUtil.hasCopyConstructor(psiClass.getSuperClass());
		if (superclassHasCopyConstructor) {
			code.append(String.format("super(%s);", parameterName));
		}

		for (ClassMember fieldMember : copyableFields) {
			PsiField field = ((PsiFieldMember) fieldMember).getElement();
			String name = field.getName();
			code.append(String.format("this.%s = %s.%s;", name, parameterName, name));
		}

		code.append("notifyChange();\n");
		code.append("}");

		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
		PsiMethod constructor = elementFactory.createMethodFromText(code.toString(), psiClass);
		return constructor;
	}

	private ClassMember[] toMembers(List<PsiField> allCopyableFields) {
		ClassMember[] classMembers = new ClassMember[allCopyableFields.size()];
		for (int i = 0; i < allCopyableFields.size(); i++) {
			classMembers[i] = new PsiFieldMember(allCopyableFields.get(i));
		}
		return classMembers;
	}
}
