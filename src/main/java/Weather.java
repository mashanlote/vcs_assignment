import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Getter
@Setter
public class Weather {

    private UUID regionId;
    private String regionName;
    private int temperature;
    private LocalDateTime dateTime;

    public Weather(UUID regionId, String regionName, int temperature, LocalDateTime dateTime) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.temperature = temperature;
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", regionName, dateTime);
    }

    public static List<Weather> generateRandomWeatherObservations(
            UUID regionId,
            String regionName,
            int baseTemperature,
            int temperatureVariance
    ) {
        var dates = LocalDate.of(2023, 9, 1)
                .datesUntil(LocalDate.of(2023, 10, 1))
                .map(date -> LocalDateTime.of(date, LocalTime.of(12, 0)))
                .toList();
        return dates.stream()
                .map(date -> new Weather(
                        regionId,
                        regionName,
                        getRandomTemperature(baseTemperature, temperatureVariance),
                        date))
                .toList();
    }

    private static int getRandomTemperature(int baseTemperature, int temperatureVariance) {
        return ThreadLocalRandom.current().nextInt(
                baseTemperature - temperatureVariance,
                baseTemperature + temperatureVariance + 1
        );
    }

    // Создать функцию для поиска регионов, больше какой-то определенной температуры
    public static List<String> getRegionsWarmerThanXDegrees(
            Map<String, Double> regionList,
            double x
    ) {
        return regionList.entrySet().stream()
                .filter(e -> e.getValue() > x)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void main(String[] args) {
        var regionIds = Stream.generate(UUID::randomUUID).limit(5).toList();
        var regionNames = List.of("Tomsk", "Moscow", "Sochi", "Hong Kong", "Doha");
        var baseTemperatures = List.of(0, 10, 15, 20, 30);
        var temperatureVariances = List.of(10, 5, 5, 5, 2);
        var randomTemperatures = IntStream.range(0, regionIds.size())
                .mapToObj(i -> generateRandomWeatherObservations(
                        regionIds.get(i),
                        regionNames.get(i),
                        baseTemperatures.get(i),
                        temperatureVariances.get(i)
                ).stream())
                .flatMap(Function.identity())
                .toList();

        // Рассчитать среднее значение температуры в регионах
        var averageTemperatureByRegion = randomTemperatures.stream()
                .collect(groupingBy(
                        Weather::getRegionName,
                        collectingAndThen(
                            averagingDouble(Weather::getTemperature),
                            t -> Math.round(t * 100) / 100.00
                        )
                ));

        printSeparator();
        System.out.println("Subtask 1: Printing Average Temperature by Region");
        averageTemperatureByRegion.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.printf("%-10s%.2f%n", e.getKey(), e.getValue()));
        printSeparator();

        // Создать функцию для поиска регионов, больше какой-то определенной температуры
        var regionsWithAverageHigherThan10Degrees = getRegionsWarmerThanXDegrees(averageTemperatureByRegion, 10);
        System.out.println("Subtask 2: Print Regions With Temperature Higher than 10 Degrees");
        regionsWithAverageHigherThan10Degrees.forEach(System.out::println);
        printSeparator();

        // Преобразовать список в Map,
        // у которой ключ - уникальный идентификатор, значение - список со значениями температур
        System.out.println("Subtask 3: Print Regions' UUIDs and Lists of Temperatures");
        var regionToTemperaturesMap = randomTemperatures
                .stream()
                .collect(groupingBy(
                        Weather::getRegionId,
                        Collectors.mapping(Weather::getTemperature, toList())
                ));
        regionToTemperaturesMap.entrySet().stream()
                .collect(toMap(
                        e -> e.getKey(),
                        e -> String.join(", ", e.getValue().toString())
                ))
                .forEach((k, v) -> System.out.printf("%s: %s%n", k, v));
        printSeparator();

        // Преобразовать список в Map,
        // у которой ключ - температура,
        // значение - коллекция объектов Weather,
        // которым соответствует температура, указанная в ключе
        System.out.println("Subtask 4: Print Lists of Weather Objects By Temperature");
        var temperaturesToWeathersMap = randomTemperatures
                .stream()
                .collect(groupingBy(
                        Weather::getTemperature,
                        TreeMap::new,
                        Collectors.mapping(Function.identity(), toList())
                ));
        temperaturesToWeathersMap
                .forEach((k, v) -> System.out.printf("%s: %s%n", k, v));
        printSeparator();

    }

    public static void printSeparator() {
        System.out.println("--------------------------------------------------");
    }

}
