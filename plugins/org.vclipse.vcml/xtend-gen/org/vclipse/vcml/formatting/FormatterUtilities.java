package org.vclipse.vcml.formatting;

import java.util.List;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.formatting.impl.FormattingConfig.IndentationLocatorEnd;
import org.eclipse.xtext.formatting.impl.FormattingConfig.IndentationLocatorStart;
import org.eclipse.xtext.formatting.impl.FormattingConfig.LinewrapLocator;
import org.eclipse.xtext.formatting.impl.FormattingConfig.NoLinewrapLocator;
import org.eclipse.xtext.formatting.impl.FormattingConfig.NoSpaceLocator;
import org.eclipse.xtext.util.Pair;

@SuppressWarnings("all")
public class FormatterUtilities {
  public void handleCurlyBraces(final IGrammarAccess grammarAccess, final FormattingConfig config, final String left, final String right) {
    List<Pair<Keyword,Keyword>> _findKeywordPairs = grammarAccess.findKeywordPairs(left, right);
    for (final Pair<Keyword,Keyword> pair : _findKeywordPairs) {
      {
        final Keyword open = pair.getFirst();
        IndentationLocatorStart _setIndentationIncrement = config.setIndentationIncrement();
        _setIndentationIncrement.after(open);
        LinewrapLocator _setLinewrap = config.setLinewrap();
        _setLinewrap.after(open);
        final Keyword close = pair.getSecond();
        IndentationLocatorEnd _setIndentationDecrement = config.setIndentationDecrement();
        _setIndentationDecrement.before(close);
        LinewrapLocator _setLinewrap_1 = config.setLinewrap();
        _setLinewrap_1.around(close);
      }
    }
  }
  
  public void handleNoSpaceNoLineWrapBraces(final IGrammarAccess grammarAccess, final FormattingConfig config, final String left, final String right) {
    List<Pair<Keyword,Keyword>> _findKeywordPairs = grammarAccess.findKeywordPairs(left, right);
    for (final Pair<Keyword,Keyword> pair : _findKeywordPairs) {
      {
        NoSpaceLocator _setNoSpace = config.setNoSpace();
        Keyword _first = pair.getFirst();
        _setNoSpace.after(_first);
        NoSpaceLocator _setNoSpace_1 = config.setNoSpace();
        Keyword _second = pair.getSecond();
        _setNoSpace_1.before(_second);
        NoLinewrapLocator _setNoLinewrap = config.setNoLinewrap();
        Keyword _first_1 = pair.getFirst();
        Keyword _second_1 = pair.getSecond();
        _setNoLinewrap.between(_first_1, _second_1);
      }
    }
  }
}
