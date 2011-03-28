package org.jetbrains.jet.lang.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.JetNodeTypes;

/**
 * @author max
 */
public class JetThisExpression extends JetLabelQualifiedExpression {

    public JetThisExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(JetVisitor visitor) {
        visitor.visitThisExpression(this);
    }

    @Nullable
    public JetTypeReference getSuperTypeQualifier() {
        return (JetTypeReference) findChildByType(JetNodeTypes.TYPE_REFERENCE);
    }

    @Nullable
    public JetSimpleNameExpression getLabelElement() {
        return findChildByClass(JetSimpleNameExpression.class);
    }

    @Nullable
    public String getLabelName() {
        JetSimpleNameExpression labelElement = getLabelElement();
        assert labelElement == null || labelElement.getText().startsWith("@");
        return labelElement == null ? null : labelElement.getText().substring(1);
    }
}
