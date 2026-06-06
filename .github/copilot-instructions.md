# Descripción de la actividad

En esta tercera semana deberán realizar de manera grupal (2 integrantes) la actividad
sumativa de la Experiencia 1, llamada "Construyendo una solución Cloud Native completa
considerando la gestión de usuario y componentes”. Para la primera tendrán que entregar en
un archivo comprimido (.rar o .zip) los componentes creados con el código fuente de la
aplicación para el BackEnd. Para la segunda parte deberán hacer una presentación, a través
de un video para mostrar el correcto funcionamiento de sus microservicios, ejecución y
pruebas mediante POSTMAN, así como verificación de que la información este siendo
manipulada y almacenada en una Base de Datos Oracle.

## Instrucciones específicas

Los microservicios creados deben cumplir con los siguientes aspectos:
• Deben ser desarrollados bajo el Framework Spring con spring boot.
• Manejo y organización del utilizando herramientas colaborativas y de repositorios
en GIT.
• Los microservicios deben usar una conexión a una Base de Datos en Oracle donde
estén construidas sus respectivas tablas para manipular su información.
• Debe implementar todos los controladores necesarios para manipular las
comunicaciones con RESTful (get,post,put,delete).
• Debe manipular POSTMAN para poder validar el correcto funcionamiento de las
consultar.

Para la creación del sistema deberán tener en cuenta lo siguiente en el caso asignado.

Caso
“Desarrollar un sistema de alertas médicas en tiempo real para un hospital que gestione
señales vitales de pacientes críticos.”. Corresponde a un sistema conformado por un
componente frontend y varios componentes backend.

En este caso solo trabajaremos el backen ya que el frontend se trabajara aparte.

• Backend:
o El sistema debería tener securitizado el backend mediante spring security en
el BFF.
o El sistema deberá contar como mínimo con un componente Microservicio BFF.
• API Manager:
o Todos los endpoints del BFF deberían estar registrados en el API Manager.
4 3. Deberán usar Docker para su desarrollo y deben tomar en consideración al momento
de probar su desarrollo final cambiar las urls de las APIs de comunicación a las que
genera Docker Lab una vez que los hayan subido y puesto a funcionar en el mismo.
