package tasks;

import common.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class Task9Test {
  private static Task9 task9;
  private static Person person1, person2, person3, person4;

  @BeforeAll
  public static void before() {
    task9 = new Task9();

    Instant time = Instant.now();
    person1 = new Person(1, "Name1", "SecondName1", null, time);
    person2 = new Person(2, "Name2", "SecondName2", "MidName2", time);
    person3 = new Person(3, "Name3", null, null, time);
    person4 = new Person(4, "Name1", "SecondName4", null, time);
  }

  private static Stream<Arguments> generateDataPersonToString() {
    return Stream.of(
        Arguments.of(person1, "SecondName1 Name1"),
        Arguments.of(person2, "SecondName2 Name2 MidName2"),
        Arguments.of(person3, "Name3"),
        Arguments.of(person4, "SecondName4 Name1")
    );
  }

  private static Stream<Arguments> generateDataNumbers() {
    return Stream.of(
        Arguments.of(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 5),
        Arguments.of(Stream.of(1, 3, 5, 7), 0),
        Arguments.of(Stream.of(0), 1),
        Arguments.of(Stream.of(2, 4, 16, 32), 4),
        Arguments.of(Stream.of(), 0)
    );
  }

  @Test
  public void testGetNamesFromImmutableList() {
    List<Person> persons = List.of(person1, person2, person3, person4);

    assertEquals(List.of(person2.firstName(), person3.firstName(), person4.firstName()), task9.getNames(persons));
  }

  @Test
  public void testGetNamesFromMutableList() {
    List<Person> persons = new ArrayList<>();
    persons.add(person1);
    persons.add(person2);
    persons.add(person3);
    persons.add(person4);

    assertEquals(List.of(person2.firstName(), person3.firstName(), person4.firstName()), task9.getNames(persons));
  }

  @Test
  public void testGetNamesFromEmptyList() {
    List<Person> persons = Collections.emptyList();

    assertEquals(List.of(), task9.getNames(persons));
  }

  @Test
  public void testGetNamesFromOneElementList() {
    List<Person> persons = List.of(person1);

    assertEquals(List.of(), task9.getNames(persons));
  }

  @Test
  public void testGetDifferentNames() {
    assertEquals(
        Set.of(person1.firstName(), person2.firstName(), person3.firstName()),
        task9.getDifferentNames(List.of(person1, person2, person3, person4))
    );
  }

  @ParameterizedTest
  @MethodSource("generateDataPersonToString")
  public void testConvertPersonToString(Person person, String result) {
    assertEquals(result, task9.convertPersonToString(person));
  }

  @Test
  public void testGetPersonNames() {
    assertEquals(
        Map.of(1, "SecondName1 Name1", 2, "SecondName2 Name2 MidName2", 3, "Name3"),
        task9.getPersonNames(List.of(person1, person2, person3, new Person(1, "Spy", null, null, Instant.now())))
    );
  }

  @Test
  public void testHasSamePersonsShouldReturnTrue() {
    assertTrue(task9.hasSamePersons(List.of(person1, person2, person3, person4), List.of(person1, person4)));
  }

  @Test
  public void testHasSamePersonsShouldReturnFalse() {
    assertFalse(task9.hasSamePersons(List.of(person2, person3), List.of(person1, person4)));
  }
  @ParameterizedTest
  @MethodSource("generateDataNumbers")
  public void testCountEven(Stream<Integer> numbers, long result) {
    assertEquals(result, task9.countEven(numbers));
  }

  // Тестовый метод для подтверждения теории
  @ParameterizedTest
  @MethodSource("generateDataNumbers")
  public void testCountEvenInTwoThreads(Stream<Integer> numbers, long result) {
    List<Integer> copy = numbers.toList();

    AtomicLong count1 = new AtomicLong();
    AtomicLong count2 = new AtomicLong();

    Thread thread1 = new Thread(() -> count1.set(task9.countEven(copy.stream())));
    Thread thread2 = new Thread(() -> count2.set(task9.countEven(copy.stream())));

    thread1.start();
    thread2.start();

    try {
      thread1.join();
      thread2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertEquals(result, count1.get());
    assertEquals(result, count2.get());
  }
}
