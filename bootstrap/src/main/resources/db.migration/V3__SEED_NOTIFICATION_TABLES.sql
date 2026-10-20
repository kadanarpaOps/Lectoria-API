INSERT INTO T_NOTIFICATION_TEMPLATES
(
    NOTIFICATION_TEMPLATE_ID,
    NOTIFICATION_TEMPLATE_NAME,
    NOTIFICATION_TEMPLATE_SUBJECT,
    NOTIFICATION_TEMPLATE_LOCATION,
    NOTIFICATION_TEMPLATE_CREATED_AT
)
VALUES
    (
        gen_random_uuid(),
        'SUCCESS_REGISTRY',
        'Bienvenido a Lectoria!',
        '/welcome',
        current_date
    ),
    (
        gen_random_uuid(),
        'OTP_VERIFICATION',
        'Tu Código de Verificación',
        '/otpVerification',
        current_date
    ),
    (
        gen_random_uuid(),
        'SUCCESS_UPDATE_INFORMATION',
        'Se han modificado tus datos satisfactoriamente',
        '/updateInformationAlert',
        current_date
    )
ON CONFLICT (NOTIFICATION_TEMPLATE_NAME) DO NOTHING;
