# Práctica Cafetería - Paradigmas de Programación

Este repositorio contiene la primera Práctica Especial Cuatrimestral (PECL1) para la asignatura de **Paradigmas de Programación**, curso 2025-26. 

## 📝 Descripción del Proyecto

El proyecto es una aplicación desarrollada en **Java** centrada en la **programación concurrente y distribuida**. Simula el funcionamiento completo de una cafetería mediante el uso de múltiples hilos (*threads*) y acceso a recursos compartidos. Adicionalmente, implementa **Java RMI** (Remote Method Invocation) para la comunicación distribuida entre clientes remotos y la cafetería central.

### 🎭 Entidades principales (Hilos)
- **Cocineros (`Cocinero.java`)**: Se encargan de preparar los pedidos utilizando ingredientes de la despensa.
- **Vendedores (`Vendedor.java`)**: Gestionan la logística entre la despensa y el mostrador para servir a los clientes.
- **Clientes (`Cliente.java`)**: Simulan la entrada de clientes a la cafetería, la solicitud de pedidos en el mostrador y el pago en la caja.

### 🏢 Recursos Compartidos
La correcta sincronización de hilos se realiza gestionando los siguientes recursos compartidos:
- **Despensa (`Despensa.java`)**: Lugar de donde se obtienen los recursos.
- **Mostrador (`Mostrador.java`)**: Punto de intercambio entre los vendedores y los clientes.
- **Caja (`Caja.java`)**: Punto de cobro de los pedidos.
- **Control de Aforos (`ControlAforos.java`)**: Mecanismo para gestionar la capacidad máxima de la cafetería.

### 📡 Arquitectura RMI
El proyecto implementa un servidor RMI (`CafeteriaRemotaImpl.java`) en el puerto 1099. Permite que programas clientes independientes (`ClienteRemoto.java`) puedan visualizar o interactuar con el estado de la cafetería de forma remota, siguiendo la interfaz `ICafeteriaRemota`.

### 🖥️ Interfaz Gráfica (GUI)
Incluye una interfaz desarrollada previsiblemente en Java Swing (generada con NetBeans) a través de `InterfazCafeteria` y `ClienteRemoto.form`, que permite visualizar de forma gráfica y en tiempo real el estado de:
- El aforo actual.
- Los logs de las acciones de cada entidad (`Log.java`).
- El estado de la caja, despensa y mostradores.

## 📁 Estructura del repositorio

- `/PracticaCafeteria/PracticaCafeteria/`: Contiene el proyecto principal de Java estructurado mediante Maven.
- `src/main/java/pdp/practicacafeteria/`: Código fuente de la aplicación.
- `Memoria y código fuente.pdf`: Documento con las especificaciones detalladas y explicación de la solución implementada.
- `Diagrama UML.pdf`: Diagrama de clases que detalla la arquitectura de software de la práctica.

## 🛠️ Tecnologías y Herramientas utilizadas
- **Java SE** (Concurrencia con `Thread`, `Runnable`, utilidades de sincronización).
- **Java RMI** para sistemas distribuidos.
- **Java Swing** para las interfaces de usuario.
- **Apache Maven** para la gestión de dependencias y la construcción del proyecto.

## 👥 Autores
- Mauro Sánchez
- Sergio Pavón
