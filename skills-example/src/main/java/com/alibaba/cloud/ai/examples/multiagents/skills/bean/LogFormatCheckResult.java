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
package com.alibaba.cloud.ai.examples.multiagents.skills.bean;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志格式检查结果Bean，用于表示日志格式检查的输出结果。
 */
public class LogFormatCheckResult {

    private String conclusion;
    private String specFile;
    private String logFile;
    private LocalDateTime checkedAt;
    private Summary summary;
    private List<Issue> issues;

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getSpecFile() {
        return specFile;
    }

    public void setSpecFile(String specFile) {
        this.specFile = specFile;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    /**
     * 检查结果摘要统计信息。
     */
    public static class Summary {

        private int totalEntries;
        private int totalIssues;
        private IssueTypes issueTypes;

        public int getTotalEntries() {
            return totalEntries;
        }

        public void setTotalEntries(int totalEntries) {
            this.totalEntries = totalEntries;
        }

        public int getTotalIssues() {
            return totalIssues;
        }

        public void setTotalIssues(int totalIssues) {
            this.totalIssues = totalIssues;
        }

        public IssueTypes getIssueTypes() {
            return issueTypes;
        }

        public void setIssueTypes(IssueTypes issueTypes) {
            this.issueTypes = issueTypes;
        }
    }

    /**
     * 问题类型统计。
     */
    public static class IssueTypes {

        private int missingField;
        private int typeMismatch;
        private int formatViolation;

        public int getMissingField() {
            return missingField;
        }

        public void setMissingField(int missingField) {
            this.missingField = missingField;
        }

        public int getTypeMismatch() {
            return typeMismatch;
        }

        public void setTypeMismatch(int typeMismatch) {
            this.typeMismatch = typeMismatch;
        }

        public int getFormatViolation() {
            return formatViolation;
        }

        public void setFormatViolation(int formatViolation) {
            this.formatViolation = formatViolation;
        }
    }

    /**
     * 具体问题详情。
     */
    public static class Issue {

        private int id;
        private Location location;
        private String description;
        private String violatedRule;
        private String suggestion;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getViolatedRule() {
            return violatedRule;
        }

        public void setViolatedRule(String violatedRule) {
            this.violatedRule = violatedRule;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }

    /**
     * 问题所在位置。
     */
    public static class Location {

        private String entry;
        private String field;

        public String getEntry() {
            return entry;
        }

        public void setEntry(String entry) {
            this.entry = entry;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}