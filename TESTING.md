# Тестирование

## Инструменты
- JUnit 5 (через spring-boot-starter-test)
- Mockito (мокирование зависимостей)
- Maven Surefire (запуск тестов)

## Запуск
Команда:
mvnw test

Результат запуска:
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

## Набор unit-тестов

| ID | Класс теста | Сценарий | Ожидаемый результат | Статус |
|---|---|---|---|---|
| UT-PS-01 | PetServiceTest | feedPet (hunger=80) | hunger ограничен 100, выполняется сохранение | PASS |
| UT-PS-02 | PetServiceTest | putPetToSleep (уже спит) | повторное сохранение не выполняется | PASS |
| UT-PS-03 | PetServiceTest | putPetToSleep (не спит) | sleepUntil назначается, state меняется, save вызывается | PASS |
| UT-MS-01 | MoodServiceTest | дата в прошлом | выбрасывается DateNotAllowedException | PASS |
| UT-MS-02 | MoodServiceTest | дата в будущем | выбрасывается DateNotAllowedException | PASS |
| UT-MS-03 | MoodServiceTest | today (новая запись) | save выполняется, achievements проверяются | PASS |
| UT-MS-04 | MoodServiceTest | today (обновление) | запись обновляется, achievements проверяются | PASS |

## Краткий анализ
- Проверена бизнес-логика сервисного слоя (без поднятия UI).
- Протестированы граничные условия: ограничение hunger до 100 и запрет дат, отличных от текущей.
- Внешние зависимости (репозитории/сервисы достижений) мокируются, поэтому тесты быстрые и стабильные.
- Все тесты завершились успешно (Failures/Errors = 0).