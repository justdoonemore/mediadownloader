/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.mediadownloader.series.download;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * Finds all matching sections of a text of input given a specific regex pattern.
     * 
     * @param input
     *            the input text
     * @param regex
     *            the regex pattern
     * @return the matches
     */
    public static Collection<RegexMatch> findRegexMatches(String input, String regex) {
        // The collection of matches
        Collection<RegexMatch> matches = new ArrayList<RegexMatch>();

        // Create a pattern
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

        // Look for matches
        Matcher matcher = pattern.matcher(input);

        // Loop to find all matches in the given contents
        while (matcher.find()) {

            String match = matcher.group();
            String[] groups = new String[matcher.groupCount() + 1];

            for (int i = 0; i <= matcher.groupCount(); i++) {
                groups[i] = matcher.group(i);
            }

            matches.add(new RegexMatch(match, groups));
        }

        return matches;
    }

    /**
     * Return a compiled pattern with multiline, dot representing all characters, and case
     * insensitive.
     * 
     * @param pattern
     *            The string pattern
     * @return The compiled pattern
     */
    public static Pattern getCompiledPattern(String pattern) {
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    }
}
