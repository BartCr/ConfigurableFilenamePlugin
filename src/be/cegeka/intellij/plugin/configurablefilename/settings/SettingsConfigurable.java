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
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static be.cegeka.intellij.plugin.configurablefilename.messages.MessageBundle.message;

public class SettingsConfigurable implements Configurable {
    private final Project project;
    private SettingsPanel panel;

    public SettingsConfigurable(Project project) {
        this.project = project;
    }

    private class SettingsPanel extends JPanel {
        private final JBList filenameList;
        private final DefaultListModel<ConfigurableFilename> listModel;

        private boolean isModified = false;

        public SettingsPanel() {
            listModel = new DefaultListModel<ConfigurableFilename>();
            initializeListModel();
            filenameList = new JBList(listModel);
            filenameList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(((ConfigurableFilename) value).getType());
                    return this;

                }
            });
            setLayout(new BorderLayout());

            add(ToolbarDecorator.createDecorator(filenameList)
                    .setAddAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton anActionButton) {
                            addNew();
                        }
                    })
                    .setRemoveAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton anActionButton) {
                            removeSelected();
                        }
                    })
                    .setEditAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton anActionButton) {
                            editSelected();
                        }
                    })
                    .disableUpAction()
                    .disableDownAction()
                    .createPanel());
        }

        private void apply() {
            List<ConfigurableFilename> newList = new ArrayList<ConfigurableFilename>(listModel.getSize());
            Enumeration<ConfigurableFilename> elements = listModel.elements();
            while (elements.hasMoreElements()) {
                newList.add(elements.nextElement());
            }

            PluginSettings.getInstance(project).setConfiguration(newList);
            isModified = false;
        }

        private void reset() {
            listModel.removeAllElements();
            initializeListModel();
            isModified = false;
        }

        private void initializeListModel() {
            for (ConfigurableFilename filename : PluginSettings.getInstance(project).filenames) {
                listModel.add(listModel.size(), filename);
            }
        }

        private void addNew() {
            ConfigurableFilenameEditorDialog dlg = createConfigurableFilenameEditorDialog(MessageBundle.message("dialog.add.title"));
            ConfigurableFilename filename = new ConfigurableFilename();
            dlg.setData(filename);
            if (dlg.showAndGet()) {
                insertNewConfigurableFilename(dlg.getData(), true);
            }
            filenameList.requestFocus();
            isModified = true;
        }

        private void removeSelected() {
            int selectedIndex = filenameList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
            }

            filenameList.requestFocus();
            isModified = true;
        }

        private void editSelected() {
            int selectedIndex = filenameList.getSelectedIndex();
            if (selectedIndex != -1) {
                ConfigurableFilename filename = listModel.getElementAt(selectedIndex);
                ConfigurableFilenameEditorDialog dlg = createConfigurableFilenameEditorDialog(MessageBundle.message("dialog.edit.title"));
                dlg.setData(filename);
                if (dlg.showAndGet()) {
                    listModel.set(selectedIndex, dlg.getData());
                }
            }

            filenameList.requestFocus();
            isModified = true;
        }

        private ConfigurableFilenameEditorDialog createConfigurableFilenameEditorDialog(String title) {
            return new ConfigurableFilenameEditorDialog(panel, title);
        }

        private void insertNewConfigurableFilename(@NotNull ConfigurableFilename filename, boolean setSelected) {
            listModel.add(listModel.size(), filename);
            if (setSelected) filenameList.setSelectedValue(filename, true);
        }

    }

    @Nls
    @Override
    public String getDisplayName() {
        return message("configuration.display.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        // No help
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (panel == null) {
            panel = new SettingsPanel();
        }
        return panel;
    }

    @Override
    public boolean isModified() {
        return panel != null && panel.isModified;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (panel != null) {
            panel.apply();
        }
    }

    @Override
    public void reset() {
        if (panel != null) {
            panel.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        panel = null;
    }
}
