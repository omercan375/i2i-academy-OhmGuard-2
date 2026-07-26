package org.example.i2iacademyohmguard2.homes;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.appliances.ApplianceRepo;
import org.example.i2iacademyohmguard2.appliances.AppliancesService;
import org.example.i2iacademyohmguard2.appliances.AppliancesTable;
import org.example.i2iacademyohmguard2.appliances.dto.AddAppliancesDto;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;
import org.example.i2iacademyohmguard2.billing_accounts.BillingAccountsRepo;
import org.example.i2iacademyohmguard2.billing_accounts.BillingAccountsTable;
import org.example.i2iacademyohmguard2.homes.dto.*;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveState;
import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveStateService;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveState;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveStateService;
import org.example.i2iacademyohmguard2.kafka.registration.AssetRegistrationEvent;
import org.example.i2iacademyohmguard2.users.UsersRepo;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.example.i2iacademyohmguard2.zcommon.exception.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomesService {

    private static final String PUBLIC_OWNER_EMAIL = "public@ohmguard.local";

    private final HomesRepo homesRepo;

    private final HomeLiveStateService homeLiveStateService;
    private final ApplianceLiveStateService applianceLiveStateService;
    private final BillingAccountsRepo billingAccountsRepo;
    private final UsersRepo usersRepo;
    private final AppliancesService appliancesService;
    private final ApplianceRepo applianceRepo;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void deletePublic(UUID homeId) {
        HomesTable home = homesRepo.findByHomeId(homeId);
        if (home == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        cascadeDeleteHome(home);
    }

    @Transactional
    public void deleteForUser(UsersTable user, UUID homeId) {
        HomesTable home = homesRepo.findHomeByOwnerIdAndHomeId(user.getId(), homeId);
        if (home == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        cascadeDeleteHome(home);
    }

    private void cascadeDeleteHome(HomesTable home) {
        for (AppliancesTable appliance : applianceRepo.findAllByHomeId(home.getId())) {
            applianceLiveStateService.removeAppliances(appliance.getId());
            applicationEventPublisher.publishEvent(AssetRegistrationEvent.applianceRemoved(home.getId(), appliance.getId()));
        }
        homeLiveStateService.removeHome(home.getId());
        homesRepo.delete(home);
    }

    @Transactional
    public void deleteAppliancePublic(UUID homeId, UUID applianceId) {
        HomesTable home = homesRepo.findByHomeId(homeId);
        if (home == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        appliancesService.deleteAppliance(home.getOwner().getId(), homeId, applianceId);
    }

    @Transactional
    public void registerPublic(RegisterHomeDto dto) {
        UsersTable owner = getOrCreatePublicOwner();

        CreateHomeDto createHomeDto = CreateHomeDto.builder()
                .name(dto.getName())
                .email(dto.getContactEmail())
                .budgetLimit(dto.getBudgetLimit())
                .normalTariffRate(dto.getNormalTariffRate())
                .penaltyTariffRate(dto.getPenaltyTariffRate())
                .build();
        createHome(owner, createHomeDto);

        HomesTable created = homesRepo.findByOwnerIdAndHomeName(owner.getId(), dto.getName());
        if (dto.getAppliances() != null) {
            for (RegisterApplianceDto appliance : dto.getAppliances()) {
                AddAppliancesDto addAppliancesDto = AddAppliancesDto.builder()
                        .homeId(created.getId())
                        .name(appliance.getName())
                        .safeWattLimit(appliance.getSafeWattLimit())
                        .active(true)
                        .build();
                appliancesService.addAppliances(owner, addAppliancesDto);
            }
        }
    }

    @Transactional
    public void addAppliancePublic(UUID homeId, RegisterApplianceDto dto) {
        HomesTable home = homesRepo.findByHomeId(homeId);
        if (home == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        AddAppliancesDto addAppliancesDto = AddAppliancesDto.builder()
                .homeId(homeId)
                .name(dto.getName())
                .safeWattLimit(dto.getSafeWattLimit())
                .active(true)
                .build();
        appliancesService.addAppliances(home.getOwner(), addAppliancesDto);
    }

    @Transactional
    public void registerForUser(UsersTable user, RegisterHomeDto dto) {
        CreateHomeDto createHomeDto = CreateHomeDto.builder()
                .name(dto.getName())
                .email(dto.getContactEmail())
                .budgetLimit(dto.getBudgetLimit())
                .normalTariffRate(dto.getNormalTariffRate())
                .penaltyTariffRate(dto.getPenaltyTariffRate())
                .build();
        createHome(user, createHomeDto);

        HomesTable created = homesRepo.findByOwnerIdAndHomeName(user.getId(), dto.getName());
        if (dto.getAppliances() != null) {
            for (RegisterApplianceDto appliance : dto.getAppliances()) {
                AddAppliancesDto addAppliancesDto = AddAppliancesDto.builder()
                        .homeId(created.getId())
                        .name(appliance.getName())
                        .safeWattLimit(appliance.getSafeWattLimit())
                        .active(true)
                        .build();
                appliancesService.addAppliances(user, addAppliancesDto);
            }
        }
    }

    private UsersTable getOrCreatePublicOwner() {
        UsersTable existing = usersRepo.findByEmail(PUBLIC_OWNER_EMAIL);
        if (existing != null) {
            return existing;
        }
        UsersTable owner = UsersTable.builder()
                .email(PUBLIC_OWNER_EMAIL)
                .passwordHash("-")
                .firstName("Public")
                .lastName("Registrar")
                .build();
        try {
            return usersRepo.save(owner);
        } catch (DataIntegrityViolationException e) {
            return usersRepo.findByEmail(PUBLIC_OWNER_EMAIL);
        }
    }

    @Transactional
    public void createHome(UsersTable user, CreateHomeDto createHomeDto) {
        HomesTable createHome;
        /*exist control*/
        HomesTable existControl = homesRepo.findByOwnerIdAndHomeName(user.getId(),createHomeDto.getName());
        if (existControl != null) {
            throw new AlreadyExistsException("Home already exists");
        }
        if (createHomeDto.getNormalTariffRate().compareTo(createHomeDto.getPenaltyTariffRate()) >= 0) {
            throw new InvalidResourceException(
                    "Penalty tariff rate must be greater than normal tariff rate"
            );
        }
        HomesTable newHome = HomesTable.builder()
                .owner(user)
                .homeName(createHomeDto.getName())
                .contactEmail(createHomeDto.getEmail())
                .budgetLimit(createHomeDto.getBudgetLimit())
                .normalTariffRate(createHomeDto.getNormalTariffRate())
                .penaltyTariffRate(createHomeDto.getPenaltyTariffRate())
                .isActive(true)
                .build();
        try{
          createHome =   homesRepo.save(newHome);
        }catch (DataIntegrityViolationException e) {
            throw new SaveException("home cant save");
        }
        /*create billing account for home*/
        BillingAccountsTable billingAccount = BillingAccountsTable.builder()
                .homeTable(createHome)
                .accumulatedEnergyKwh(BigDecimal.ZERO)
                .accumulatedCost(BigDecimal.ZERO)
                .activeTariff(ActiveTariff.NORMAL)
                .build();
        billingAccountsRepo.save(billingAccount);
        /*create home ignit*/
        homeLiveStateService.initHome(createHome.getId(),createHome.getNormalTariffRate(),createHome.getPenaltyTariffRate(),createHome.getBudgetLimit());

        /*kafka mesajını yolla*/
        applicationEventPublisher.publishEvent(AssetRegistrationEvent.homeCreated(createHome.getId()));

    }

    public List<ShowAllHomeDto> showAllHomes(UsersTable user) {
        List<ShowAllHomeDto> showAllHome = new ArrayList<>();
        List<HomesTable> allHomes = homesRepo.findAllByOwnerId(user.getId());
        for (HomesTable home : allHomes) {
            ShowAllHomeDto newHome = ShowAllHomeDto.builder()
                    .homeId(home.getId())
                    .name(home.getHomeName())
                    .isActive(home.getIsActive())
                    .build();
            showAllHome.add(newHome);
        }
        return showAllHome;
    }
    public HomeInfo getHomeInfo(UsersTable user, UUID homeId){
        HomesTable find = homesRepo.findHomeByOwnerIdAndHomeId(user.getId(), homeId);
        if (find == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        return HomeInfo.builder()
                .name(find.getHomeName())
                .contactEmail(find.getContactEmail())
                .budgetLimit(find.getBudgetLimit())
                .normalTariffRate(find.getNormalTariffRate())
                .penaltyTariffRate(find.getPenaltyTariffRate())
                .isActive(find.getIsActive())
                .build();
    }
    @Transactional
    public void updateBudgetLimit(UsersTable user, UpdateBudgetLimit updateBudgetLimitDto) {
        /*find home*/
        HomesTable findHome = homesRepo.findHomeByOwnerIdAndHomeId(user.getId(), updateBudgetLimitDto.getHomeId());
        if (findHome == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        int updateBudgetLimit = homesRepo.updateBudgetLimit(updateBudgetLimitDto.getNewBudgetLimit(),findHome.getId(), findHome.getVersion());
        if(updateBudgetLimit == 0){
            throw new UpdateException("budget limit cant update");
        }
        /*igniti günceller*/
        homeLiveStateService.updateBudgetLimit(findHome.getId(),updateBudgetLimitDto.getNewBudgetLimit());
    }
    @Transactional
    public void updateTariffRates(UsersTable user, UpdateTariffLimits updateTariffLimits) {
        /*controls*/
        if(updateTariffLimits.getNormTariffRate().compareTo(updateTariffLimits.getPenaltyTariffRate()) >= 0){
            throw new IllegalArgumentException("normal tariff must not much than penalty tariff rate");
        }

        /*find home*/
        HomesTable findHome = homesRepo.findHomeByOwnerIdAndHomeId(user.getId(), updateTariffLimits.getHomeId());
        if (findHome == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        int updateTariffRates = homesRepo.updateTariffRates(updateTariffLimits.getNormTariffRate(),updateTariffLimits.getPenaltyTariffRate(),findHome.getId(), findHome.getVersion());
        if(updateTariffRates == 0){
            throw new UpdateException("tariff rate cant update");
        }
        /*igniti günceller*/
        homeLiveStateService.updateTariffRate(findHome.getId(),updateTariffLimits.getNormTariffRate(),updateTariffLimits.getPenaltyTariffRate());

    }

    public List<DashboardHomeDto> dashboard() {
        return buildDashboard(homesRepo.findAll());
    }

    public List<DashboardHomeDto> dashboardForUser(UsersTable user) {
        return buildDashboard(homesRepo.findAllByOwnerId(user.getId()));
    }

    private List<DashboardHomeDto> buildDashboard(Iterable<HomesTable> homes) {
        List<DashboardHomeDto> result = new ArrayList<>();
        for (HomesTable home : homes) {
            HomeLiveState live = homeLiveStateService.getHomeLiveState(home.getId());
            List<ApplianceLiveState> appliances = applianceLiveStateService.getApplianceLiveStatesByHome(home.getId());
            long anomalyCount = appliances.stream().filter(ApplianceLiveState::isAnomalous).count();

            BigDecimal budget = home.getBudgetLimit();
            BigDecimal cost = live != null ? live.getAccumulatedCost() : BigDecimal.ZERO;
            BigDecimal kwh = live != null ? live.getAccumulatedKwh() : BigDecimal.ZERO;

            result.add(DashboardHomeDto.builder()
                    .homeId(home.getId())
                    .name(home.getHomeName())
                    .contactEmail(home.getContactEmail())
                    .budgetLimit(budget)
                    .accumulatedCost(cost)
                    .accumulatedKwh(kwh)
                    .usagePercent(usagePercent(cost, budget))
                    .activeTariff(live != null ? live.getActiveTariff() : ActiveTariff.NORMAL)
                    .alert80Sent(live != null && live.isAlert80Sent())
                    .alert100Sent(live != null && live.isAlert100Sent())
                    .anomalyCount(anomalyCount)
                    .applianceCount(appliances.size())
                    .live(live != null)
                    .build());
        }
        return result;
    }

    private BigDecimal usagePercent(BigDecimal cost, BigDecimal budget) {
        if (cost == null || budget == null || budget.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return cost.divide(budget, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public HomeStatusDto getHomeStatus(UUID homeId) {
        HomeLiveState home = homeLiveStateService.getHomeLiveState(homeId);
        if (home == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        List<ApplianceStatusDto> appliances = new ArrayList<>();
        for (ApplianceLiveState appliance : applianceLiveStateService.getApplianceLiveStatesByHome(homeId)) {
            appliances.add(ApplianceStatusDto.builder()
                    .applianceId(appliance.getApplianceId())
                    .name(appliance.getName())
                    .lastMeasuredWatt(appliance.getLastMeasuredWatt())
                    .safeWattLimit(appliance.getSafeWattLimit())
                    .consecutiveBreachCount(appliance.getConsecutiveBreachCount())
                    .anomalous(appliance.isAnomalous())
                    .lastMeasuredAt(appliance.getLastMeasuredAt())
                    .build());
        }
        return HomeStatusDto.builder()
                .homeId(home.getHomeId())
                .accumulatedKwh(home.getAccumulatedKwh())
                .accumulatedCost(home.getAccumulatedCost())
                .budgetLimit(home.getBudgetLimit())
                .activeTariff(home.getActiveTariff())
                .alert80Sent(home.isAlert80Sent())
                .alert100Sent(home.isAlert100Sent())
                .appliances(appliances)
                .build();
    }

    public HomesTable findHome(UUID homeId){
        HomesTable find = homesRepo.findByHomeId(homeId);
        if (find == null) {
            throw new ResourceNotFoundException("HOME NOT FOUND");
        }
        return find;
    }
    public List<HomesTable> findActiveHome(){
        return homesRepo.findAllActive();
    }

}
