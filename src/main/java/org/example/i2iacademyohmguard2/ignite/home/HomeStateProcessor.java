package org.example.i2iacademyohmguard2.ignite.home;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.example.i2iacademyohmguard2.billing_accounts.ActiveTariff;
import org.example.i2iacademyohmguard2.ignite.home.dto.ProcessResponse;
import org.example.i2iacademyohmguard2.zcommon.exception.ResourceNotFoundException;
import javax.cache.processor.MutableEntry;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Bir evin canli durumunu ATOMIK olarak Ignite cache'ine yazan EntryProcessor.
 *
 * cache.invoke(key, processor) cagrisi ayni homeId icin Ignite tarafindan
 * kilitlenerek calistirildigindan, es zamanli yazmalar birbirini bozmaz.
 *
 * {@link org.example.i2iacademyohmguard2.ignite.appliance.LatestTelemetryProcessor}
 * ile ayni stilde: mevcut state yoksa olusturur, kendisine verilen degerleri
 * oldugu gibi set eder ve cache'e geri yazar. Hicbir hesap/karar yapmaz.
 */
public class HomeStateProcessor
        implements CacheEntryProcessor<UUID, HomeLiveState, ProcessResponse> {

    private static final long serialVersionUID = 1L;


    private final BigDecimal measuredWatt;
    private final int intervalSeconds;




    public HomeStateProcessor(BigDecimal measuredWatt, int intervalSeconds) {
        this.measuredWatt = measuredWatt;
        this.intervalSeconds = intervalSeconds;
    }


    @Override
    public ProcessResponse process(MutableEntry<UUID, HomeLiveState> entry, Object... args) {
        HomeLiveState home = entry.getValue();   // bir kez oku, tekrar tekrar getValue çağırma

        if (home == null) {
            return null;
        }

        /*billing calculation*/
        BigDecimal tariffRate = switch(home.getActiveTariff()){
            case NORMAL -> home.getNormalRate();
            case PENALTY -> home.getPenaltyRate();
            default -> throw new ResourceNotFoundException(" rate not found");
        };
        BigDecimal wattToKw = measuredWatt.divide(BigDecimal.valueOf(1000),12, RoundingMode.HALF_UP);
        BigDecimal applianceConsumeKwh = wattToKw.multiply(BigDecimal.valueOf(intervalSeconds)).divide(BigDecimal.valueOf(3600),12, RoundingMode.HALF_UP);
        BigDecimal applianceConsumeCost = applianceConsumeKwh.multiply(tariffRate);
        // accumulated
        BigDecimal newAccumulateKwh =home.getAccumulatedKwh().add(applianceConsumeKwh) ;
        BigDecimal newAccumulateCost = home.getAccumulatedCost().add(applianceConsumeCost);
        // set
        home.setAccumulatedKwh(newAccumulateKwh);
        home.setAccumulatedCost(newAccumulateCost);
        /*quota kontrol*/
        BigDecimal homeBudget = entry.getValue().getBudgetLimit();
        BigDecimal accumulatedCost = entry.getValue().getAccumulatedCost();
        BigDecimal percent80ofHomeBudget = (homeBudget.multiply(BigDecimal.valueOf(80))).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_EVEN);

        boolean alert80Previous = home.isAlert80Sent();
        boolean alert100Previous = home.isAlert100Sent();
        /*toplam kullanım 80 veya 100 e geldiği zaman ignite de alert 80 true oluyor uyarıldı anlamında
        * daha önceden true ise if e hiç girmiyor
        * */
        if(percent80ofHomeBudget.compareTo(accumulatedCost) <= 0 && !entry.getValue().isAlert80Sent()) {
            home.setAlert80Sent(true);
            home.setQuotaVersion(home.getQuotaVersion()+1); // 0 dan 1 e çıkıcak 1 kilit değerimiz
            // burdan kesin 1 olarak çıkıyor eğer kota kısıtlaması olacaksa
        }
        if(homeBudget.compareTo(accumulatedCost) <= 0 && !entry.getValue().isAlert100Sent()) {
            home.setAlert100Sent(true);
            if(home.getQuotaVersion() == 0 || home.getQuotaVersion() ==1){
                home.setQuotaVersion(1);
            }
            home.setActiveTariff(ActiveTariff.PENALTY);
        }
        /*anamoly events control*/
        entry.setValue(home);

    return new ProcessResponse(home.getHomeId(),newAccumulateKwh,newAccumulateCost,home.isAlert80Sent(), home.isAlert100Sent(),alert80Previous,alert100Previous,home.getQuotaVersion());}
}
