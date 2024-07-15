package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ResponseDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query(value = "select new ru.practicum.dto.ResponseDto(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats as s " +
            "where s.timestamp between :start and :end " +
            "and s.uri in :uri " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc")
    List<ResponseDto> findStatsByUriUniqueIp(@Param("uri") List<String> uri,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.dto.ResponseDto(s.app, s.uri, count(s.ip)) " +
            "from Stats as s " +
            "where s.timestamp between :start and :end " +
            "and s.uri in :uri " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ResponseDto> findStatsByUri(@Param("uri") List<String> uri,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.dto.ResponseDto(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats as s " +
            "where s.timestamp between :start and :end " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc")
    List<ResponseDto> findStatsUniqueIp(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.dto.ResponseDto(s.app, s.uri, count(s.ip)) " +
            "from Stats as s " +
            "where s.timestamp between :start and :end " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ResponseDto> findStats(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);
}

