INSERT INTO T_ROLES (ROLE_ID, ROLE_NAME) VALUES
    (gen_random_uuid(), 'LECTORIA_ADMIN'),
    (gen_random_uuid(), 'LECTORIA_LECTOR'),
    (gen_random_uuid(), 'LECTORIA_WRITER')
ON CONFLICT (ROLE_NAME) DO NOTHING;
