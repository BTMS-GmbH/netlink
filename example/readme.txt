Key store was generated with:
keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass changeit -validity 3650 -keysize 2048
keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.jks -deststoretype pkcs12
