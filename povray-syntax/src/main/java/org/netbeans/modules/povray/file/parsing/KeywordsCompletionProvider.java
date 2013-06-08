/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.povray.file.parsing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tim Boudreau
 */
@MimeRegistration(mimeType = "text/x-povray", service = CompletionProvider.class)
public class KeywordsCompletionProvider implements CompletionProvider {

    private static String findCurrentWord(JTextComponent jtc) {
        int pos = jtc.getCaretPosition() - 1;
        Document d = jtc.getDocument();
        StringBuilder sb = new StringBuilder();
        for (;;) {
            try {
                String s = d.getText(pos, 1);
                if (Character.isWhitespace(s.charAt(0))) {
                    break;
                }
                sb.insert(0, s);
                pos--;
            } catch (BadLocationException ex) {
                return null;
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    @Override
    public CompletionTask createTask(int completionType, JTextComponent jtc) {
        switch (completionType) {
            case COMPLETION_QUERY_TYPE:
                final String test = findCurrentWord(jtc);
                if (test == null) {
                    return createTask(completionType + 1, jtc);
                }
                return new AsyncCompletionTask(new AsyncCompletionQuery() {
                    @Override
                    protected void query(CompletionResultSet crs, Document dcmnt, int i) {
                        boolean onlyHash = "#".equals(test);
                        boolean isHash = test.startsWith("#");
                        for (Keywords keyword : Keywords.values()) {
                            String s = keyword.toString();
                            if (onlyHash) {
                                if (keyword.occursAfterHash()) {
                                    crs.addItem(new KeywordCompletionItem(keyword));
                                }
                            } else {
                                String tst = test;
                                if (isHash) {
                                    tst = test.substring(1);
                                }
                                if (s.startsWith(tst)) {
                                    crs.addItem(new KeywordCompletionItem(keyword));
                                }
                            }
                        }
                        crs.finish();
                    }
                });
            default:
                return new AsyncCompletionTask(new AsyncCompletionQuery() {
                    @Override
                    protected void query(CompletionResultSet crs, Document dcmnt, int i) {
                        for (Keywords keyword : Keywords.values()) {
                            crs.addItem(new KeywordCompletionItem(keyword));
                        }
                        crs.finish();
                    }
                });
        }
    }

    @Override
    public int getAutoQueryTypes(JTextComponent jtc, String string) {
        return 0;
    }

    private static final class KeywordCompletionItem implements CompletionItem {

        private final Keywords keyword;

        public KeywordCompletionItem(Keywords keyword) {
            this.keyword = keyword;
        }

        @Override
        public void defaultAction(JTextComponent jtc) {
            try {
                String w = findCurrentWord(jtc);
                String toInsert = keyword.toString();
                if (w != null && w.length() > 0) {
                    int off = w.length();
                    if (off < toInsert.length()) {
                        int start = w.startsWith("#") ? off - 1 : off;
                        toInsert = toInsert.substring(start);
                    }
                }
                if (keyword.isVisualAttribute()) {
                    //XXX get current indent
                    toInsert += " {\n}";
                }
                if (toInsert.length() >= 0) {
                    jtc.getDocument().insertString(jtc.getCaretPosition(), toInsert + " ", null);
                }
                Completion.get().hideAll();
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public void processKeyEvent(KeyEvent ke) {
            //do nothing
        }

        @Override
        public int getPreferredWidth(Graphics grphcs, Font font) {
            return CompletionUtilities.getPreferredWidth(keyword.toString(), null, grphcs, font);
        }
        private final Color fieldColor = Color.BLACK;
        private final ImageIcon fieldIcon = ImageUtilities.loadImageIcon("org/netbeans/modules/povray/file/favicon.png", true);

        @Override
        public void render(Graphics g, Font defaultFont, Color defaultColor,
                Color backgroundColor, int width, int height, boolean selected) {
            CompletionUtilities.renderHtml(fieldIcon, keyword.toString(), null, g, defaultFont,
                    (selected ? Color.white : fieldColor), width, height, selected);
        }

        @Override
        public CompletionTask createDocumentationTask() {
            return null;
        }

        @Override
        public CompletionTask createToolTipTask() {
            return null;
        }

        @Override
        public boolean instantSubstitution(JTextComponent jtc) {
            return false;
        }

        @Override
        public int getSortPriority() {
            return 0;
        }

        @Override
        public CharSequence getSortText() {
            return keyword.toString();
        }

        @Override
        public CharSequence getInsertPrefix() {
            return getSortText();
        }
    }
}
