# Bill Counter

Este pequeño proyecto se encarga de contabilizar las facturas que hay entre dos fechas utilizando el servicio ubicado en http://34.209.24.195/facturas

## Descripción de la implementación

Se implementa una clase que se encarga de realizar peticiones web simples para poder obtener los resultados del servicio web.
También se implementa la clase que desarrolla el algoritmo para partir las fechas y hacer el menor número de peticiones posibles, simplemente realiza la petición y si no encuentra un resultado concreto, parte el periodo entre las dos fechas a la mitad, realizando 2 peticiones más para obtener los resultados entre dos periodos con la mitad de días.

También se implenta una clase que realiza las peticiones en periodos fijos de días.

## Utilización

En la carpeta release se encuentra el jar compilado, se debe ejecutar con el comando: java -jar facturas.jar <id> <start> <finish>

En el archivo de configuración billcounter.properties puedes establecer qué implementación usar (recursive, fixed) con la propiedad billcounter.implementation y el tamaño del periodo fijo (en días) de la implementación de periodo fijo con la propiedad billcounter.fixed.size .

## Autor

**Héctor Enrique Díaz Hernández**

