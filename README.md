# 📦 WMS Backend - Warehouse Management System

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)](https://www.docker.com/)
[![Status](https://img.shields.io/badge/Status-Funcional%20%7C%20En%20Desarrollo-yellow?style=flat-square)](#-estado-del-proyecto)

> Proyecto práctico de desarrollo Backend enfocado en solucionar problemas reales de gestión de almacenes e inventarios.

---

## 🎯 Sobre el Proyecto

Como **Desarrollador Java Backend Junior** con experiencia de campo en almacenes y logística, creé este proyecto para aplicar mis conocimientos técnicos en un caso de uso real. 

El objetivo es construir un sistema WMS (*Warehouse Management System*) desde cero. **Actualmente la base del proyecto está completamente funcional y ejecutable**, y lo continúo **DESARROLLANDO** de forma activa para sumar nuevas características y mejorar mi dominio del framework.

---

## 🛠️ Tecnologías y Aprendizajes Aplicados

- **Lenguaje principal:** Java 21
- **Framework Backend:** Spring Boot 3
- **Base de Datos & ORM:** Spring Data JPA / Hibernate con postgreSQL
- **Despliegue Local:** Docker & Docker Compose (para la base de datos y entorno)
- **Herramientas de desarrollo:** Git, Maven, Postman e IntelliJ IDEA

---

## ✨ Lo que ya está Funcional y Construido

- [x] **Configuración e Infraestructura:** Entorno local contenerizado con Docker Compose listo para ejecutar.
- [x] **Modelo de Datos Relacional:** Diseño de tablas y entidades para productos, ubicaciones y movimientos de stock.
- [x] **Endpoints CRUD:** API funcional para la gestión básica de catálogo e inventarios.
- [x] **Ubicación Jerárquica:** Mapeo básico de posiciones en almacén (Almacén > Pasillo > Rack > Nivel).

---

## 🚧 Estado del Proyecto y Próximos Pasos (Roadmap)

Este proyecto está en constante evolución mientras continúo mi ruta de aprendizaje:

- [x] Arquitectura base y persistencia con Spring Data JPA.
- [x] Integración de base de datos postgreSQL en contenedor Docker.
- [ ] Implementar validaciones avanzadas en los DTOs y manejo global de excepciones.
- [ ] Agregar documentación interactiva de la API con Swagger / OpenAPI.
- [ ] Incorporar autenticación y autorización básica con Spring Security (JWT).
- [ ] Crear módulo para el registro de conteos cíclicos de inventario.

---

## 🚀 Cómo Ejecutar el Proyecto en Local

### Requisitos
- **JDK 21**
- **Docker** y **Docker Compose**
- **Git** y **Maven**

### Pasos
1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/Jariruna/wms-backend.git](https://github.com/Jariruna/wms-backend.git)
   cd wms-backend

**Levantar la base de datos con Docker:**

   ```bash
   docker-compose up -d
   ```
**Ejecutar la aplicación:**

   ```bash
   mvn spring-boot:run
   ```
---
### 👨‍💻 Sobre Mí

José Luis Ninaco Salazar
Desarrollador Java Backend Junior

- **LinkedIn:** [jose-luis-ninaco-salazar](https://linkedin.com/in/jose-luis-ninaco-salazar/)  
- **GitHub:** [@Jariruna](https://github.com/Jariruna)
