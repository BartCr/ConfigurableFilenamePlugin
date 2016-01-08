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

import be.cegeka.intellij.plugin.configurablefilename.CreateFileWithConfigurableNameAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "ConfigurableFilename",
        storages = {
                @Storage(file = StoragePathMacros.PROJECT_FILE),
                @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/configurable-filename.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class PluginSettings implements PersistentStateComponent<PluginSettings>, ProjectComponent {

    public List<ConfigurableFilename> filenames = new ArrayList<ConfigurableFilename>();

    @Nullable
    @Override
    public PluginSettings getState() {
        return this;
    }

    @Override
    public void loadState(PluginSettings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }

    public static PluginSettings getInstance(Project project) {
        return project.getComponent(PluginSettings.class);
    }

    private void addActions() {
        if (filenames != null && !filenames.isEmpty()) {
            ActionManager am = ActionManager.getInstance();
            DefaultActionGroup newGroup = (DefaultActionGroup) am.getAction("NewGroup");
            String anchorId = "NewFile";
            for (ConfigurableFilename filename : filenames) {
                CreateFileWithConfigurableNameAction action =
                        new CreateFileWithConfigurableNameAction(filename.getType(), filename.getDefaultExtension(), filename.getTemplate());
                String actionId = "ConfigurableFileName." + filename.getType();
                am.registerAction(actionId, action);
                newGroup.addAction(action, new Constraints(Anchor.AFTER, anchorId));
                anchorId = actionId;
            }
        }
    }

    public void setConfiguration(List<ConfigurableFilename> filenames) {
        removeActions();
        this.filenames = filenames;
        addActions();
    }

    public void removeActions() {
        ActionManager am = ActionManager.getInstance();
        DefaultActionGroup newGroup = (DefaultActionGroup) am.getAction("NewGroup");
        for (ConfigurableFilename filename : filenames) {
            AnAction action = am.getAction("ConfigurableFileName." + filename.getType());
            am.unregisterAction("ConfigurableFileName." + filename.getType());
            newGroup.remove(action);
        }
    }

    @Override
    public void projectOpened() {
        addActions();
    }

    @Override
    public void projectClosed() {
        removeActions();
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return  "ConfigurableFileNameSettings";
    }
}

