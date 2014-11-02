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
package com.jdom.mediadownloader.services;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class HtmlUtil {
   private static final Logger LOG = LoggerFactory.getLogger(HtmlUtil.class);

   /**
    * Retrieve the contents of the URL.
    *
    * @param url the url
    * @return the contents as a string
    */
   public static String downloadUrlContents(String url) {
      URL u;
      try {
         u = new URL(url.replaceAll(" ", ""));
      } catch (MalformedURLException e) {
         throw new RuntimeException(e);
      }

      return downloadUrlContents(u);
   }

   /**
    * Retrieve the contents of the URL.
    *
    * @param url the url
    * @return the contents as a string
    */
   public static String downloadUrlContents(URL url) {

      if (LOG.isDebugEnabled()) {
         LOG.debug(String.format("Downloading URL [%s]", url.toString()));
      }

      DataInputStream is = null;
      HttpClient httpClient = new HttpClient();
      String[] urlParts = url.toString().split("\\?");

      GetMethod method = new GetMethod(urlParts[0]);

      if (urlParts.length == 2) {
         method.setQueryString(urlParts[1]);
      }

      StringBuilder builder = new StringBuilder();

      try {

         int statusCode = httpClient.executeMethod(method);

         if (statusCode != HttpStatus.SC_OK) {
            LOG.warn(String.format("Http retrieval returned unsuccessful for url [%s]: %s", url, method.getStatusLine()));
         }

         is = new DataInputStream(new BufferedInputStream(
               method.getResponseBodyAsStream()));

         byte[] bytes = ByteStreams.toByteArray(is);
         builder.append(new String(bytes));
      } catch (Exception e) {
         try {
            method.getResponseBody();
         } catch (Exception ignore) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Ignoring exception getting response body:",
                     ignore);
            }
         }
         LOG.error(
               "Exception downloading URL contents, aborting the method:",
               e);
         method.abort();
      } finally {
         method.releaseConnection();
         Closeables.closeQuietly(is);
      }

      return builder.toString();
   }

}
