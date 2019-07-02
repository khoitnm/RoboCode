package org.tnmk.common.collection.samplemodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimplePersonFactory {
    public static SimplePerson constructPerson() {
        return constructPersonWithAge(new Random().nextInt());
    }

    public static SimplePerson constructPersonWithAge(int age) {
        SimplePerson simple = new SimplePerson();
        simple.setAge(age);
        simple.setName("Name_" + System.nanoTime());
        return simple;
    }

    public static List<SimplePerson> constructPersonsWithAges(int... ages) {
        List<SimplePerson> result = new ArrayList<>();
        for (int age : ages) {
            SimplePerson simple = constructPersonWithAge(age);
            result.add(simple);
        }
        return result;
    }
}
