package com.inventario.catalogo_productos.service;

import com.inventario.catalogo_productos.model.entity.Configuracion;
import com.inventario.catalogo_productos.repository.ConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionRepository repo;

    public String getValor(String clave) {
        return repo.findByClave(clave).map(Configuracion::getValor).orElse(null);
    }

    public void setValor(String clave, String valor) {
        Configuracion config = repo.findByClave(clave).orElse(new Configuracion());
        config.setClave(clave);
        config.setValor(valor);
        repo.save(config);
    }
}
