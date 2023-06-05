package ru.yandex.practicum.filmorate.inmemory.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.ASCENDING_ORDER;
import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static final String sort = DESCENDING_ORDER;

    private Map<Integer, Film> films = new ConcurrentHashMap<>();
    private Integer idCounter = 0;

    public Map<Integer, Film> getFilms() {
        return films;
    }

    public List<Film> findAll() {

        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        if (film.getDescription().length() > 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
        ) {
            throw new ValidationException("Фильм не создан. Не прошел " +
                    "проверку.");
        }
        Film resultFilm = Film.builder()
                .id(++idCounter)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(film.getLikes())
                .build();
        films.put(resultFilm.getId(), resultFilm);
        log.debug("Фильм создан: '{}'", resultFilm);
        return resultFilm;
    }

    public Film update(Film film) {
        if (film != null && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм обновлен: '{}'", film);
        } else {
            throw new NotFoundException("Фильм не обновлен. Не найден в " +
                    "списке.");
        }
        return film;
    }

    @Override
    public Film findFilmById(Integer id) {
        if (id != null && films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм не найден в списке.");
        }
    }

    @Override
    public List<Film> findTopLiked(Integer count) {
        return filter(count, sort);
    }

    public List<Film> filter(Integer count, String sort) {
        return films
                .values()
                .stream()
                .sorted((p0, p1) -> compare(p0, p1, sort))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1, String sort) {
        int result = f0.getLikes().size() - (f1.getLikes().size());
        switch (sort) {
            case ASCENDING_ORDER:
                result = 1 * result;
                break;
            case DESCENDING_ORDER:
                result = -1 * result; //обратный порядок сортировки
                break;
        }
        return result;
    }
}
