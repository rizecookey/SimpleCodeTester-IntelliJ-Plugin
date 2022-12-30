package de.mr_pine.simplecodetesterplugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import de.mr_pine.simplecodetesterplugin.CodeTester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CodeTesterSubmitAllAction : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { project ->
            val sourceRoot = ModuleRootManager.getInstance(ModuleManager.getInstance(project).modules[0]).sourceRoots[0]
            val fileList = mutableListOf<VirtualFile>()
            VfsUtilCore.iterateChildrenRecursively(
                sourceRoot,
                { true },
                { file ->
                    if(!file.isDirectory && (file.extension ?: "") == "java") fileList.add(file)
                    true
                }
            )

            CoroutineScope(Job() + Dispatchers.IO).launch {
                CodeTester.submitFiles(files = fileList)
            }

            LOG.info(fileList.toString())
        }
    }

    private val icon = AllIcons.Actions.Upload
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
        event.presentation.icon = icon
    }

    companion object {
        private val LOG = Logger.getInstance(CodeTesterSubmitAllAction::class.java).apply {
            setLevel(LogLevel.DEBUG)
        }
    }
}