package com.gzhennaxia.financial.client.fred;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FRED 单日观测点。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
public record FredObservationPoint(LocalDate date, BigDecimal value) {
}
