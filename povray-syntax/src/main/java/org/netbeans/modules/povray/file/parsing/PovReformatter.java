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

import javax.swing.text.BadLocationException;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.ReformatTask;

/**
 *
 * @author Tim Boudreau
 */
public class PovReformatter implements ReformatTask.Factory {

    //XXX implement and register me

    @Override
    public ReformatTask createTask(Context cntxt) {
        return new PovReformatTask(cntxt);
    }

    static class PovReformatTask implements ReformatTask, ExtraLock {
        private final Context context;

        public PovReformatTask(Context context) {
            this.context = context;
        }

        @Override
        public void reformat() throws BadLocationException {
            //do nothing
        }

        @Override
        public ExtraLock reformatLock() {
            return this;
        }

        boolean locked;

        @Override
        public void lock() {
            locked = true;
        }

        @Override
        public void unlock() {
            locked = false;
        }
    }

}
