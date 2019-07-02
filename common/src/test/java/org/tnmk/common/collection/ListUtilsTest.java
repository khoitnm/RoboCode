package org.tnmk.common.collection;

import org.junit.Assert;
import org.junit.Test;
import org.tnmk.common.collection.samplemodel.SimplePerson;
import org.tnmk.common.collection.samplemodel.SimplePersonFactory;

import java.util.List;

public class ListUtilsTest {

    @Test
    public void testGetLeastValue() {
        List<SimplePerson> simplePersons = SimplePersonFactory.constructPersonsWithAges(5, 2, 4, 2, 6);
        List<SimplePerson> leastAgeSimples = ListUtils.findLeastValueItems(simplePersons, SimplePerson::getAge);
        Assert.assertEquals(2, leastAgeSimples.size());
        Assert.assertTrue(leastAgeSimples.stream().allMatch(person -> person.getAge() == 2));
    }

    @Test
    public void testGetLeastValueWithEmptyList() {
        List<SimplePerson> simplePersons = SimplePersonFactory.constructPersonsWithAges();
        List<SimplePerson> leastAgeSimples = ListUtils.findLeastValueItems(simplePersons, SimplePerson::getAge);
        Assert.assertTrue(leastAgeSimples.isEmpty());
        //No exception should be thrown when the list is empty!
    }
}
