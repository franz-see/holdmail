/*******************************************************************************
 * Copyright 2016 - 2017 Sparta Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.spartasystems.holdmail.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;

import static com.spartasystems.holdmail.mime.MimeUtils.safeURLDecode;
import static com.spartasystems.holdmail.mime.MimeUtils.trimQuotes;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

/**
 * Represents a paramaterized mime header value, of the form:
 * <code>"value; p1key=p1val; p2key=p2val; ..."</code>
 */
public class HeaderValue {


    private final String value;
    private final Map<String, String> params = new HashMap<>();

    public HeaderValue(String valueString) {

        if (StringUtils.isBlank(valueString)) {
            value = valueString;
        } else {

            String[] parts = valueString.split(";");

            // the value is everything up to the first ';'
            value = trimQuotes(parts[0]);

            // all following parts are key=value parts
            stream(parts, 1, parts.length)
                    .map(ValueParam::parse)
                    .collect(groupingBy(ValueParam::getName))
                    .forEach((name, params) -> {

                        String concatenated = params.stream()
                                .sorted(comparingInt(ValueParam::getPosition))
                                .map(ValueParam::getValue).collect(joining(""));

                        String decoded = params.stream()
                                .sorted(comparingInt(ValueParam::getPosition))
                                .findFirst().map(ValueParam::getCharset)
                                .map(encoding -> safeURLDecode(concatenated, encoding))
                                .orElse(concatenated);

                        this.params.put(name, decoded);
                    });

        }

    }


    public boolean hasValue(String value) {
        return value != null && StringUtils.equals(this.value, value);
    }

    public String getValue() {
        return value;
    }

    public String getParamValue(String paramName) {
        return params.get(paramName);
    }

    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
