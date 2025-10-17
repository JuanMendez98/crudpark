package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.config.AppConfig;
import com.crudzaso.crudpark.dao.TarifaDAO;
import com.crudzaso.crudpark.model.Tarifa;
import com.crudzaso.crudpark.model.TipoVehiculo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servicio para calcular tarifas de parqueadero
 */
public class TarifaCalculator {
    private final TarifaDAO tarifaDAO;
    private final AppConfig config;

    public TarifaCalculator() {
        this.tarifaDAO = new TarifaDAO();
        this.config = AppConfig.getInstance();
    }

    public BigDecimal calcularMonto(int minutosEstadia, TipoVehiculo tipoVehiculo) {
        int tiempoGracia = config.getTiempoGracia();

        if (minutosEstadia <= tiempoGracia) {
            return BigDecimal.ZERO;
        }

        Tarifa tarifa = tarifaDAO.findActivaByTipoVehiculo(tipoVehiculo);

        if (tarifa == null) {
            System.err.println("No se encontró tarifa activa para: " + tipoVehiculo);
            return BigDecimal.ZERO;
        }

        int minutosACobrar = minutosEstadia - tiempoGracia;

        BigDecimal horas = BigDecimal.valueOf(minutosACobrar)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);

        BigDecimal monto = tarifa.getValorHora().multiply(horas);

        return monto.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean aplicaTiempoGracia(int minutosEstadia) {
        return minutosEstadia <= config.getTiempoGracia();
    }
}
