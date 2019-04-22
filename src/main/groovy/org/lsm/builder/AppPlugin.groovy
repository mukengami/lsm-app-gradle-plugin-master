package org.lsm.builder

import org.gradle.api.*

class AppPlugin implements Plugin<Project> {
    
    def void apply(project) {
        project.extensions.create("lsm", Configuration)
        loadPluginConfiguration(project)
        addTasks(project)
    }
    
    def addTasks(project) {
        def helper = new TaskHelper()
        project.task('printConfigPath') {
            description = "Print the path to the configuration as determined by the lsm.properties file"
        } << {
            println "Your current configuration path is " + project.lsm.dir
        }

        project.task('printConfigFiles') {
            description = "Print the files that will combine into the current configuration"
        } << {
            helper.printConfigFiles(project)
        }

        project.task('printConfig') {
            description = "Print the current configuration"
        } << {
            helper.printConfig(project)
        }

        project.task('format') {
            description = "Formats the source according to the project standard"
        } << {
            def files = project.lsm.activeConfig.getSrcFiles(project)
            helper.format(project, files)
        }
    }
    private def loadPluginConfiguration(project) {
        // first find out where the more specific properties are
        try {
            def configFile = project.file('lsm.properties')
            if (configFile.exists()) {
                project.apply from: configFile
            }
        }
        catch(GradleException e) {
            println "Could not load lsm.properties, using default configuration"
        }

        // then actually load them
        def configPath = project.uri(project.lsm.dir + '/' + 'lsm.properties')
        try {
            project.apply from: configPath
        }
        catch(GradleException e) {
            println "Configuration in " + configPath + " is malformed."
            throw e
        }
    }


}
