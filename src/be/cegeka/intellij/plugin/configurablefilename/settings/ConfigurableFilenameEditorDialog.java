/*
 *  Copyright (c) 2016 Bart Cremers
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package be.cegeka.intellij.plugin.configurablefilename.settings;

import be.cegeka.intellij.plugin.configurablefilename.messages.MessageBundle;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ConfigurableFilenameEditorDialog extends DialogWrapper {

    private final JTextField nameField = new JTextField(15);
    private final JTextField extensionField = new JTextField(5);
    private final JTextField templateField = new JTextField(30);

    public ConfigurableFilenameEditorDialog(JComponent parent, String title) {
        super(parent, true);
        setTitle(title);
        init();
    }

    @Override
    public void show() {
        super.show();
        nameField.requestFocus();
    }

    public ConfigurableFilename getData() {
        ConfigurableFilename filename = new ConfigurableFilename();

        filename.setType(convertString(nameField.getText()));
        filename.setDefaultExtension(convertString(extensionField.getText()));
        filename.setTemplate(convertString(templateField.getText()));

        return filename;
    }

    public void setData(ConfigurableFilename filename) {
        nameField.setText(filename.getType());
        extensionField.setText(filename.getDefaultExtension());
        templateField.setText(filename.getTemplate());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constr;

        // name
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 0;
        constr.anchor = GridBagConstraints.WEST;
        constr.insets = new Insets(5, 0, 0, 0);
        panel.add(new JLabel(MessageBundle.message("dialog.name.label")), constr);

        constr = new GridBagConstraints();
        constr.gridx = 1;
        constr.gridy = 0;
        constr.weightx = 1;
        constr.insets = new Insets(5, 10, 0, 0);
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.anchor = GridBagConstraints.WEST;
        panel.add(nameField, constr);

        // extension
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 1;
        constr.anchor = GridBagConstraints.WEST;
        constr.insets = new Insets(5, 0, 0, 0);
        panel.add(new JLabel(MessageBundle.message("dialog.extension.label")), constr);

        constr = new GridBagConstraints();
        constr.gridx = 1;
        constr.gridy = 1;
        constr.insets = new Insets(5, 10, 0, 0);
        constr.anchor = GridBagConstraints.WEST;
        panel.add(extensionField, constr);

        // template
        constr = new GridBagConstraints();
        constr.gridx = 0;
        constr.gridy = 2;
        constr.anchor = GridBagConstraints.WEST;
        constr.insets = new Insets(5, 0, 0, 0);
        panel.add(new JLabel(MessageBundle.message("dialog.template.label")), constr);

        constr = new GridBagConstraints();
        constr.gridx = 1;
        constr.gridy = 2;
        constr.weightx = 1;
        constr.insets = new Insets(5, 10, 0, 0);
        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.anchor = GridBagConstraints.WEST;
        panel.add(templateField, constr);

        return panel;
    }

    private String convertString(String s) {
        if (s != null && s.trim().isEmpty()) return null;
        return s;
    }
}
