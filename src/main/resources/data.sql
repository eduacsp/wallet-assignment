INSERT INTO wallet_user (id, name, cpf_cnpj)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Usuário Teste', '12345678901');

INSERT INTO wallet (id, user_id, created_at)
VALUES (
  'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
  'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  CURRENT_TIMESTAMP
);
