package com.dhf.plugin.mojo;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/4.
 */

/**
 * 由于历史原因，使用1.4的的注解风格，将注解写在1.4中，每个Mojo都需要声明自己的goal，相当于插件jetty中的run
 * 也就是说每个插件的goal其实都是一个Mojo，每个Mojo的实现对应goal的实现
 * 该Mojo用于统计所有Java、XML、Properties文件，并允许用户配置希望统计哪些类型的文件
 * phase表示goal默认绑定的生命周期阶段，还有其他的可用注解有：
 * requiresDependencyResolution <scope>运行该Mojo前必须解析所指定范围的依赖，如maven-surefire-plugin就有@requiresDependencyResolution test
 * 表示执行前所有测试范围的依赖都需要解析，还有的scope值有compile、runtime，默认runtime
 * requiresProject <true/false> 是否需要在某个maven目录下运行，大部分插件都为true，少部分如maven-help-plugin的system为false
 * requiresDirectInvocation <true/false> 表示是否只能在命令行运行，如果为true则在pom中将该Mojo绑定到生命周期会报错，默认为false
 * requiresOnline <true/false> maven是否需要在线状态，默认false
 * requireReport <true/false> 是否需要项目报告已生成，默认false
 * aggregate 如果加了该注解表示在多模块项目中，Mojo只运行在顶层模块
 * execute goal = "<goal>" 运行该goal前先运行另一个goal，如goal为本插件的goal则直接指定goal，否则用prefix:goal
 * execute phase = "<phase>" 运行该goal前先让maven运行到phase生命周期
 * execute lifecycle = "<lifecycle>" phase = "<phase>" 运行该goal前运行一个自定义的生命周期到指定的阶段
 * @phase verify
 * @goal count
 */
public class CountMojo extends AbstractMojo {
    /**
     * 表示哪些类型是默认配置的，即
     * <includes>
     * <include>java</include>
     * <include>xml</include>
     * <include>properties</include>
     * </includes>
     */
    private static final String[] INCLUDES_DEFAULT = {"java", "xml", "properties"};

    /**
     * parameter表示该字段是插件的一个参数，expression表示该字段的值，可以为project.basedir这样的build in值，或如maven.test.skip，
     * 这样可以在命令行用-Dmaven.test.skip指定该值或用pom中的properties和filtering，readonly表示用户不可更改值，如果没有
     * readonly则通过<baseDir>path to file</baseDir>进行配置parameter后可以跟一个alias="xxx"表示别名，则可以用<xxx></xxx>来配置
     * parameter后跟default-value表示默认值
     *
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourceDirectory;

    /**
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     * @readonly
     */
    private File testSourceDirectory;

    /**
     * @parameter expression="${project.build.resources}"
     * @required
     * @readonly
     */
    private List<Resource> resources;

    /**
     * @parameter expression="${project.build.testResources}"
     * @required
     * @readonly
     */
    private List<Resource> testResources;

    /**
     * 表示需要统计的文件类型，如果不指定则使用INCLUDES_DEFAULT
     * The file types which will be included for counting
     *
     * @parameter
     */
    private String[] includes;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (includes == null || includes.length == 0) {
            includes = INCLUDES_DEFAULT;
        }

        try {
            countDir(sourceDirectory);
            countDir(testSourceDirectory);

            for (Resource resource : resources) {
                countDir(new File(resource.getDirectory()));
            }
            for (Resource resource : testResources) {
                countDir(new File(resource.getDirectory()));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to count lines of code.", e);
        }
    }

    //计算某个目录下文件行数
    private void countDir(File dir)
            throws IOException {
        if (!dir.exists()) {
            return;
        }

        List<File> collected = new ArrayList<File>();
        collectFiles(collected, dir);

        int lines = 0;
        for (File sourceFile : collected) {
            lines += countLine(sourceFile);
        }

        String path = dir.getAbsolutePath().substring(basedir.getAbsolutePath().length());
        getLog().info(path + ": " + lines + " lines of code in " + collected.size() + " files");
    }

    private void collectFiles(List<File> collected, File file) {
        if (file.isFile()) {
            for (String include : includes) {
                if (file.getName().endsWith("." + include)) {
                    collected.add(file);
                    break;
                }
            }
        } else {
            for (File sub : file.listFiles()) {
                collectFiles(collected, sub);
            }
        }
    }

    private int countLine(File file)
            throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        int line = 0;
        try {
            while (reader.ready()) {
                reader.readLine();
                line++;
            }
        } finally {
            reader.close();
        }
        return line;
    }
}
