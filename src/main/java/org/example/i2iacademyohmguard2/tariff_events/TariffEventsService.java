package org.example.i2iacademyohmguard2.tariff_events;


import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.homes.HomesService;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.tariff_events.dto.TariffEventsDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffEventsService {

    private final HomesService homesService;
    private final TariffEventsRepo tariffEventsRepo;

    public void createTariffEvents(UUID homeId,boolean alert100,boolean previousAlert100){
        boolean toPenalty = !previousAlert100 && alert100;
        boolean toNormal = previousAlert100 && !alert100;
        if(!toPenalty && !toNormal){
            return;
        }

        HomesTable home = homesService.findHome(homeId);

        if(toPenalty){
            /*normal den penalty e geçiş*/
            TariffEventsTable newTariffEvent = TariffEventsTable.builder()
                    .home(home)
                    .previousTariff(TariffTypes.NORMAL)
                    .newTariff(TariffTypes.PENALTY)
                    .build();
            tariffEventsRepo.save(newTariffEvent);
        }
        if(toNormal){
            /*penalty den normale geçiş*/
            TariffEventsTable newTariffEvent = TariffEventsTable.builder()
                    .home(home)
                    .previousTariff(TariffTypes.PENALTY)
                    .newTariff(TariffTypes.NORMAL)
                    .build();
            tariffEventsRepo.save(newTariffEvent);
        }
    }

    public List<TariffEventsDto> tariffEventsHistory(UUID userId,UUID homeId){
        List<TariffEventsDto> history = new ArrayList<>();

        List<TariffEventsTable> getAll = tariffEventsRepo.tariffEventsHistory(userId,homeId);
        for (TariffEventsTable tariffEvents : getAll) {
            TariffEventsDto tariffEvent = TariffEventsDto.builder()
                    .previousTariff(tariffEvents.getPreviousTariff().toString())
                    .newTariff(tariffEvents.getNewTariff().toString())
                    .activedAt(tariffEvents.getActivatedAt())
                    .build();
            history.add(tariffEvent);
        }
        return history;

    }
}
