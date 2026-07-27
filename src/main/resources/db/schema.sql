-- ==========================================================
-- OhmGuard PostgreSQL Schema (DDL)
-- Kalici veri katmani: master kayitlar, finansal defter,
-- olay loglari ve gunluk tuketim snapshot'lari.
-- Canli durum (anlik kWh/maliyet, ihlal sayaclari) Apache
-- Ignite'ta tutulur ve bu semada YER ALMAZ.
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ==========================================================
-- USERS
-- ==========================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE,

    version INTEGER NOT NULL DEFAULT 0
);

-- ==========================================================
-- HOMES
-- ==========================================================

CREATE TABLE homes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    owner_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    contact_email VARCHAR(255) NOT NULL,

    budget_limit NUMERIC(19,2) NOT NULL
        CHECK (budget_limit > 0),

    normal_tariff_rate NUMERIC(19,6) NOT NULL
        CHECK (normal_tariff_rate >= 0),

    penalty_tariff_rate NUMERIC(19,6) NOT NULL
        CHECK (penalty_tariff_rate >= 0),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    version INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_homes_owner
        FOREIGN KEY(owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- APPLIANCES
-- ==========================================================

CREATE TABLE appliances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    home_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    safe_watt_limit NUMERIC(19,2) NOT NULL
        CHECK (safe_watt_limit > 0),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_appliances_home
        FOREIGN KEY(home_id)
        REFERENCES homes(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- BILLING ACCOUNTS
-- ==========================================================

CREATE TABLE billing_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    home_id UUID NOT NULL UNIQUE,

    accumulated_energy_kwh NUMERIC(19,6)
        NOT NULL DEFAULT 0
        CHECK (accumulated_energy_kwh >= 0),

    accumulated_cost NUMERIC(19,2)
        NOT NULL DEFAULT 0
        CHECK (accumulated_cost >= 0),

    active_tariff VARCHAR(20)
        NOT NULL DEFAULT 'NORMAL'
        CHECK (active_tariff IN ('NORMAL','PENALTY')),

    updated_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_billing_home
        FOREIGN KEY(home_id)
        REFERENCES homes(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- CONSUMPTION SNAPSHOTS
-- ==========================================================

CREATE TABLE consumption_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    home_id UUID NOT NULL,

    energy_kwh NUMERIC(19,6)
        NOT NULL
        CHECK (energy_kwh >= 0),

    total_cost NUMERIC(19,2)
        NOT NULL
        CHECK (total_cost >= 0),

    type VARCHAR(20)
        NOT NULL
        CHECK (type IN ('MONTHLY','DAILY')),

    recorded_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_snapshot_home
        FOREIGN KEY(home_id)
        REFERENCES homes(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- QUOTA EVENTS
-- ==========================================================

CREATE TABLE quota_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    home_id UUID NOT NULL,

    threshold_percentage INTEGER
        NOT NULL
        CHECK (threshold_percentage IN (80,100)),

    budget_limit NUMERIC(19,2)
        NOT NULL
        CHECK (budget_limit > 0),

    accumulated_cost NUMERIC(19,2)
        NOT NULL
        CHECK (accumulated_cost >= 0),

    occurred_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_quota_home
        FOREIGN KEY(home_id)
        REFERENCES homes(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- TARIFF EVENTS
-- ==========================================================

CREATE TABLE tariff_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    home_id UUID NOT NULL,

    previous_tariff VARCHAR(20)
        NOT NULL
        CHECK (previous_tariff IN ('NORMAL','PENALTY')),

    new_tariff VARCHAR(20)
        NOT NULL
        CHECK (new_tariff IN ('NORMAL','PENALTY')),

    activated_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_tariff_change
        CHECK (previous_tariff <> new_tariff),

    CONSTRAINT fk_tariff_home
        FOREIGN KEY(home_id)
        REFERENCES homes(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- ANOMALY EVENTS
-- ==========================================================

CREATE TABLE anomaly_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    home_id UUID NOT NULL,

    appliance_id UUID NOT NULL,

    measured_watt NUMERIC(19,2)
        NOT NULL
        CHECK (measured_watt >= 0),

    safe_watt_limit NUMERIC(19,2)
        NOT NULL
        CHECK (safe_watt_limit > 0),

    breach_count INTEGER
        NOT NULL
        CHECK (breach_count >= 3),

    detected_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_anomaly_home
        FOREIGN KEY(home_id)
        REFERENCES homes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_anomaly_appliance
        FOREIGN KEY(appliance_id)
        REFERENCES appliances(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- AI RECOMMENDATIONS
-- ==========================================================

CREATE TABLE ai_recommendations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    home_id UUID NOT NULL,

    appliance_id UUID,

    trigger_type VARCHAR(30)
        NOT NULL
        CHECK (
            trigger_type IN
            (
                'QUOTA_80',
                'QUOTA_100',
                'ANOMALY'
            )
        ),

    recommendation_text TEXT NOT NULL,

    generated_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ai_home
        FOREIGN KEY(home_id)
        REFERENCES homes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ai_appliance
        FOREIGN KEY(appliance_id)
        REFERENCES appliances(id)
        ON DELETE SET NULL
);

-- ==========================================================
-- EMAIL NOTIFICATIONS
-- ==========================================================

CREATE TABLE email_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    recommendation_id UUID NOT NULL,

    recipient_email VARCHAR(255) NOT NULL,

    status VARCHAR(20)
        NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING','SENT','FAILED')),

    sent_at TIMESTAMP WITH TIME ZONE,

    failure_reason TEXT,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_email_recommendation
        FOREIGN KEY(recommendation_id)
        REFERENCES ai_recommendations(id)
        ON DELETE CASCADE
);

-- ==========================================================
-- INDEXES
-- ==========================================================

CREATE INDEX idx_homes_owner
ON homes(owner_id);

CREATE INDEX idx_appliances_home
ON appliances(home_id);

CREATE INDEX idx_billing_home
ON billing_accounts(home_id);

CREATE INDEX idx_snapshot_home
ON consumption_snapshots(home_id);

CREATE INDEX idx_snapshot_recorded
ON consumption_snapshots(recorded_at DESC);

CREATE INDEX idx_quota_home
ON quota_events(home_id);

CREATE INDEX idx_tariff_home
ON tariff_events(home_id);

CREATE INDEX idx_anomaly_home
ON anomaly_events(home_id);

CREATE INDEX idx_anomaly_appliance
ON anomaly_events(appliance_id);

CREATE INDEX idx_ai_home
ON ai_recommendations(home_id);

CREATE INDEX idx_email_status
ON email_notifications(status);
