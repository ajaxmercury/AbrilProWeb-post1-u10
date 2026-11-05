# Carpeta de Capturas de Pantalla de Evidencia - U10

Este directorio aloja las 4 evidencias graficas requeridas por la rubrica de evaluacion:

1. **`jacoco-reporte.png`**: Captura de pantalla del reporte HTML generado por JaCoCo (`target/site/jacoco/index.html`) evidenciando una cobertura de lineas superior al 70% (obtenido 91.38%).
2. **`jacoco-build-fallido.png`**: Captura de la terminal donde se evidencia la ejecucion de `mvn clean verify` fallando deliberadamente al elevar temporalmente el umbral a 95% o al omitir pruebas (`[ERROR] Coverage checks have not been met`).
3. **`tests-verde.png`**: Captura de la terminal con la ejecucion de `mvn test` o `mvn clean verify` mostrando todos los 21 tests en verde (`BUILD SUCCESS`, 0 failures, 0 errors).
4. **`postman-runner.png`**: Captura de la interfaz de Postman Runner ejecutando `ColeccionCatalogo.json` contra `env-local.json` mostrando los 4 requests ejecutados y las 14 aserciones pasando al 100% (0 failures).
