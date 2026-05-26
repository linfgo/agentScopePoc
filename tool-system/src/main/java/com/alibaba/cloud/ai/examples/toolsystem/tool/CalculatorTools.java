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
package com.alibaba.cloud.ai.examples.toolsystem.tool;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 计算器工具 - 支持基本的四则运算
 *
 * @author agentscope-poc
 */
@Component
public class CalculatorTools {

    @Tool(name = "add", description = "执行加法运算，返回两个数的和")
    public double add(@ToolParam(name = "a", description = "第一个加数") double a,
                      @ToolParam(name = "b", description = "第二个加数") double b) {
        return a + b;
    }

    @Tool(name = "subtract", description = "执行减法运算，返回两个数的差")
    public double subtract(@ToolParam(name = "a", description = "被减数") double a,
                           @ToolParam(name = "b", description = "减数") double b) {
        return a - b;
    }

    @Tool(name = "multiply", description = "执行乘法运算，返回两个数的积")
    public double multiply(@ToolParam(name = "a", description = "第一个乘数") double a,
                           @ToolParam(name = "b", description = "第二个乘数") double b) {
        return a * b;
    }

    @Tool(name = "divide", description = "执行除法运算，返回两个数的商")
    public double divide(@ToolParam(name = "a", description = "被除数") double a,
                         @ToolParam(name = "b", description = "除数") double b) {
        if (b == 0) {
            throw new IllegalArgumentException("除数不能为零");
        }
        return a / b;
    }
}
