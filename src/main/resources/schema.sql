CREATE TABLE wallet_user (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE wallet (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES wallet_user(id)
);

CREATE TABLE transaction (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN
    related_wallet_id UUID,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallet(id),
    CONSTRAINT fk_transaction_related_wallet FOREIGN KEY (related_wallet_id) REFERENCES wallet(id)
);

CREATE INDEX idx_transaction_wallet_id ON transaction(wallet_id);
CREATE INDEX idx_transaction_created_at ON transaction(created_at);
