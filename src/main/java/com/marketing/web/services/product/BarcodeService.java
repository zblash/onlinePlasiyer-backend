package com.marketing.web.services.product;

import com.marketing.web.models.Barcode;
import com.marketing.web.models.Product;

import java.util.List;

public interface BarcodeService {

    List<Barcode> findAll();

    List<Barcode> findByProduct(Product product);

    Barcode findByProductAndBarcodeNo(Product product, String barcodeNo);

    Barcode findByBarcodeNo(String barcodeNo);

    Barcode checkByBarcodeNo(String barcodeNo);

    Barcode findById(Long id);

    Barcode findByUuid(String uuid);

    Barcode create(Barcode barcode);

    Barcode update(String uuid,Barcode updatedBarcode);

    void delete(Barcode barcode);
}
