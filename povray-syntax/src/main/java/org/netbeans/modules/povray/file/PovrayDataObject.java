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
package org.netbeans.modules.povray.file;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.modules.povray.file.parsing.CommentOutAction;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@Messages({
    "LBL_Povray_LOADER=Povray Scene Files"
})
@MIMEResolver.ExtensionRegistration(displayName = "#LBL_Povray_LOADER",
mimeType = "text/x-povray",
extension = {"pov", "inc"})
@DataObject.Registration(mimeType = "text/x-povray",
iconBase = "org/netbeans/modules/povray/file/favicon.png",
displayName = "#LBL_Povray_LOADER",
position = 300)
@ActionReferences({
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
    position = 100,
    separatorAfter = 200),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
    position = 300),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
    position = 400,
    separatorAfter = 500),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
    position = 600),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
    position = 700,
    separatorAfter = 800),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
    position = 900,
    separatorAfter = 1000),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
    position = 1100,
    separatorAfter = 1200),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
    position = 1300),
    @ActionReference(path = "Loaders/text/x-povray/Actions",
    id =
    @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
    position = 1400)
})
public class PovrayDataObject extends MultiDataObject {

    public PovrayDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/x-povray", true);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @MultiViewElement.Registration(displayName = "#LBL_Povray_EDITOR",
    iconBase = "org/netbeans/modules/povray/file/favicon.png",
    mimeType = "text/x-povray",
    persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
    preferredID = "Povray",
    position = 1000)
    @Messages("LBL_Povray_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        MultiViewEditorElement e = new MultiViewEditorElement(lkp) {
            @Override
            public JEditorPane getEditorPane() {
                JEditorPane result = super.getEditorPane();
                System.err.println("SETTING UP EDITOR PANE");
                result.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, KeyEvent.CTRL_MASK), "toggle-comment");
                result.getActionMap().put("toggle-comment", new CommentOutAction());
                return result;
            }
        };
        return e;
    }

    @TemplateRegistration(folder = "Other", content = "PovrayTemplate.pov")
    public static WizardDescriptor.InstantiatingIterator templateIterator() {
        return null;
    }

    protected File getImageFile() {
        File dir = FileUtil.toFile(getPrimaryFile().getParent());
        String name = getPrimaryFile().getName() + ".png"; //XXX check all types
        return new File(dir, name);
    }
}
