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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 文件读取工具 - 支持读取 classpath 下的文件内容
 *
 * @author agentscope-poc
 */
@Component
public class FileReadTools {

    @Tool(name = "readFile", description = "读取指定路径的文件内容")
    public String readFile(@ToolParam(name = "filePath", description = "文件路径，相对于 classpath，如 test/sample.txt") String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            if (!resource.exists()) {
                return String.format("文件不存在：%s", filePath);
            }
            byte[] bytes = resource.getContentAsByteArray();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return String.format("读取文件失败：%s，错误：%s", filePath, e.getMessage());
        }
    }

    @Tool(name = "fileExists", description = "检查文件是否存在")
    public boolean fileExists(@ToolParam(name = "filePath", description = "文件路径，相对于 classpath") String filePath) {
        ClassPathResource resource = new ClassPathResource(filePath);
        return resource.exists();
    }

    @Tool(name = "getFileLength", description = "获取文件内容长度（字符数）")
    public int getFileLength(@ToolParam(name = "filePath", description = "文件路径，相对于 classpath") String filePath) {
        try {
            String content = readFile(filePath);
            if (content.startsWith("文件不存在") || content.startsWith("读取文件失败")) {
                return -1;
            }
            return content.length();
        } catch (Exception e) {
            return -1;
        }
    }
}
