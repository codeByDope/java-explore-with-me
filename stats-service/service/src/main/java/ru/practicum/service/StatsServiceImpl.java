package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatsMapper;
import ru.practicum.StatsRepository;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.ResponseDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;
    private final StatsMapper mapper = StatsMapper.INSTANCE;

    @Override
    public void hit(RequestDto requestDto) {
        Stats stats = mapper.requestToModel(requestDto);
        repository.save(stats);
    }

    @Override
    public List<ResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris.isEmpty()) {
            if (unique) {
                return repository.findStatsUniqueIp(start, end);
            } else {
                return repository.findStats(start, end);
            }
        } else {
            if (unique) {
                return repository.findStatsByUriUniqueIp(uris, start, end);
            } else {
                return repository.findStatsByUri(uris, start, end);
            }
        }
    }
}
