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
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 多工具绑定配置 - 演示如何将多个自定义工具注册到同一个 Agent
 *
 * @author agentscope-poc
 */
@Configuration
public class ToolSystemConfig {

    @Bean("multiToolToolkit")
    public Toolkit multiToolToolkit(CalculatorTools calculatorTools,
                                     TimeQueryTools timeQueryTools,
                                     FileReadTools fileReadTools) {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(calculatorTools);
        toolkit.registerTool(timeQueryTools);
        toolkit.registerTool(fileReadTools);
        return toolkit;
    }

    @Bean("multiToolAgent")
    public ReActAgent multiToolAgent(@Lazy @Qualifier("multiToolToolkit") Toolkit multiToolToolkit) {
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
                .name("多工具 Agent")
                .sysPrompt("你是一个可以使用各种工具的智能助手。你拥有计算器、时间查询和文件读取工具。")
                .model(model)
                .toolkit(multiToolToolkit)
                .build();
    }
}
