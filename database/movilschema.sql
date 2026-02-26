-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-02-2026 a las 17:42:14
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `movil`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consultas_log`
--

CREATE TABLE `consultas_log` (
  `id` int(11) NOT NULL,
  `id_movil` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `fecha_consulta` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `moviles`
--

CREATE TABLE `moviles` (
  `id` int(11) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `modelo` varchar(100) NOT NULL,
  `cpu_tipo` varchar(50) DEFAULT NULL,
  `cpu_nucleos` int(11) DEFAULT NULL,
  `cpu_ghz` decimal(4,2) DEFAULT NULL,
  `almacenamiento_gb` int(11) DEFAULT NULL,
  `ram_gb` int(11) NOT NULL,
  `pantalla_pulgadas` decimal(4,2) DEFAULT NULL,
  `pantalla_tecnologia` varchar(50) DEFAULT NULL,
  `dim_alto` decimal(5,2) DEFAULT NULL,
  `dim_ancho` decimal(5,2) DEFAULT NULL,
  `dim_grosor` decimal(5,2) DEFAULT NULL,
  `peso_gr` int(11) DEFAULT NULL,
  `camara_capacidad` varchar(100) DEFAULT NULL,
  `bateria_mah` int(11) DEFAULT NULL,
  `nfc` tinyint(1) DEFAULT 0,
  `precio` decimal(10,2) NOT NULL,
  `fecha_lanzamiento` date DEFAULT NULL,
  `consultas` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre_usuario` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` enum('admin','usuario','invitado') DEFAULT 'invitado'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `consultas_log`
--
ALTER TABLE `consultas_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_movil_consulta` (`id_movil`);

--
-- Indices de la tabla `moviles`
--
ALTER TABLE `moviles`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre_usuario` (`nombre_usuario`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `consultas_log`
--
ALTER TABLE `consultas_log`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `moviles`
--
ALTER TABLE `moviles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `consultas_log`
--
ALTER TABLE `consultas_log`
  ADD CONSTRAINT `fk_movil_consulta` FOREIGN KEY (`id_movil`) REFERENCES `moviles` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
