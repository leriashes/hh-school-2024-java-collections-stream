package tasks;

import common.Person;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Далее вы увидите код, который специально написан максимально плохо.
Постарайтесь без ругани привести его в надлежащий вид
P.S. Код в целом рабочий (не везде), комментарии оставлены чтобы вам проще понять чего же хотел автор
P.P.S Здесь ваши правки необходимо прокомментировать (можно в коде, можно в PR на Github)
 */
public class Task9 {

  // Неиспользуемое поле count удалено

  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй

  /*
  Минусы исходного решения
  - метод remove изменяет исходный список людей
  - в случае с неизменяемым списком в remove - будет ошибка
   */
  public List<String> getNames(List<Person> persons) {
    return persons.stream()
        .skip(1)
        .map(Person::firstName)
        .collect(Collectors.toList());
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  // Нет необходимости использовать стрим и distinct, поскольку можно сразу создать множество
  public Set<String> getDifferentNames(List<Person> persons) {
    return new HashSet<>(getNames(persons));
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО
  /*
  - добавлено отчество (в исходном решении повторно добавлялась фамилия вместо отчества)
  - использован стрим для проверки на null и склейки через разделители, более компактно
   */
  public String convertPersonToString(Person person) {
    return Stream.of(person.secondName(), person.firstName(), person.middleName())
        .filter(Objects::nonNull)
        .collect(Collectors.joining(" "));
  }

  // словарь id персоны -> ее имя
  // Можно сразу собирать в словарь при помощи стрима, более компактно, без доп. переменных и проверок "вручную"
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
    return persons.stream()
        .collect(Collectors.toMap(Person::id, this::convertPersonToString, (first, second) -> first));
  }

  // есть ли совпадающие в двух коллекциях персоны?
  /*
  Минусы исходного решения:
  - сложность O(n * m)
  - даже когда человек из persons1 нашёлся в persons2 продолжается его поиск (сравнение с остальными)

  Можно использовать стрим и хэш-сет, более быстро и лаконично
  + anyMatch для person остановится как только найдёт совпадающего в persons2
  + поиск в хэш-сет O(1)
  + итоговая сложность O(n + m)
  - использование памяти
   */
  //
  // В исходном решении сложность O(n * m)
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    return persons1.stream().anyMatch(new HashSet<>(persons2)::contains);
  }

  // Посчитать число четных чисел
  /*
    Минусы использования переменной count:
    - использование памяти
    - если в дальнейшем count будет использоваться в другом методе, как свойство объекта, а не счётчик,
    то вызов метода countEven может непредсказуемо изменять данную переменную
    - при использовании метода (или просто изменении count) сразу несколькими потоками значение count непредсказуемо (проблема гонки данных)
  */
  public long countEven(Stream<Integer> numbers) {
    return numbers.filter(num -> num % 2 == 0).count();
  }

  // Загадка - объясните почему assert тут всегда верен
  // Пояснение в чем соль - мы перетасовали числа, обернули в HashSet, а toString() у него вернул их в сортированном порядке
  /*
  В данном случае assert будет всегда true потому, что количество buckets (корзин) в хэш-таблице для HashSet
  будет расчитано относительно числа элементов (так, чтобы число элементов не превышало 0.75 от числа бакетов),
  а hashCode для int равен самому числу,
  поэтому получится, что каждое число будет "лежать" в своей корзине

  При получении элементов множества, они будут выданы в порядке номеров корзин,
  следовательно, в порядке возрастания
   */
  void listVsSet() {
    List<Integer> integers = IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList());
    List<Integer> snapshot = new ArrayList<>(integers);
    Collections.shuffle(integers);
    Set<Integer> set = new HashSet<>(integers);
    assert snapshot.toString().equals(set.toString());
  }
}
