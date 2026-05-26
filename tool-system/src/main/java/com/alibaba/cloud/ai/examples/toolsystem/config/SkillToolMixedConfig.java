/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.examples.toolsystem.config;

import com.alibaba.cloud.ai.examples.toolsystem.tool.CalculatorTools;
import com.alibaba.cloud.ai.examples.toolsystem.tool.FileReadTools;
import com.alibaba.cloud.ai.examples.toolsystem.tool.TimeQueryTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.Model;
import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.SkillBox;
import io.agentscope.core.skill.repository.ClasspathSkillRepository;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.List;

/**
 * Skill 与工具混合配置 - 演示在已有 Skill 系统基础上添加自定义工具
 *
 * @author agentscope-poc
 */
@Configuration
public class SkillToolMixedConfig {

    @Bean("mixedToolkit")
    public Toolkit mixedToolkit(CalculatorTools calculatorTools,
                                 TimeQueryTools timeQueryTools,
                                 FileReadTools fileReadTools) {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(calculatorTools);
        toolkit.registerTool(timeQueryTools);
        toolkit.registerTool(fileReadTools);
        return toolkit;
    }

    @Bean("mixedSkillBox")
    public SkillBox mixedSkillBox(@Lazy @Qualifier("mixedToolkit") Toolkit mixedToolkit) throws IOException {
        SkillBox skillBox = new SkillBox(mixedToolkit);

        // 启用 codeExecution 功能（内置文件操作等工具）
        skillBox.codeExecution()
                .workDir("/external/data-directory")
                .withRead()   // 注册 view_text_file 工具
                .withWrite()  // 注册 write_text_file 工具
                .enable();

        // 从 classpath:skills 加载 Skill（与主项目相同的技能目录）
        ClasspathSkillRepository skillRepository = new ClasspathSkillRepository("skills");
        List<AgentSkill> skills = skillRepository.getAllSkills();
        for (AgentSkill skill : skills) {
            skillBox.registration().skill(skill).apply();
        }

        return skillBox;
    }

    @Bean("mixedAgent")
    public ReActAgent mixedAgent(@Lazy @Qualifier("mixedToolkit") Toolkit mixedToolkit, @Lazy @Qualifier("mixedSkillBox") SkillBox mixedSkillBox) {
        String baseUrl = System.getenv("AI_DASHSCOPE_BASE_URL");
        String key = System.getenv("AI_DASHSCOPE_API_KEY");
        String modelName = System.getenv("AI_DASHSCOPE_MODEL_NAME");

        Model model = DashScopeChatModel.builder()
                .baseUrl(baseUrl != null ? baseUrl : "https://dashscope.aliyuncs.com/api/v1")
                .apiKey(key != null ? key : "your-api-key")
                .modelName(modelName != null ? modelName : "qwen-max")
                .stream(false)
                .build();

        return ReActAgent.builder()
                .name("Skill+Tool 混合 Agent")
                .sysPrompt("你是一个可以同时使用技能和工具的智能助手。你拥有自定义工具（计算器、时间查询、文件读取）以及系统技能。")
                .model(model)
                .toolkit(mixedToolkit)
                .skillBox(mixedSkillBox)
                .build();
    }
}
