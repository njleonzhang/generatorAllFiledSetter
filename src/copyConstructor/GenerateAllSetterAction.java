package copyConstructor;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;

public class GenerateAllSetterAction extends BaseGenerateAction {

	public GenerateAllSetterAction() {
		super(new GenerateAllSetterHandler());
	}

	@Override
	protected boolean isValidForClass(PsiClass targetClass) {
		return super.isValidForClass(targetClass)
				&& !(targetClass instanceof PsiAnonymousClass)
				&& !ConstructorUtil.hasCopyConstructor(targetClass);
	}
}
