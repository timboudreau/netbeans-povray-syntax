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

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Tim Boudreau
 */
class ImgPanel extends JPanel implements FileChangeListener {
    private final JScrollPane pane = new JScrollPane();
    private final JLabel imgLabel = new JLabel("[no image]");

    ImgPanel(PovrayDataObject dob) {
        this.dob = dob;
        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        pane.setViewportView(imgLabel);
        File f = dob.getImageFile();
        FileUtil.addFileChangeListener(this, f);
        change(f);
    }
    private final PovrayDataObject dob;

    private void change() {
        File f = dob.getImageFile();
        change(f);
    }

    private boolean init;
    private void change(File file) {
        if (file == null) {
            imgLabel.setIcon(null);
            imgLabel.setText("[no image]");
            return;
        }
        if (file != null && !init) {
            init = true;
            if (callback != null) {
                callback.requestVisible();
            }
        }
        try {
            BufferedImage img = ImageIO.read(file);
            imgLabel.setIcon(new ImageIcon(img));
            imgLabel.setText("");
        } catch (OutOfMemoryError err) {
            imgLabel.setIcon(null);
            imgLabel.setText("[insufficient memory]");
        } catch (IOException ex) {
            Logger.getLogger(ImgPanel.class.getName()).log(Level.INFO, null, ex);
        }
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        //do nothing
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        change();
    }

    @Override
    public void fileChanged(FileEvent fe) {
        change();
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        //            change();
    }

    @Override
    public void fileRenamed(FileRenameEvent fre) {
        //do nothing
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fae) {
        //do nothing
    }

    void setCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }
    private MultiViewElementCallback callback;

}
