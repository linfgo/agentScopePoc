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
package com.alibaba.cloud.ai.examples.multiagents.skills;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.memory.autocontext.AutoContextConfig;
import io.agentscope.core.memory.autocontext.AutoContextHook;
import io.agentscope.core.memory.autocontext.AutoContextMemory;
import io.agentscope.core.memory.autocontext.ContextOffloadTool;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.core.model.transport.HttpTransportConfig;
import io.agentscope.core.model.transport.HttpVersion;
import io.agentscope.core.model.transport.JdkHttpTransport;
import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.SkillBox;
import io.agentscope.core.skill.repository.ClasspathSkillRepository;
import io.agentscope.core.tool.Toolkit;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the SQL assistant agent using AgentScope skill support.
 *
 * <ul>
 *   <li>{@link ClasspathSkillRepository} loads skills from classpath {@code skills/}
 *       (each subdirectory with a {@code SKILL.md} is one skill).</li>
 *   <li>{@link SkillBox} holds a {@link Toolkit} and registers all loaded skills;
 *       the agent gets the skill system prompt and the read_skill / use_skill tools from the SkillBox.</li>
 *   <li>{@link ReActAgent} uses {@link DashScopeChatModel}, the toolkit, skillBox, and memory.</li>
 * </ul>
 *
 * <p>Progressive disclosure: the agent sees skill descriptions in the system prompt and loads
 * full skill content on demand via the skill tools.
 */
@Configuration
public class SkillsConfig {

    private static final String SYSTEM_PROMPT =
            """
            你是一个日志任务处理助手，擅长对收集上来的日志进行检查分析。
            """;

    @Bean
    public ClasspathSkillRepository skillRepository() throws IOException {
        return new ClasspathSkillRepository("skills");
    }

    @Bean
    public Toolkit toolkit() {
        return new Toolkit();
    }

    @Bean
    public SkillBox skillBox(Toolkit toolkit, ClasspathSkillRepository skillRepository) {
        SkillBox skillBox = new SkillBox(toolkit);
        skillBox.codeExecution()
                .workDir("/external/data-directory")  // 设置外部目录
                .withRead()                           // 注册view_text_file和list_directory工具
                .withWrite()                          // 注册write_text_file工具
                //.withShell()                          // 注册run_shell_command工具
                .enable();
        List<AgentSkill> skills = skillRepository.getAllSkills();
        for (AgentSkill skill : skills) {
            skillBox.registration().skill(skill).apply();
        }
        return skillBox;
    }

    /*@Bean
    public WebClient webClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .protocol(HttpProtocol.HTTP11)  // 固定协议版本
                        //.secure(ssl -> ssl.handshakeTimeout(Duration.ofSeconds(30)))
                ))
                .build();
    }*/

    @Bean("sqlAssistantAgent")
    public ReActAgent sqlAssistantAgent(Toolkit toolkit, SkillBox skillBox) {
        String key = System.getenv("AI_DASHSCOPE_API_KEY");
        String modelName = System.getenv("AI_DASHSCOPE_MODEL_NAME");

        Model model = DashScopeChatModel.builder()
                .apiKey(key)
                .modelName(modelName)
                .stream(false)
                .build();

        // 记忆
        // 配置
        AutoContextConfig config = AutoContextConfig.builder()
                .maxToken(100 * 1024)
                .tokenRatio(0.5)
                .minCompressionTokenThreshold(2000)
                .minConsecutiveToolMessages(2)
                .msgThreshold(5)
                .lastKeep(3)
                .currentRoundCompressionRatio(0.3)
                .largePayloadThreshold(3 * 1024)
                .build();

        // 创建内存
        AutoContextMemory memory = new AutoContextMemory(config, model);

        // 注册上下文重载工具
        toolkit.registerTool(new ContextOffloadTool(memory));


        return ReActAgent.builder()
                .name("超级助手")
                .sysPrompt(SYSTEM_PROMPT)
                .model(model)
                .toolkit(toolkit)
                .skillBox(skillBox)
                .memory(memory)
                .hook(new AutoContextHook())
                .build();
    }

    @Bean("openAiAgent")
    public ReActAgent openAiAgent(Toolkit toolkit, SkillBox skillBox) {
        // 1. 创建 HTTP/1.1 配置
        HttpTransportConfig transportConfig = HttpTransportConfig.builder()
                .httpVersion(HttpVersion.HTTP_1_1)  // 关键：解决 Connection:Upgrade 问题
                .build();

        // 2. 创建 Transport
        JdkHttpTransport transport = JdkHttpTransport.builder()
                .config(transportConfig)
                .build();

        Model model = OpenAIChatModel.builder()
                .baseUrl("http://10.10.40.102:32730/openapi/71c6900d-9d33-425b-b6c1-d4430e036c91")
                .apiKey("3adjsHChmB11DYaJs28upRnOCxE2v4y_TiOxnOFTJdo")
                .modelName("qwen_v3_5_122b_a10b")
                .httpTransport(transport)
                .stream(false)
                .build();

        // 记忆
        // 配置
        AutoContextConfig config = AutoContextConfig.builder()
                .maxToken(10 * 1024)
                .tokenRatio(0.5)
                .minConsecutiveToolMessages(2)
                .msgThreshold(2)
                .lastKeep(1)
                .tokenRatio(0.3)
                .currentRoundCompressionRatio(0.3)
                .build();

        // 创建内存
        AutoContextMemory memory = new AutoContextMemory(config, model);

        // 注册上下文重载工具
        toolkit.registerTool(new ContextOffloadTool(memory));

        //String key = System.getenv("AI_DASHSCOPE_API_KEY");
        //String modelName = System.getenv("AI_DASHSCOPE_MODEL_NAME");
        return ReActAgent.builder()
                .name("超级助手")
                .sysPrompt(SYSTEM_PROMPT)
                .model(model)
                .toolkit(toolkit)
                .skillBox(skillBox)
                .memory(memory)
                .hook(new AutoContextHook())
                .build();
    }
}
