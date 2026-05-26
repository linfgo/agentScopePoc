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
package com.alibaba.cloud.ai.examples.toolsystem.runner;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 工具系统测试入口 - 验证工具功能可用性
 *
 * @author agentscope-poc
 */
@Component
public class ToolSystemTestRunner implements ApplicationRunner {

    @Resource(name = "multiToolAgent")
    private ReActAgent multiToolAgent;

    @Resource(name = "mixedAgent")
    private ReActAgent mixedAgent;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("========================================");
        System.out.println("开始工具系统验证测试");
        System.out.println("========================================");

        // 测试用例 1：纯工具测试
        testPureTools();

        // 测试用例 2：纯 Skill 测试
        testPureSkill();

        // 测试用例 3：混合场景测试
        testMixedScenario();

        System.out.println("========================================");
        System.out.println("工具系统验证测试完成");
        System.out.println("========================================");
    }

    /**
     * 测试用例 1：纯工具测试
     * 验证自定义工具可以正常调用
     */
    private void testPureTools() {
        System.out.println("\n--- 测试用例 1：纯工具测试 ---");

        // 测试 1.1：计算器工具
        String query1 = "请计算 15 + 27 的结果";
        System.out.println("问题：" + query1);
        try {
            Msg userMsg1 = Msg.builder()
                    .role(MsgRole.USER)
                    .content(TextBlock.builder().text(query1).build())
                    .build();
            Msg response1 = multiToolAgent.call(userMsg1).block();
            System.out.println("回答：" + (response1 != null ? response1.getTextContent() : "无响应"));
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        }

        // 测试 1.2：时间查询工具
        System.out.println("\n问题：现在纽约时间是多少？");
        try {
            Msg userMsg2 = Msg.builder()
                    .role(MsgRole.USER)
                    .content(TextBlock.builder().text("现在纽约时间是多少？").build())
                    .build();
            Msg response2 = multiToolAgent.call(userMsg2).block();
            System.out.println("回答：" + (response2 != null ? response2.getTextContent() : "无响应"));
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        }

        // 测试 1.3：组合工具调用
        System.out.println("\n问题：先计算 100 - 35，然后告诉我当前北京时间");
        try {
            Msg userMsg3 = Msg.builder()
                    .role(MsgRole.USER)
                    .content(TextBlock.builder().text("先计算 100 - 35，然后告诉我当前北京时间").build())
                    .build();
            Msg response3 = multiToolAgent.call(userMsg3).block();
            System.out.println("回答：" + (response3 != null ? response3.getTextContent() : "无响应"));
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        }
    }

    /**
     * 测试用例 2：纯 Skill 测试
     * 验证 Skill 功能正常
     */
    private void testPureSkill() {
        System.out.println("\n--- 测试用例 2：纯 Skill 测试 ---");

        // 测试 Skill 调用（使用现有的 data-security-alert-analyzer 技能）
        String query = "使用 data-security-alert-analyzer 分析安全日志";
        System.out.println("问题：" + query);
        try {
            Msg userMsg = Msg.builder()
                    .role(MsgRole.USER)
                    .content(TextBlock.builder().text(query).build())
                    .build();
            Msg response = mixedAgent.call(userMsg).block();
            System.out.println("回答：" + (response != null ? response.getTextContent() : "无响应"));
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        }
    }

    /**
     * 测试用例 3：混合场景测试
     * 验证 Skill 与 Tool 协同工作
     */
    private void testMixedScenario() {
        System.out.println("\n--- 测试用例 3：混合场景测试 ---");

        // 测试 Skill 和 Tool 协同
        String query = "先用计算器计算 256 除以 8 的结果，然后用 analysis-skill 生成分析报告";
        System.out.println("问题：" + query);
        try {
            Msg userMsg = Msg.builder()
                    .role(MsgRole.USER)
                    .content(TextBlock.builder().text(query).build())
                    .build();
            Msg response = mixedAgent.call(userMsg).block();
            System.out.println("回答：" + (response != null ? response.getTextContent() : "无响应"));
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        }

        // 测试文件读取 + 分析
        System.out.println("\n问题：读取 test/sample.txt 文件内容并分析");
        try {
            Msg userMsg = Msg.builder()
                    .role(MsgRole.USER)
                    .content(TextBlock.builder().text("读取 test/sample.txt 文件内容并分析").build())
                    .build();
            Msg response = mixedAgent.call(userMsg).block();
            System.out.println("回答：" + (response != null ? response.getTextContent() : "无响应"));
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        }
    }
}
