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
 */package com.jdom.util.j2ee.ejb;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBUtils {

    public static final String EAR_NAME = "MEDIASERVER";

    static final String REMOTE = "remote";

    static final String LOCAL = "local";

    static ContextLookupStrategy contextLookup = new NewContextLookupStrategy();

    /**
     * Retrieve a remote ejb reference.
     * 
     * @param ejb
     *            the bean class
     * @return the looked up object
     */
    public static Object getRemoteEjb(Class<?> ejb) {
        return getEjb(ejb, true);
    }

    /**
     * Retrieve a local ejb reference.
     * 
     * @param ejb
     *            the bean class
     * @return the looked up object
     */
    public static Object getLocalEjb(Class<?> ejb) {
        return getEjb(ejb, false);
    }

    /**
     * Retrieve a remote ejb reference.
     * 
     * @param ejb
     *            the bean class
     * @param remote
     *            true if it should be a remote lookup
     * @return the looked up object
     */
    private static Object getEjb(Class<?> ejb, boolean remote) {
        String localOrRemote = (remote) ? REMOTE : LOCAL;

        return lookup(getJndi(ejb, localOrRemote));
    }

    public static Object lookup(String jndi) {
        InitialContext ctx = null;
        Object retVal = null;

        try {
            ctx = contextLookup.getInitialContext();

            retVal = ctx.lookup(jndi);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeContext(ctx);
        }

        return retVal;
    }

    /**
     * Close the context.
     * 
     * @param ctx
     */
    public static void closeContext(Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (Exception e) {
                // Nothing we can do about it
            }
        }
    }

    private static String getJndi(Class<?> ejb, String remoteOrLocal) {
        return EAR_NAME + "/" + ejb.getSimpleName() + "/" + remoteOrLocal;
    }

    static interface ContextLookupStrategy {
        InitialContext getInitialContext() throws NamingException;
    }

    static class NewContextLookupStrategy implements ContextLookupStrategy {

        @Override
        public InitialContext getInitialContext() throws NamingException {
            return new InitialContext();
        }
    }
}
