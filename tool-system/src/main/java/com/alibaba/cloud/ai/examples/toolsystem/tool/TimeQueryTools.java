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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间查询工具 - 支持多时区时间查询
 *
 * @author agentscope-poc
 */
@Component
public class TimeQueryTools {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool(name = "queryCurrentTime", description = "查询指定时区的当前时间")
    public String queryCurrentTime(@ToolParam(name = "timezone", description = "时区名称，如 Asia/Shanghai, America/New_York, Europe/London") String timezone) {
        try {
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            return String.format("时区 %s 的当前时间是：%s", timezone, zonedDateTime.format(DEFAULT_FORMATTER));
        } catch (Exception e) {
            return String.format("无效的时区：%s，错误：%s", timezone, e.getMessage());
        }
    }

    @Tool(name = "queryTimeWithFormat", description = "查询指定时区并使用指定格式的时间")
    public String queryTimeWithFormat(@ToolParam(name = "timezone", description = "时区名称，如 Asia/Shanghai") String timezone,
                                      @ToolParam(name = "format", description = "时间格式，如 yyyy-MM-dd HH:mm:ss 或 yyyy 年 MM 月 dd 日") String format) {
        try {
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return String.format("时区 %s 的时间（格式：%s）：%s", timezone, format, zonedDateTime.format(formatter));
        } catch (Exception e) {
            return String.format("查询失败：%s", e.getMessage());
        }
    }

    @Tool(name = "queryDefaultTime", description = "获取系统默认时区的当前时间")
    public String queryDefaultTime() {
        String timezone = ZoneId.systemDefault().getId();
        String time = LocalDateTime.now().format(DEFAULT_FORMATTER);
        return String.format("系统默认时区 %s 的当前时间是：%s", timezone, time);
    }
}
