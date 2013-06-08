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

import java.awt.event.KeyEvent;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import org.netbeans.modules.editor.NbEditorKit;

/**
 *
 * @author Tim Boudreau
 */
public class PovKit extends NbEditorKit {

    public PovKit() {
        System.err.println("CREATED A POV KIT");
    }

    @Override
    public String getContentType() {
        return "text/x-povray";
    }

    @Override
    protected void executeInstallActions(JEditorPane result) {
        result.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, KeyEvent.CTRL_MASK), "toggle-comment");
        result.getActionMap().put("toggle-comment", new CommentOutAction());
        System.err.println("SET UP ACTION");
        super.executeInstallActions(result);
    }

    @Override
    protected void executeDeinstallActions(JEditorPane c) {
        super.executeDeinstallActions(c);
    }
}
