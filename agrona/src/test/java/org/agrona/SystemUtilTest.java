/*
 * Copyright 2014-2020 Real Logic Limited.
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
package org.agrona;

import org.junit.jupiter.api.Test;

import static org.agrona.SystemUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

public class SystemUtilTest
{
    @Test
    public void shouldParseSizesWithSuffix()
    {
        assertEquals(1L, parseSize("", "1"));
        assertEquals(1024L, parseSize("", "1k"));
        assertEquals(1024L, parseSize("", "1K"));
        assertEquals(1024L * 1024L, parseSize("", "1m"));
        assertEquals(1024L * 1024L, parseSize("", "1M"));
        assertEquals(1024L * 1024L * 1024L, parseSize("", "1g"));
        assertEquals(1024L * 1024L * 1024L, parseSize("", "1G"));
    }

    @Test
    public void shouldParseTimesWithSuffix()
    {
        assertEquals(1L, parseDuration("", "1"));
        assertEquals(1L, parseDuration("", "1ns"));
        assertEquals(1L, parseDuration("", "1NS"));
        assertEquals(1000L, parseDuration("", "1us"));
        assertEquals(1000L, parseDuration("", "1US"));
        assertEquals(1000L * 1000, parseDuration("", "1ms"));
        assertEquals(1000L * 1000, parseDuration("", "1MS"));
        assertEquals(1000L * 1000 * 1000, parseDuration("", "1s"));
        assertEquals(1000L * 1000 * 1000, parseDuration("", "1S"));
        assertEquals(12L * 1000 * 1000 * 1000, parseDuration("", "12S"));
    }

    @Test
    public void shouldThrowWhenParseTimeHasBadSuffix()
    {
        assertThrows(NumberFormatException.class, () -> parseDuration("", "1g"));
    }

    @Test
    public void shouldThrowWhenParseTimeHasBadTwoLetterSuffix()
    {
        assertThrows(NumberFormatException.class, () -> parseDuration("", "1zs"));
    }

    @Test
    public void shouldThrowWhenParseSizeOverflows()
    {
        assertThrows(NumberFormatException.class, () -> parseSize("", 8589934592L + "g"));
    }

    @Test
    public void shouldDoNothingToSystemPropsWhenLoadingFileWhichDoesNotExist()
    {
        final int originalSystemPropSize = System.getProperties().size();

        loadPropertiesFile("$unknown-file$");

        assertEquals(originalSystemPropSize, System.getProperties().size());
    }

    @Test
    public void shouldMergeMultiplePropFilesTogether()
    {
        assertThat(System.getProperty("TestFileA.foo"), is(emptyOrNullString()));
        assertThat(System.getProperty("TestFileB.foo"), is(emptyOrNullString()));

        loadPropertiesFiles("TestFileA.properties", "TestFileB.properties");

        assertEquals("AAA", System.getProperty("TestFileA.foo"));
        assertEquals("BBB", System.getProperty("TestFileB.foo"));
    }

    @Test
    public void shouldOverrideSystemPropertiesWithConfigFromPropFile()
    {
        System.setProperty("TestFileA.foo", "ToBeOverridden");
        assertEquals("ToBeOverridden", System.getProperty("TestFileA.foo"));

        loadPropertiesFile("TestFileA.properties");

        assertEquals("AAA", System.getProperty("TestFileA.foo"));

        System.clearProperty("TestFileA.foo");
    }

    @Test
    public void shouldNotOverrideSystemPropertiesWithConfigFromPropFile()
    {
        System.setProperty("TestFileA.foo", "ToBeNotOverridden");
        assertEquals("ToBeNotOverridden", System.getProperty("TestFileA.foo"));

        loadPropertiesFile(PropertyAction.PRESERVE, "TestFileA.properties");
        assertEquals("ToBeNotOverridden", System.getProperty("TestFileA.foo"));

        System.clearProperty("TestFileA.foo");
    }

    @Test
    void shouldReturnPid()
    {
        assertNotEquals(PID_NOT_FOUND, getPid());
    }
}
