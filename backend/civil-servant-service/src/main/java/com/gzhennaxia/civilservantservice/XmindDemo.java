package com.gzhennaxia.civilservantservice;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.parsers.StoryParser;
import org.xmind.core.*;

import java.io.File;
import java.io.IOException;
import java.lang.module.Configuration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: li.bo
 * @date: 2025/9/23 18:43
 * @version: v1.0
 */
public class XmindDemo {
    public static void main(String[] args) throws Exception {

        // 创建生成器
        XMindGenerator generator = new XMindGenerator();

        // 构建中心主题
        Topic centerTopic = new Topic("Java学习路径");

        // 添加一级子主题
        Topic basicTopic = new Topic("基础语法");
        basicTopic.addChild(new Topic("变量与类型"));
        basicTopic.addChild(new Topic("控制流"));

        Topic oopTopic = new Topic("面向对象");
        oopTopic.addChild(new Topic("类与对象"));
        oopTopic.addChild(new Topic("继承与多态"));

        // 将子主题添加到中心主题
        centerTopic.addChild(basicTopic);
        centerTopic.addChild(oopTopic);

        // 生成XMind文件
        generator.generate(centerTopic, "java_learning_path.xmind");
        System.out.println("XMind文件生成完成！");
    }
    }
}
