package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {


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
                .likes(new HashSet<>())
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
}
