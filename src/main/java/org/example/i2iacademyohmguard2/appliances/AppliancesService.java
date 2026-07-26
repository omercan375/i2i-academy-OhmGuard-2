package org.example.i2iacademyohmguard2.appliances;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.i2iacademyohmguard2.appliances.dto.AddAppliancesDto;
import org.example.i2iacademyohmguard2.appliances.dto.ApplianceInfo;
import org.example.i2iacademyohmguard2.appliances.dto.ShowAllAppliancesDto;
import org.example.i2iacademyohmguard2.appliances.dto.UpdateSafeWattLimitDto;
import org.example.i2iacademyohmguard2.homes.HomesRepo;
import org.example.i2iacademyohmguard2.homes.HomesTable;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveStateService;
import org.example.i2iacademyohmguard2.kafka.registration.AssetRegistrationEvent;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.example.i2iacademyohmguard2.zcommon.exception.AlreadyExistsException;
import org.example.i2iacademyohmguard2.zcommon.exception.ResourceNotFoundException;
import org.example.i2iacademyohmguard2.zcommon.exception.SaveException;
import org.example.i2iacademyohmguard2.zcommon.exception.UpdateException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppliancesService {

    private final ApplianceRepo applianceRepo;
    private final HomesRepo homesRepo;
    private final ApplianceLiveStateService applianceLiveStateService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void addAppliances(UsersTable user, AddAppliancesDto addAppliancesDto) {
        AppliancesTable newAppliance;
        /*find home*/
        HomesTable home = homesRepo.findHomeByOwnerIdAndHomeId(user.getId(), addAppliancesDto.getHomeId());
        if (home == null) {
            throw new ResourceNotFoundException("Home not found");
        }
        /*appliances exist control*/
        AppliancesTable existControl = applianceRepo.getAppliancesByNameAndHomeId(addAppliancesDto.getName(), home.getId());
        if (existControl != null) {
            throw new AlreadyExistsException("Appliances already exists");
        }

        AppliancesTable addAppliances = AppliancesTable.builder()
                .home(home)
                .name(addAppliancesDto.getName())
                .safeWattLimit(addAppliancesDto.getSafeWattLimit())
                .active(addAppliancesDto.isActive())
                .build();
        try {
           newAppliance =   applianceRepo.save(addAppliances);
        } catch (DataIntegrityViolationException e) {
            throw new SaveException("appliances cant save");
        }

        applianceLiveStateService.addAppliances(newAppliance.getId(),home.getId(),newAppliance.getName(),addAppliancesDto.getSafeWattLimit());
        /* kafka ya yolla*/
        applicationEventPublisher.publishEvent(AssetRegistrationEvent.applianceAdded(home.getId(),newAppliance.getId(),newAppliance.getName(),newAppliance.getSafeWattLimit()));



    }

    public List<ShowAllAppliancesDto> showAllAppliance(UsersTable user, UUID homeId) {
        List<ShowAllAppliancesDto> showAllAppliances = new ArrayList<>();
        List<AppliancesTable> findAll = applianceRepo.getAppliancesByHomeIdAndOwnerId(homeId, user.getId());
        for (AppliancesTable appliancesTable : findAll) {
            ShowAllAppliancesDto newAppliance = ShowAllAppliancesDto.builder()
                    .applianceId(appliancesTable.getId())
                    .applianceName(appliancesTable.getName())
                    .isActive(appliancesTable.isActive())
                    .build();
            showAllAppliances.add(newAppliance);
        }
        return showAllAppliances;
    }

    public ApplianceInfo applianceInfo(UsersTable user, UUID homeId, UUID applianceId) {
        AppliancesTable findAppliances = applianceRepo.findApplianceByApplianceIdAndByHomeIdAndOwnerId(applianceId, homeId, user.getId());
        if (findAppliances == null) {
            throw new ResourceNotFoundException("Appliance not found");
        }
        return ApplianceInfo.builder()
                .applianceName(findAppliances.getName())
                .safeWattLimit(findAppliances.getSafeWattLimit())
                .active(findAppliances.isActive())
                .build();
    }

    @Transactional
    public void updateSafeWattLimit(UsersTable user, UpdateSafeWattLimitDto updateSafeWattLimitDto) {
        /*find appliances*/
        AppliancesTable findAppliance = applianceRepo.findApplianceByApplianceIdAndByHomeIdAndOwnerId(updateSafeWattLimitDto.getApplianceId(),updateSafeWattLimitDto.getHomeId(), user.getId());
        if (findAppliance == null) {
            throw new ResourceNotFoundException("Appliance not found");
        }
        int updateSafeWattLimit=applianceRepo.updateSafeWattLimit(updateSafeWattLimitDto.getNewWattLimit(),updateSafeWattLimitDto.getApplianceId(),findAppliance.getVersion());
        if(updateSafeWattLimit == 0){
            throw new UpdateException("new safe watt limit cant update");
        }
        /*ignitteki veriyi güncelle*/
        applianceLiveStateService.updateNewSafeWattLimit(findAppliance.getId(),updateSafeWattLimitDto.getNewWattLimit());
    }
    @Transactional
    public void deleteAppliance(UUID userId,UUID homeId,UUID applianceId) {
        AppliancesTable findAppliance = applianceRepo.findApplianceByApplianceIdAndByHomeIdAndOwnerId(applianceId, homeId, userId);
        if (findAppliance == null) {
            throw new ResourceNotFoundException("Appliance not found");
        }

        int deleteControl =  applianceRepo.deleteById(applianceId,findAppliance.getVersion());
        if(deleteControl == 0){
            throw new UpdateException("delete appliance failed");
        }
        /*İGNİTE İ TEMİZLE*/
        applianceLiveStateService.removeAppliances(findAppliance.getId());

        /*kafkaya mesajı dön*/
        applicationEventPublisher.publishEvent(AssetRegistrationEvent.applianceRemoved(homeId,applianceId));



    }

}
