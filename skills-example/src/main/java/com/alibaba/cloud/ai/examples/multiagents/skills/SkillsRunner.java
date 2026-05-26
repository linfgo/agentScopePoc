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

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.ai.examples.multiagents.skills.bean.LogFormatCheckResult;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Runs a short demo of the skills (progressive disclosure) agent when
 * {@code skills.runner.enabled=true}. Uses AgentScope ReActAgent: builds a user Msg,
 * calls {@code agent.call(userMsg).block()}, and logs the assistant text from {@link Msg#getTextContent()}.
 */
@Component
@ConditionalOnProperty(name = "skills.runner.enabled", havingValue = "true")
public class SkillsRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SkillsRunner.class);

    private final ReActAgent sqlAssistantAgent;

    public SkillsRunner(ReActAgent sqlAssistantAgent) {
        this.sqlAssistantAgent = sqlAssistantAgent;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        ReActAgent openAiAgent = SpringUtil.getBean("openAiAgent");
        String desc_content = FileUtil.readString(new File("K:\\pyWorkSpace\\langChainStudy1\\src\\描述文件\\desc.md"), StandardCharsets.UTF_8);
        String log_content = FileUtil.readString(new File("K:\\pyWorkSpace\\langChainStudy1\\src\\日志文件\\登录日志-zzz.txt"), StandardCharsets.UTF_8);

        /*String query =
                "通过log-format-checker判断其是否合规,描述文件内容:"+ desc_content + ";日志内容:"+log_content+",最终输出一个markdown格式的报告;";*/
        /*String query =
                "通过log-openAiAgent-checker判断其是否合规,读取目录下的描述文件路径:desc.md;日志文件登录日志-zzz.txt,最终输出报告报告;";*/
        String query =
                "通过log-format-checker判断其是否合规,读取待分析数据安全日志目录下的三个文件，通过data-security-alert-analyzer进行分析，输出json和md报告在res2目录下,直接执行完成，不要问任何问题";
        /*String query = "现在有什么skill可以用";*/
        log.info("User: {}", query);
        Msg userMsg =
                Msg.builder()
                        .role(MsgRole.USER)
                        .content(TextBlock.builder().text(query).build())
                        .build();
        Msg response = sqlAssistantAgent.call(userMsg).block();
        String text = response != null ? response.getTextContent() : "";
        log.info("Assistant: {}", text);
    }
}
