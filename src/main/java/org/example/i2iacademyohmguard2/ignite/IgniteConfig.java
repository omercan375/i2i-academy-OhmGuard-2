package org.example.i2iacademyohmguard2.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import org.example.i2iacademyohmguard2.ignite.appliance.ApplianceLiveState;
import org.example.i2iacademyohmguard2.ignite.home.HomeLiveState;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.UUID;


/**
 * Lokal, tek-node bir Apache Ignite ornegini ayaga kaldiran minimum yapilandirma.
 *
 * Iki canli-durum cache'i tanimlanir (her ikisi de PARTITIONED + ATOMIC):
 * <ul>
 *   <li>{@link #APPLIANCE_LIVE_STATE_CACHE} — Cache&lt;UUID applianceId, {@link ApplianceLiveState}&gt;</li>
 *   <li>{@link #HOME_LIVE_STATE_CACHE} — Cache&lt;UUID homeId, {@link HomeLiveState}&gt;</li>
 * </ul>
 * Multicast discovery kapatilarak (statik localhost VM ip finder) gelistirme
 * ortaminda deterministik/izole bir node saglanir.
 */
@Configuration
public class IgniteConfig {

    /** Key = applianceId (UUID), Value = {@link ApplianceLiveState}. */
    public static final String APPLIANCE_LIVE_STATE_CACHE = "applianceLiveStateCache";

    /** Key = homeId (UUID), Value = {@link HomeLiveState}. */
    public static final String HOME_LIVE_STATE_CACHE = "homeLiveStateCache";

    @Bean(destroyMethod = "close")
    public Ignite ignite() {
        return Ignition.start(igniteConfiguration());
    }

    /**
     * Uretim bean'i ve testler tarafindan paylasilan tekil yapilandirma noktasi.
     */
    public static IgniteConfiguration igniteConfiguration() {
        CacheConfiguration<UUID, ApplianceLiveState> applianceCfg =
                partitionedAtomicCache(APPLIANCE_LIVE_STATE_CACHE);

        CacheConfiguration<UUID, HomeLiveState> homeCfg =
                partitionedAtomicCache(HOME_LIVE_STATE_CACHE);

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));

        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setIpFinder(ipFinder);

        return new IgniteConfiguration()
                .setIgniteInstanceName("ohmguard-ignite")
                .setClientMode(false)
                .setPeerClassLoadingEnabled(false)
                .setMetricsLogFrequency(0)
                .setDiscoverySpi(discoverySpi)
                .setCacheConfiguration(applianceCfg, homeCfg);
    }

    /**
     * Key'i UUID olan PARTITIONED + ATOMIC bir cache yapilandirmasi uretir.
     * Iki cache de ayni ayarlari paylastigi icin tekrar bu yardimcida toplandi.
     */
    private static <V> CacheConfiguration<UUID, V> partitionedAtomicCache(String cacheName) {
        return new CacheConfiguration<UUID, V>(cacheName)
                .setCacheMode(CacheMode.PARTITIONED)
                .setAtomicityMode(CacheAtomicityMode.ATOMIC);
    }
}