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
 */package com.jdom.persist.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jdom.persist.persistence.MockDas.MockEntity;

@RunWith(JMock.class)
public class AbstractDASTest {

    private final MockDas das = new MockDas();

    private final Mockery context = new Mockery();

    private final EntityManager mockEntityManager = context.mock(EntityManager.class);

    private final Query mockQuery = context.mock(Query.class);

    private final MockEntity mockEntity = new MockEntity();

    @Before
    public void setUp() {
        das.em = mockEntityManager;
    }

    @Test
    public void testGetAll() {
        final List<MockEntity> results = new ArrayList<MockEntity>(Arrays.asList(mockEntity, new MockEntity()));
        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).createQuery(with(any(String.class)));
                will(returnValue(mockQuery));
                allowing(mockEntityManager);
                oneOf(mockQuery).getResultList();
                will(returnValue(results));
                allowing(mockQuery);
            }
        });

        assertEquals("The das should have returned the results!", results, das.getAll());
    }

    @Test
    public void testGetMostRecent() {

        final List<MockEntity> results = new ArrayList<MockEntity>(Arrays.asList(mockEntity, new MockEntity()));
        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).createQuery(with(any(String.class)));
                will(returnValue(mockQuery));
                allowing(mockEntityManager);
                oneOf(mockQuery).getResultList();
                will(returnValue(results));
                allowing(mockQuery);
            }
        });

        assertEquals("The das should have returned the results!", results, das.getMostRecent(3));
    }

    @Test
    public void testFindByUniqueNameReturnsFirstResult() {
        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).createQuery(with(any(String.class)));
                will(returnValue(mockQuery));
                allowing(mockEntityManager);
                oneOf(mockQuery).getResultList();
                will(returnValue(new ArrayList<MockEntity>(Arrays.asList(mockEntity, new MockEntity()))));
            }
        });

        assertEquals("The das should have returned the first entity!", mockEntity, das.findByUniqueName("test"));
    }

    @Test
    public void testFindByUniqueNameReturnsNullWhenEmptyResultListIsReturned() {
        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).createQuery(with(any(String.class)));
                will(returnValue(mockQuery));
                allowing(mockEntityManager);
                oneOf(mockQuery).getResultList();
                will(returnValue(Collections.emptyList()));
            }
        });

        assertNull("The das should have returned null!", das.findByUniqueName("test"));
    }

    @Test
    public void testGetIdColumn() {
        assertEquals("id", das.getIdColumn());
    }

    @Test
    public void testUpdateObjectReturnsTrueOnSuccess() {

        final Sequence updateObjectSequence = context.sequence("updateObjectSequence");

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).merge(mockEntity);
                inSequence(updateObjectSequence);
                oneOf(mockEntityManager).flush();
                inSequence(updateObjectSequence);
            }
        });

        assertTrue("The Das should have returned true when update was successful!", das.updateObject(mockEntity));
    }

    @Test
    public void testUpdateObjectReturnsFalseOnSuccess() {

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).merge(mockEntity);
                will(throwException(new IllegalArgumentException("on purpose")));
            }
        });

        assertFalse("The Das should have returned false when update was not successful!", das.updateObject(mockEntity));
    }

    @Test
    public void testAddObjectReturnsTrueOnSuccess() {
        final Sequence addObjectSequence = context.sequence("addObjectSequence");

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).persist(mockEntity);
                inSequence(addObjectSequence);
                oneOf(mockEntityManager).flush();
                inSequence(addObjectSequence);
            }
        });

        assertTrue("The Das should have returned true when add was successful!", das.addObject(mockEntity));
    }

    @Test
    public void testAddObjectReturnsFalseOnFailure() {
        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).persist(mockEntity);
                will(throwException(new IllegalArgumentException("on purpose")));
            }
        });

        assertFalse("The Das should have returned false when add was successful!", das.addObject(mockEntity));
    }

    @Test
    public void testDeleteObjectReturnsTrueOnSuccess() {
        final Sequence deleteObjectSequence = context.sequence("deleteObjectSequence");

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).merge(mockEntity);
                will(returnValue(mockEntity));
                inSequence(deleteObjectSequence);
                oneOf(mockEntityManager).remove(mockEntity);
                inSequence(deleteObjectSequence);
                oneOf(mockEntityManager).flush();
                inSequence(deleteObjectSequence);
            }
        });

        assertTrue("The Das should have returned true when delete was successful!", das.deleteObject(mockEntity));
    }

    @Test
    public void testDeleteObjectReturnsFalseOnFailure() {
        final Sequence deleteObjectSequence = context.sequence("deleteObjectSequence");

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).merge(mockEntity);
                will(returnValue(mockEntity));
                inSequence(deleteObjectSequence);
                oneOf(mockEntityManager).remove(mockEntity);
                will(throwException(new IllegalArgumentException("on purpose")));
            }
        });

        assertFalse("The Das should have returned false when delete fails!", das.deleteObject(mockEntity));
    }

    @Test
    public void testFindObject() {
        final Integer primaryKey = new Integer(7);

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).find(MockEntity.class, primaryKey);
                will(returnValue(mockEntity));
            }
        });

        assertSame("The found object should have been returned!", mockEntity, das.findObject(primaryKey));
    }

    @Test
    public void testRunQueryString() {
        final String exampleQuery = "query";
        final List<String> unsortedResults = Arrays.asList("last", "first");
        final List<String> expectedResults = new ArrayList<String>(unsortedResults);
        Collections.sort(expectedResults);

        final Sequence runQuerySequence = context.sequence("runQuerySequence");

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).createQuery(exampleQuery);
                will(returnValue(mockQuery));
                inSequence(runQuerySequence);
                oneOf(mockQuery).getResultList();
                will(returnValue(unsortedResults));
                inSequence(runQuerySequence);
            }
        });

        assertEquals("The results were not as expected!", expectedResults, das.runQuery(exampleQuery));
    }

    @Test
    public void testRunQueryStringInt() {

        final String exampleQuery = "query";
        final int numResults = 3;

        final List<String> unsortedResults = Arrays.asList("last", "first");
        final List<String> expectedResults = new ArrayList<String>(unsortedResults);
        Collections.sort(expectedResults);

        final Sequence runQuerySequence = context.sequence("runQuerySequence");

        context.checking(new Expectations() {
            {
                oneOf(mockEntityManager).createQuery(exampleQuery);
                will(returnValue(mockQuery));
                inSequence(runQuerySequence);
                oneOf(mockQuery).setMaxResults(numResults);
                inSequence(runQuerySequence);
                oneOf(mockQuery).getResultList();
                will(returnValue(unsortedResults));
                inSequence(runQuerySequence);
            }
        });

        assertEquals("The results were not as expected!", expectedResults, das.runQuery(exampleQuery, numResults));
    }
}
