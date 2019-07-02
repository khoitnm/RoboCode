package org.tnmk.common.collection;

import org.junit.Assert;
import org.junit.Test;
import org.tnmk.common.collection.samplemodel.SimplePerson;
import org.tnmk.common.collection.samplemodel.SimplePersonFactory;

import java.util.List;

public class ListUtilsTest {


    @Test
    public void testGetLeastValue_WithEmptyList() {
        List<SimplePerson> simplePersons = SimplePersonFactory.constructPersonsWithAges();
        List<SimplePerson> leastAgeSimples = ListUtils.findLeastValueItems(simplePersons, SimplePerson::getAge);
        Assert.assertTrue(leastAgeSimples.isEmpty());
        //No exception should be thrown when the list is empty!
    }

    @Test
    public void testGetLeastValue_WithOneValue() {
        List<SimplePerson> simplePersons = SimplePersonFactory.constructPersonsWithAges(3);
        List<SimplePerson> leastAgeSimples = ListUtils.findLeastValueItems(simplePersons, SimplePerson::getAge);
        Assert.assertEquals(1, leastAgeSimples.size());
        Assert.assertTrue(leastAgeSimples.stream().allMatch(person -> person.getAge() == 3));
    }

    @Test
    public void testGetLeastValue_WithSameValues() {
        List<SimplePerson> simplePersons = SimplePersonFactory.constructPersonsWithAges(3, 3, 3, 3);
        List<SimplePerson> leastAgeSimples = ListUtils.findLeastValueItems(simplePersons, SimplePerson::getAge);
        Assert.assertEquals(4, leastAgeSimples.size());
        Assert.assertTrue(leastAgeSimples.stream().allMatch(person -> person.getAge() == 3));
    }

    @Test
    public void testGetLeastValue() {
        List<SimplePerson> simplePersons = SimplePersonFactory.constructPersonsWithAges(7, 3, 5, 3, 4, 2, 2, 4, 2, 6, 2);
        List<SimplePerson> leastAgeSimples = ListUtils.findLeastValueItems(simplePersons, SimplePerson::getAge);
        Assert.assertEquals(4, leastAgeSimples.size());
        Assert.assertTrue(leastAgeSimples.stream().allMatch(person -> person.getAge() == 2));
    }
}
