package tasks;

import common.Area;
import common.Person;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
Имеются
- коллекция персон Collection<Person>
- словарь Map<Integer, Set<Integer>>, сопоставляющий каждой персоне множество id регионов
- коллекция всех регионов Collection<Area>
На выходе хочется получить множество строк вида "Имя - регион". Если у персон регионов несколько, таких строк так же будет несколько
 */
public class Task6 {

  private static String concatPersonArea(Person person, Area area) {
    return "%s - %s".formatted(person.firstName(), area.getName());
  }

  public static Set<String> getPersonDescriptions(Collection<Person> persons,
                                                  Map<Integer, Set<Integer>> personAreaIds,
                                                  Collection<Area> areas) {
    Map<Integer, Area> areasMap = areas.stream()
        .collect(Collectors.toMap(Area::getId, Function.identity()));

    return persons.stream()
        .flatMap(person -> personAreaIds.get(person.id()).stream()
            .map(areaId -> concatPersonArea(person, areasMap.get(areaId))))
        .collect(Collectors.toSet());
  }
}
