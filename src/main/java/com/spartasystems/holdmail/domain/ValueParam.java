/*******************************************************************************
 * Copyright 2017 Sparta Systems, Inc
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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.spartasystems.holdmail.mime.MimeUtils.trimQuotes;

/**
 * TODO: doc and test
 */
class ValueParam {

    /*
        valid RFC2231 name patterns:
            blah* - not split, but encoded
            blah*123 = split, position=123, not encoded
            blah*123* = split, position=123, encoded
    */
    private static final Pattern RFC2231_NAME_PATT = Pattern.compile("^(?<name>[^*]+)\\*((?<position>\\d+)\\*?)?$");
    /*
        valid RFC2231 value patterns:
            blah
            'encoding'en'blah
            'encoding''blah
            ''en'blah
            ''blah
     */
    private static final Pattern RFC2231_VALUE_PATTERN = Pattern.compile("^((?<charset>.*?)?'(?<language>.*?)?')?(?<value>.*)$");

    private String name;
    private String value;
    private int position;
    private String charset;
    private String language;

    public static ValueParam parse(String nameValueString) {

        String[] nameValuePair = nameValueString.split("=");
        String rawName = trimQuotes(nameValuePair[0]);
        String rawValue = nameValuePair.length < 2 ? "" : trimQuotes(nameValuePair[1]);

        Matcher nameMatcher = RFC2231_NAME_PATT.matcher(rawName);
        if (!nameMatcher.matches()) {
            return new ValueParam(rawName, rawValue, 0, null, null);
        }

        int position = 0;
        if (nameMatcher.group("position") != null) {
            position = Integer.parseInt(nameMatcher.group("position"));
        }

        String name = nameMatcher.group("name");
        String value = rawValue;
        String charset = null;
        String lang = null;

        // the presence of a trailing '*' indicates whether to attempt to parse lang/charset info
        if (rawName.endsWith("*")) {

            Matcher valueMatcher = RFC2231_VALUE_PATTERN.matcher(value);
            if (valueMatcher.matches()) {
                value = valueMatcher.group("value");
                charset = valueMatcher.group("charset");
                lang = valueMatcher.group("language");
            }

        }

        return new ValueParam(name, value, position, charset, lang);
    }

    private ValueParam(String name, String value, int position, String charset, String language) {
        this.name = name;
        this.value = value;
        this.position = position;
        this.charset = charset;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }

    public String getCharset() {
        return charset;
    }

    public String getLanguage() {
        return language;
    }


    @Override
    public String toString() {
        return "ValueParam{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", position=" + position +
                ", charset='" + charset + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
