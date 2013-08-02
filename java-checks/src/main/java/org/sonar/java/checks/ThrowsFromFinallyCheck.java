/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.ast.parser.JavaGrammar;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "S1163",
  priority = Priority.MAJOR)
@BelongsToProfile(title = "Sonar way", priority = Priority.MAJOR)
public class ThrowsFromFinallyCheck extends SquidCheck<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(JavaGrammar.THROW_STATEMENT);
  }

  @Override
  public void visitNode(AstNode node) {
    if (isInFinally(node)) {
      getContext().createLineViolation(this, "Refactor this code to not throw exceptions in finally blocks.", node);
    }
  }

  private static boolean isInFinally(AstNode node) {
    return getFirstAncestor(node, JavaGrammar.FINALLY_, JavaGrammar.CLASS_BODY_DECLARATION).is(JavaGrammar.FINALLY_);
  }

  private static AstNode getFirstAncestor(AstNode node, AstNodeType type1, AstNodeType type2) {
    AstNode result = node.getParent();

    while (result != null && !result.is(type1, type2)) {
      result = result.getParent();
    }

    return result;
  }

}
