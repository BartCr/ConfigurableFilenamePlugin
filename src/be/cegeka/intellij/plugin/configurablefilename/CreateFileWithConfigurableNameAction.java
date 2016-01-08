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

package be.cegeka.intellij.plugin.configurablefilename;

import be.cegeka.intellij.plugin.configurablefilename.messages.MessageBundle;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ComponentNotRegistered")
public class CreateFileWithConfigurableNameAction extends CreateFileAction {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    private final String type;
    private final String defaultExtension;
    private final String template;

    public CreateFileWithConfigurableNameAction(String type, String defaultExtension, String template) {
        super(type + " file", MessageBundle.message("action.create.new.file.description", type), StdFileTypes.PLAIN_TEXT.getIcon());
        this.type = type;
        this.defaultExtension = defaultExtension;
        this.template = template;
    }

    @Override
    public boolean isDumbAware() {
        return CreateFileWithConfigurableNameAction.class.equals(getClass());
    }

    @NotNull
    @Override
    protected PsiElement[] invokeDialog(Project project, PsiDirectory directory) {
        MyInputValidator validator = new MyValidator(project, directory);
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            try {
                return validator.create("test");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            Messages.showInputDialog(project, IdeBundle.message("prompt.enter.new.file.name"),
                    MessageBundle.message("title.new.file", type), null, null, validator);
            return validator.getCreatedElements();
        }
    }

    @Nullable
    @Override
    protected String getDefaultExtension() {
        return defaultExtension;
    }

    @NotNull
    @Override
    protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
        CreateFileAction.MkDirs mkdirs = new CreateFileAction.MkDirs(newName, directory);
        return new PsiElement[]{mkdirs.directory.createFile(generateFileName(getFileName(mkdirs.newName)))};
    }

    @Override
    protected String getCommandName() {
        return MessageBundle.message("command.create.file", type);
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName) {
        return MessageBundle.message("progress.creating.file", type, directory.getVirtualFile().getPresentableUrl(), File.separator, newName);
    }

    private String generateFileName(String newFileName) {
        String result = template;

        Matcher matcher = VARIABLE_PATTERN.matcher(result);
        Date timestamp = new Date();
        while (matcher.find()) {
            String variable = matcher.group(1);
            String replacement = "";

            if ("NOW".equals(variable)) {
                replacement = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(timestamp);
            } else if ("USER".equals(variable)) {
                replacement = System.getProperty("user.name");
            } else if ("NAME".equals(variable)) {
                replacement = newFileName;
            } else if (variable.startsWith("NOW;")) {
                replacement = new SimpleDateFormat(variable.substring(4)).format(timestamp);
            }
            result = matcher.replaceFirst(replacement);
            matcher.reset(result);
        }
        return result;
    }
}
