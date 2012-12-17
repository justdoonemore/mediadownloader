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

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jdom.util.j2ee.ejb.EJBUtils;
import com.jdom.util.j2ee.ejb.EJBUtils.ContextLookupStrategy;

@RunWith(JMock.class)
public class EJBUtilsTest {

	static final Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);

		}
	};

	static final InitialContext ctx = context.mock(InitialContext.class);

	@BeforeClass
	public static void beforeClass() {
		EJBUtils.contextLookup = new FakeContextLookupStrategy();
	}

	@Test
	public void testGetRemoteEjbPerformsLookupAndClosesContext()
			throws NamingException {

		context.checking(new Expectations() {
			{
				oneOf(ctx).lookup(
						EJBUtils.EAR_NAME + "/" + List.class.getSimpleName()
								+ "/" + "remote");
				oneOf(ctx).close();
			}
		});

		EJBUtils.getRemoteEjb(List.class);
	}

	@Test
	public void testGetRemoteEjbClosesContextWhenExceptionThrown()
			throws NamingException {

		context.checking(new Expectations() {
			{
				oneOf(ctx).lookup(with(any(String.class)));
				will(throwException(new NamingException("test exception thrown")));
				oneOf(ctx).close();
			}
		});

		EJBUtils.getRemoteEjb(List.class);
	}

	@Test
	public void testGetLocalEjb() throws NamingException {
		context.checking(new Expectations() {
			{
				oneOf(ctx).lookup(
						EJBUtils.EAR_NAME + "/" + List.class.getSimpleName()
								+ "/" + "local");
				oneOf(ctx).close();
			}
		});

		EJBUtils.getLocalEjb(List.class);
	}

	@Test
	public void testLocalEjbClosesContextWhenExceptionThrown()
			throws NamingException {

		context.checking(new Expectations() {
			{
				oneOf(ctx).lookup(with(any(String.class)));
				will(throwException(new NamingException("test exception thrown")));
				oneOf(ctx).close();
			}
		});

		EJBUtils.getLocalEjb(List.class);
	}

	@Test
	public void testLookup() throws NamingException {
		context.checking(new Expectations() {
			{
				oneOf(ctx).lookup("test.jndi");
				oneOf(ctx).close();
			}
		});

		EJBUtils.lookup("test.jndi");
	}

	@Test
	public void testLookupClosesContextOnThrownException()
			throws NamingException {
		context.checking(new Expectations() {
			{
				oneOf(ctx).lookup(with(any(String.class)));
				will(throwException(new NamingException("thrown on purpose")));
				oneOf(ctx).close();
			}
		});

		EJBUtils.lookup("test.jndi");
	}

	@Test
	public void testCloseContext() throws NamingException {
		context.checking(new Expectations() {
			{
				oneOf(ctx).close();
			}
		});

		EJBUtils.closeContext(ctx);
	}

	@Test
	public void testCloseContextSwallowsThrownException()
			throws NamingException {
		context.checking(new Expectations() {
			{
				oneOf(ctx).close();
				will(throwException(new NamingException("Error closing!")));
			}
		});

		EJBUtils.closeContext(ctx);
	}

	static class FakeContextLookupStrategy implements ContextLookupStrategy {

		@Override
		public InitialContext getInitialContext() {
			return ctx;
		}
	}

}
